package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.Outcome;

/**
 * Extracts the outcome of a case.
 * 
 * @author freddy
 *
 */
public class OutcomeExtractor implements
		CategoricalMetricExtractor<Outcome> {

	@Override
	public Outcome extractMetric(XTrace trace) {
		// look at the last event
		XEvent lastEvent = trace.get(trace.size()-1);
		XAttribute attr = lastEvent.getAttributes().get("OUTCOME");
		String outcome = attr == null ? "undecided" : attr.toString();
		return Outcome.fromString(outcome);
	}
}
