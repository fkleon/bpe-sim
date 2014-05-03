package co.nz.leonhardt.reco.weka;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import co.nz.leonhardt.bpe.categories.NominalValue;
import co.nz.leonhardt.bpe.processing.CategoricalMetricExtractor;
import co.nz.leonhardt.bpe.processing.NumericalMetricExtractor;
import co.nz.leonhardt.reco.DataExtractionFactory;

public class WekaNumericalFactory extends WekaFactory {
	
	/**
	 * Creates a new WekaFactory factory. Internal use only,
	 * use static create() method and with*() methods.
	 */
	private WekaNumericalFactory() {
		super();
	}
	
	/**
	 * Creates a new DataPointFactory.
	 * 
	 * @return the data point factory
	 */
	public static WekaNumericalFactory create() {
		return new WekaNumericalFactory();
	}
	
	
	private NumericalMetricExtractor<?> numClassMetric;
	
	@Override
	public DataExtractionFactory<Instance, Instances> withClass(CategoricalMetricExtractor<?> catExt) {
		throw new IllegalArgumentException("Factory is only for numerical targets");
	}
	
	/**
	 * Sets the numerical class extractor.
	 * 
	 * @param classExt
	 * @return
	 * @throws IllegalStateException if nominal class extractor is set already
	 */
	public DataExtractionFactory<Instance, Instances> withClass(NumericalMetricExtractor<?> classExt) {
		if(classMetric != null) {
			throw new IllegalStateException("Can either predict categorial or numerical class.");
		}
		
		numClassMetric = classExt;
		return this;
	}

	@Override
	protected Attribute getClassAttribute() {
		return new Attribute(numClassMetric.getMetricName());
	}

	@Override
	public Instance extractDataPoint(XTrace trace) {
		Instances dataSet = new Instances("DataSet", allAttrs, 0);
		//dataSet.setClassIndex(classAttr.index());
		
		// numerics + categories
		Instance traceData = new DenseInstance(numericalMetrics.size() + categoricalMetrics.size() + 1); // size +1 here
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
		
		//System.out.println("INSTANCE:");
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
			
			Number numClassVal = numClassMetric.extractMetric(trace);
			traceData.setClassValue(numClassVal.doubleValue());
			
			// Add instance to data set
			dataSet.add(traceData);
		}
		
		//System.out.println("INSTANCES:");
		//System.out.println(dataSet);
	
		return dataSet;
	}
}
