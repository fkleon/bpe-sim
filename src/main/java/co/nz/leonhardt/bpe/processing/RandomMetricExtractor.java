package co.nz.leonhardt.bpe.processing;

import java.util.Random;

import org.deckfour.xes.model.XTrace;

/**
 * Returns the bias term for logistic regression.
 * 
 * Always 1.0.
 * 
 * @author freddy
 *
 */
public class RandomMetricExtractor extends NumericalMetricExtractor<Double> {

	private final Random rand;
	
	public RandomMetricExtractor() {
		this.rand = new Random();
	}
	
	@Override
	public Double extractMetric(XTrace trace) {
		return rand.nextDouble();
	}

	@Override
	public String getMetricName() {
		return "Random";
	}

}
