package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XTrace;

/**
 * Returns the length of the givne trace.
 * 
 * @author freddy
 *
 */
public class TraceLengthExtractor implements NumericalMetricExtractor<Integer> {

	@Override
	public Integer extractMetric(XTrace trace) {
		return trace.size();
	}

	@Override
	public String getNumericName() {
		return "TraceLength";
	}

}
