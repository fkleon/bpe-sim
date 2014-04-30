package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XTrace;

/**
 * Interface for a metric extractor.
 * 
 * @author freddy
 *
 * @param <T>
 */
public interface MetricExtractor<T> {

	/**
	 * Extracts the metric of type T from the given trace.
	 * 
	 * @param trace
	 * @return
	 */
	public T extractMetric(XTrace trace);
	
	/**
	 * Returns the name of the metric produced by this extractor.
	 * 
	 * @return
	 */
	public String getMetricName();
}
