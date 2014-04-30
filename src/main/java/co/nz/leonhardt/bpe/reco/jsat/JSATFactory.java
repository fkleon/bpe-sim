package co.nz.leonhardt.bpe.reco.jsat;

import java.util.ArrayList;
import java.util.List;

import jsat.DataSet;
import jsat.SimpleDataSet;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import jsat.linear.Vec;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.NominalValue;
import co.nz.leonhardt.bpe.processing.CategoricalMetricExtractor;
import co.nz.leonhardt.bpe.reco.DataExtractionFactory;

/**
 * Extracts metrics from logs and traces.
 * 
 * Produces {@link DataPoint} and {@link DataSet} for the use with JSAT.
 * 
 * @author freddy
 *
 */
public class JSATFactory extends DataExtractionFactory<DataPoint, DataSet>{

	/**
	 * Creates a new JSATFactory factory. Internal use only,
	 * use static create() method and with*() methods.
	 */
	private JSATFactory() {
		super();
	}
	
	/**
	 * Creates a new DataPointFactory.
	 * 
	 * @return the data point factory
	 */
	public static JSATFactory create() {
		return new JSATFactory();
	}

	/**
	 * The {@link JSATFactory} inserts the class value as first entry of the
	 * categorical metric extractors.
	 */
	@Override
	public DataExtractionFactory<DataPoint, DataSet> withClass(CategoricalMetricExtractor<?> classExt) {
		/*
		 * JSAT considers the class value just another categorical value, and the index
		 * is given to the CategoricalDataSet to mark it as such.
		 * 
		 * We need to make sure that the class value is always the first categorical value
		 * in the data set.
		 */
		if (categoricalMetrics.isEmpty()) {
			categoricalMetrics.add(classExt);
		} else {
			categoricalMetrics.add(0, classExt);
		}
		return this;
	}

	@Override
	public DataPoint extractDataPoint(XTrace trace) {

		// numerical values
		double[] numericalValues = extractNumericalValues(trace);
		Vec numericalVec = new DenseVector(numericalValues);

		// categorical values
		NominalValue[] categoricalEnums = extractNominalValues(trace);
		int[] categoricalValues = new int[categoricalEnums.length];
		CategoricalData[] categorialData = new CategoricalData[categoricalEnums.length];

		int j = 0;
		for (NominalValue cat : categoricalEnums) {
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

	@Override
	public DataSet extractDataSet(XLog log) {
		List<DataPoint> data = new ArrayList<>(log.size());
		
		for(XTrace trace: log) {
			DataPoint dp = extractDataPoint(trace);
			data.add(dp);
		}
		
		return new SimpleDataSet(data);
	}

}
