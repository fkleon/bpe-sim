package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.NominalValue;

/**
 * Interface for a categorical metric extractor.
 * 
 * @author freddy
 *
 * @param <T>
 */
public interface CategoricalMetricExtractor<T extends NominalValue> extends MetricExtractor<T> {

	public T extractMetric(XTrace trace);
	
	public int getNumCategories();
	
	public T[] getCategories();
}
