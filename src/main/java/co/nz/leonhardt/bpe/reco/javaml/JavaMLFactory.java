package co.nz.leonhardt.bpe.reco.javaml;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.CategoricalEnum;
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
		if(catExts != null && catExts.length > 1) {
			throw new IllegalArgumentException("This factory only supports one class attribute.");
		}
		return super.withCategories(catExts);
	}


	@Override
	public Instance extractDataPoint(XTrace trace) {

		// numerical values
		double[] numericalValues = extractNumericalValues(trace);

		// categorical values
		CategoricalEnum cat = null;
		if(!categoricalMetrics.isEmpty()) {
			CategoricalMetricExtractor<? extends CategoricalEnum> classExtractor = categoricalMetrics.get(0);
			cat = classExtractor.extractMetric(trace);
		}
		
		if (cat == null) {
			return new DenseInstance(numericalValues);
		} else {
			return new DenseInstance(numericalValues, cat);
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
