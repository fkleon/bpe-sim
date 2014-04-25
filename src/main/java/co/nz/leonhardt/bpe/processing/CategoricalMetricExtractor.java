package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.CategoricalEnum;

/**
 * Interface for a categorical metric extractor.
 * 
 * @author freddy
 *
 * @param <T>
 */
public interface CategoricalMetricExtractor<T extends CategoricalEnum> {

	public T extractMetric(XTrace trace);
}
