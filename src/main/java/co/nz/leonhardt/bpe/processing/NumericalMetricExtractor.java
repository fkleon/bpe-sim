package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XTrace;

/**
 * 
 * @author freddy
 *
 * @param <T>
 */
public interface NumericalMetricExtractor<T extends Number> extends MetricExtractor<T> {

	public T extractMetric(XTrace trace);
	
	/**
	 * Returns the unique name associated with this numeric attribute
	 * @return
	 */
	public String getNumericName();
}
