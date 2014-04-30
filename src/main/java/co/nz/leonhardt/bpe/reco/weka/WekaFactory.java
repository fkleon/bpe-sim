package co.nz.leonhardt.bpe.reco.weka;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import co.nz.leonhardt.bpe.categories.NominalValue;
import co.nz.leonhardt.bpe.processing.CategoricalMetricExtractor;
import co.nz.leonhardt.bpe.processing.MetricExtractor;
import co.nz.leonhardt.bpe.processing.NumericalMetricExtractor;
import co.nz.leonhardt.bpe.reco.DataExtractionFactory;
import co.nz.leonhardt.bpe.util.ArrayListFactory;

/**
 * Extracts metrics from logs and traces.
 * 
 * Produces {@link Instance} and {@link Instances} for the use with Weka.
 * 
 * @author freddy
 *
 */
public class WekaFactory extends DataExtractionFactory<Instance, Instances> {

	/**
	 * Creates a new WekaFactory factory. Internal use only,
	 * use static create() method and with*() methods.
	 */
	private WekaFactory() {
		super();
	}
	
	/**
	 * Creates a new DataPointFactory.
	 * 
	 * @return the data point factory
	 */
	public static WekaFactory create() {
		return new WekaFactory();
	}
	
	/*
	 * Store for attribute objects.
	 */
	/** The numerical attributes */
	private ArrayList<Attribute> numAttrs = new ArrayList<>();
	/** The nominal attributes */
	private ArrayList<Attribute> nomAttrs = new ArrayList<>();
	/** The class (=target) attribute */
	private Attribute classAttr = null;
	/** All attributes */
	private ArrayList<Attribute> allAttrs = new ArrayList<>();
	
	/**
	 * Updates the attribute store.
	 */
	private void buildAttributes() {
		numAttrs = getNumericalAttributes(numericalMetrics);
		nomAttrs = getNominalAttributes(categoricalMetrics);
		classAttr = getClassAttribute(classMetric);
		allAttrs = 	ArrayListFactory.emptyOf(Attribute.class)
					.withAll(numAttrs)
					.withAll(nomAttrs)
					.with(classAttr)
					.build();
	}
	
	@Override
	public DataExtractionFactory<Instance, Instances> withNumerics(NumericalMetricExtractor<?>... numExts) {
		super.withNumerics(numExts);
		buildAttributes();
		return this;
	}
	
	@Override
	public DataExtractionFactory<Instance, Instances> withCategories(CategoricalMetricExtractor<?>... catExts) {
		super.withCategories(catExts);
		buildAttributes();
		return this;
	}
	
	@Override
	public DataExtractionFactory<Instance, Instances> withClass(CategoricalMetricExtractor<?> classExt) {
		super.withClass(classExt);
		buildAttributes();
		return this;
	}
	
	@Override
	public Instance extractDataPoint(XTrace trace) {
		Instances dataSet = new Instances("DataSet", allAttrs, 0);
		dataSet.setClassIndex(classAttr.index());
		
		// numerics + categories
		Instance traceData = new DenseInstance(numericalMetrics.size() + categoricalMetrics.size());
		traceData.setDataset(dataSet);
			
		for (int i = 0; i < numericalMetrics.size(); i++) {
			NumericalMetricExtractor<? extends Number> nEx = numericalMetrics.get(i);
			double nVal = nEx.extractMetric(trace).doubleValue();
			Attribute nAttr = numAttrs.get(i);
			traceData.setValue(nAttr, nVal);
		}
			
		for (int i = 0; i < categoricalMetrics.size(); i++) {
			CategoricalMetricExtractor<? extends NominalValue> cEx = categoricalMetrics.get(i);
			NominalValue cVal = cEx.extractMetric(trace);
			Attribute cAttr = nomAttrs.get(i);
			traceData.setValue(cAttr, cVal.toString());
		}
		
		//System.out.println(dataSet);
		//System.out.println(traceData);

		return traceData;
	}

	@Override
	public Instances extractDataSet(XLog log) {
		Instances dataSet = new Instances("DataSet", allAttrs, log.size());		
		dataSet.setClassIndex(classAttr.index());
		
		for(XTrace trace: log) {
			// numerics + categories + 1 class
			Instance traceData = new DenseInstance(numericalMetrics.size() + categoricalMetrics.size() + 1);
			traceData.setDataset(dataSet);
			
			for (int i = 0; i < numericalMetrics.size(); i++) {
				NumericalMetricExtractor<? extends Number> nEx = numericalMetrics.get(i);
				double nVal = nEx.extractMetric(trace).doubleValue();
				Attribute nAttr = numAttrs.get(i);
				traceData.setValue(nAttr, nVal);
			}
			
			for (int i = 0; i < categoricalMetrics.size(); i++) {
				CategoricalMetricExtractor<? extends NominalValue> cEx = categoricalMetrics.get(i);
				NominalValue cVal = cEx.extractMetric(trace);
				Attribute cAttr = nomAttrs.get(i);
				traceData.setValue(cAttr, cVal.toString());
			}
						
			NominalValue classVal = classMetric.extractMetric(trace);
			traceData.setClassValue(classVal.toString());

			// Add instance to data set
			dataSet.add(traceData);
		}
		
		//System.out.println(dataSet);
	
		return dataSet;
	}
	
	/**
	 * Returns all nominal attributes supported by this factory.
	 * 
	 * @param categoricalExtractors
	 * @return
	 */
	private ArrayList<Attribute> getNominalAttributes(Collection<CategoricalMetricExtractor<?>> categoricalExtractors) {
		ArrayList<Attribute> attrs = new ArrayList<>();

		for (CategoricalMetricExtractor<?> ex : categoricalExtractors) {
			List<String> nominalValues = new ArrayList<>(ex.getNumCategories());
			for(Object val: ex.getCategories()) {
				nominalValues.add(val.toString());
			}
			
			attrs.add(new Attribute(ex.getMetricName(), nominalValues));
		}
		
		return attrs;
	}
	
	/**
	 * Returns all numerical attributes supported by this factory.
	 * 
	 * @param metricExtractors
	 * @return
	 */
	private ArrayList<Attribute> getNumericalAttributes(Collection<NumericalMetricExtractor<?>> metricExtractors) {
		ArrayList<Attribute> attrs = new ArrayList<>();
		
		for (MetricExtractor<?> ex : metricExtractors) {
			attrs.add(new Attribute(ex.getMetricName()));
		}
		
		return attrs;
	}
	
	/**
	 * Returns the class attribute.
	 * 
	 * @param ex
	 * @return
	 */
	private Attribute getClassAttribute(CategoricalMetricExtractor<?> ex) {
		List<String> nominalValues = new ArrayList<>(ex.getNumCategories());
		for(Object val: ex.getCategories()) {
			nominalValues.add(val.toString());
		}
		return new Attribute(ex.getMetricName(), nominalValues);
	}
}
