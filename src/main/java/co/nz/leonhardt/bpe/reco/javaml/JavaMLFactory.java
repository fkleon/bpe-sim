package co.nz.leonhardt.bpe.reco.javaml;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.NominalValue;
import co.nz.leonhardt.bpe.processing.CategoricalMetricExtractor;
import co.nz.leonhardt.bpe.reco.DataExtractionFactory;

/**
 * Extracts metrics from logs and traces.
 * 
 * Produces {@link Instance} and {@link Dataset} for the use with java-ml.
 * 
 * @author freddy
 *
 */
public class JavaMLFactory extends DataExtractionFactory<Instance, Dataset> {

	/**
	 * Creates a new JavaMLFactory. Internal use only,
	 * use static create() method and with*() methods.
	 */
	private JavaMLFactory() {
		super();
	}
	
	/**
	 * Creates a new DataPointFactory.
	 * 
	 * @return the data point factory
	 */
	public static JavaMLFactory create() {
		return new JavaMLFactory();
	}
	
	@Override
	public DataExtractionFactory<Instance, Dataset> withCategories(CategoricalMetricExtractor<?>... catExts) {
		if(catExts == null || catExts.length > 1) {
			throw new IllegalArgumentException("This factory only supports one class attribute.");
		}
		classMetric = catExts[0];		
		return this;
	}


	@Override
	public Instance extractDataPoint(XTrace trace) {

		// numerical values
		double[] numericalValues = extractNumericalValues(trace);

		NominalValue cat = null;
		if(classMetric != null) {
			// extract class
			CategoricalMetricExtractor<? extends NominalValue> classExtractor = classMetric;
			cat = classExtractor.extractMetric(trace);
			return new DenseInstance(numericalValues, cat);
		} else {
			// only numerics
			return new DenseInstance(numericalValues);
		}
	}

	@Override
	public Dataset extractDataSet(XLog log) {
		Dataset data = new DefaultDataset();
		
		for(XTrace trace: log) {
			Instance in = extractDataPoint(trace);
			data.add(in);
		}
		
		return data;
	}

}
