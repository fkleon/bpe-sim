package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XTrace;

/**
 * Extracts the number of loops in a given trace.
 * 
 * @author freddy
 *
 */
public class LoopCountExtractor extends NumericalMetricExtractor<Integer> {

	@Override
	public Integer extractMetric(XTrace trace) {
		// TODO Auto-generated method stub
		
		// TODO SuffixTree
		return null;
	}

	@Override
	public String getMetricName() {
		return "LoopCount";
	}

}
