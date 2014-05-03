package co.nz.leonhardt.bpe.reco.weka;

import java.util.ArrayList;
import java.util.List;


import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
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
public abstract class WekaFactory extends DataExtractionFactory<Instance, Instances> {

	/**
	 * Creates a new WekaFactory factory. Internal use only,
	 * use static create() method and with*() methods.
	 */
	protected WekaFactory() {
		super();
	}
	
	/*
	 * Store for attribute objects.
	 */
	/** The numerical attributes */
	protected ArrayList<Attribute> numAttrs = new ArrayList<>();
	/** The nominal attributes */
	protected ArrayList<Attribute> nomAttrs = new ArrayList<>();
	/** The class (=target) attribute */
	protected Attribute classAttr = null;
	/** All attributes */
	protected ArrayList<Attribute> allAttrs = new ArrayList<>();
	
	/**
	 * Updates the attribute store.
	 */
	protected void buildAttributes() {
		numAttrs = getNumericalAttributes();
		nomAttrs = getNominalAttributes();
		classAttr = classMetric != null ? getClassAttribute() : getClassAttribute();
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
	
	/**
	 * Returns all nominal attributes supported by this factory.
	 * 
	 * @param categoricalExtractors
	 * @return
	 */
	private ArrayList<Attribute> getNominalAttributes() {
		ArrayList<Attribute> attrs = new ArrayList<>();

		for (CategoricalMetricExtractor<?> ex : categoricalMetrics) {
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
	protected ArrayList<Attribute> getNumericalAttributes() {
		ArrayList<Attribute> attrs = new ArrayList<>();
		
		for (MetricExtractor<?> ex : numericalMetrics) {
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
	protected abstract Attribute getClassAttribute();
}
