package co.nz.leonhardt.bpe.reco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.CategoricalEnum;
import co.nz.leonhardt.bpe.processing.CategoricalMetricExtractor;
import co.nz.leonhardt.bpe.processing.NumericalMetricExtractor;

/**
 * Generic data extraction factory.
 * 
 * @author freddy
 *
 * @param <P> type of the data point
 * @param <S> type of the data set
 */
public abstract class DataExtractionFactory<P,S> {

	/** Numerical extractors used on the trace */
	protected final List<NumericalMetricExtractor<? extends Number>> numericalMetrics;
	
	/** Categorical extractors used on the trace */
	protected final List<CategoricalMetricExtractor<? extends CategoricalEnum>> categoricalMetrics;

	/**
	 * Creates a new data extraction factory. Internal use only,
	 * use static create() method and with*() methods.
	 */
	protected DataExtractionFactory() {
		numericalMetrics = new ArrayList<>();
		categoricalMetrics = new ArrayList<>();
	}
		
	/**
	 * Adds the given numeric extractors to the list of extractors.
	 * 
	 * @param numExts
	 * @return the data point factory itself (happy chaining!)
	 */
	public DataExtractionFactory<P,S> withNumerics(NumericalMetricExtractor<?>... numExts) {
		numericalMetrics.addAll(Arrays.asList(numExts));
		return this;
	}
	
	/**
	 * Adds the given categorical extractors to the list of extractors.
	 * 
	 * @param catExts
	 * @return the data point factory itself
	 */
	public DataExtractionFactory<P,S> withCategories(CategoricalMetricExtractor<?>... catExts) {
		categoricalMetrics.addAll(Arrays.asList(catExts));
		return this;
	}
	
	/**
	 * Extracts a data point from the given trace.
	 * This is a sample of variables of one case.
	 * 
	 * @param trace
	 * @return
	 */
	public abstract P extractDataPoint(XTrace trace);
	
	/**
	 * Extracts a data set from the given log.
	 * This is a set of samples of variables for each case.
	 * 
	 * @param log
	 * @return
	 */
	public abstract S extractDataSet(XLog log);

	/**
	 * Extracts all numerical values from a trace.
	 * They will be ordered according to the input of the numerical extractors.
	 * 
	 * @param trace
	 * @return
	 */
	protected double[] extractNumericalValues(XTrace trace) {
		double[] numericalValues = new double[numericalMetrics.size()];

		int i = 0;
		for (NumericalMetricExtractor<? extends Number> ex : numericalMetrics) {
			Number numericalMetric = ex.extractMetric(trace);
			numericalValues[i++] = numericalMetric.doubleValue();
		}
		
		return numericalValues;
	}
	
	/**
	 * Extracts all categorical values from a trace.
	 * They will be ordered according to the input of the categorical extractors.
	 * 
	 * @param trace
	 * @return
	 */
	protected CategoricalEnum[] extractCategoricalValues(XTrace trace) {
		CategoricalEnum[] categoricalValues = new CategoricalEnum[categoricalMetrics.size()];
		
		int i = 0;
		for (CategoricalMetricExtractor<? extends CategoricalEnum> ex : categoricalMetrics) {
			CategoricalEnum cat = ex.extractMetric(trace);
			categoricalValues[i++] = cat;
		}
		
		return categoricalValues;
	}
}
