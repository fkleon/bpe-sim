package co.nz.leonhardt.bpe.reco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import jsat.linear.Vec;

import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.CategoricalEnum;
import co.nz.leonhardt.bpe.processing.CategoricalMetricExtractor;
import co.nz.leonhardt.bpe.processing.NumericalMetricExtractor;

/**
 * Extracts metrics from log traces.
 * 
 * @author freddy
 *
 */
public class DataPointFactory {

	/** Numerical extractors used on the trace */
	private final List<NumericalMetricExtractor<? extends Number>> numericalMetrics;
	
	/** Categorical extractors used on the trace */
	private final List<CategoricalMetricExtractor<? extends CategoricalEnum>> categoricalMetrics;

	/**
	 * Creates a new datapoint factory. Internal use only,
	 * use static create() method and with*() methods.
	 */
	private DataPointFactory() {
		numericalMetrics = new ArrayList<>();
		categoricalMetrics = new ArrayList<>();
	}
	
	/**
	 * Creates a new DataPointFactory.
	 * 
	 * @return the data point factory
	 */
	public static DataPointFactory create() {
		return new DataPointFactory();
	}
	
	/**
	 * Adds the given numeric extractors to the list of extractors.
	 * 
	 * @param numExts
	 * @return the data point factory itself (happy chaining!)
	 */
	public DataPointFactory withNumerics(NumericalMetricExtractor<?>... numExts) {
		numericalMetrics.addAll(Arrays.asList(numExts));
		return this;
	}
	
	/**
	 * Adds the given categorical extractors to the list of extractors.
	 * 
	 * @param catExts
	 * @return the data point factory itself
	 */
	public DataPointFactory withCategories(CategoricalMetricExtractor<?>... catExts) {
		categoricalMetrics.addAll(Arrays.asList(catExts));
		return this;
	}

	/**
	 * Extracts a data point out of a trace utilising all extractors
	 * specified before.
	 * 
	 * @param trace
	 * @return a data point representing the trace
	 */
	public DataPoint extractDataPoint(XTrace trace) {

		// numerical values
		double[] numericalValues = new double[numericalMetrics.size()];

		int i = 0;
		for (NumericalMetricExtractor<? extends Number> ex : numericalMetrics) {
			Number numericalMetric = ex.extractMetric(trace);
			numericalValues[i++] = numericalMetric.doubleValue();
		}

		Vec numericalVec = new DenseVector(numericalValues);

		// categorical values
		int[] categoricalValues = new int[categoricalMetrics.size()];
		CategoricalData[] categorialData = new CategoricalData[categoricalMetrics
				.size()];

		int j = 0;
		for (CategoricalMetricExtractor<? extends CategoricalEnum> ex : categoricalMetrics) {
			CategoricalEnum cat = ex.extractMetric(trace);

			int iv = cat.getIntValue();
			CategoricalData cd = cat.getCategoricalData();
			
			categoricalValues[j] = iv;
			categorialData[j++] = cd;
		}
		
		/*
		System.out.println(Arrays.toString(categorialData));
		System.out.println(categorialData[0].getCategoryName());
		System.out.println(categorialData[0].getNumOfCategories());
		System.out.println(categorialData[0].getOptionName(0));
		System.out.println(categorialData[0].getOptionName(1));
		System.out.println(categorialData[0].getOptionName(2));
		System.out.println(Arrays.toString(categoricalValues));
		*/
		
		DataPoint dp = new DataPoint(numericalVec, categoricalValues,
				categorialData);

		return dp;
	}

}
