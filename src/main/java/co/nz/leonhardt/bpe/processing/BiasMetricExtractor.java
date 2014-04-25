package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XTrace;

/**
 * Returns the bias term for logistic regression.
 * 
 * Always 1.0.
 * 
 * @author freddy
 *
 */
public class BiasMetricExtractor implements NumericalMetricExtractor<Double> {

	@Override
	public Double extractMetric(XTrace trace) {
		return 1.0;
	}

	@Override
	public String getNumericName() {
		return "Bias";
	}

}
