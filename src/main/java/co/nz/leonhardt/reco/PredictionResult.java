package co.nz.leonhardt.reco;

import java.util.Comparator;

/**
 * Wrapper for the result of a prediction.
 * 
 * @author freddy
 *
 * @param <T>
 */
public class PredictionResult<T> {

	/** The actual result */
	public final T result;
	
	/** The confidence of the prediction, null if unknown */
	public final Double confidence;
	
	/**
	 * Creates a prediction result with the given outcome and confidence.
	 * 
	 * @param result
	 * @param confidence
	 */
	public PredictionResult(final T result, final double confidence) {
		this.result = result;
		this.confidence = confidence;
	}
	
	/**
	 * Creates a prediction result with uncertain confidence.
	 * 
	 * @param result
	 */
	public PredictionResult(final T result) {
		this.result = result;
		this.confidence = null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("[Prediction Result] ")
			.append("confidence = ").append(confidence).append("; ")
			.append("result = ").append(result);
		
		return sb.toString();
	}
	
	/**
	 * Comparator for prediction results.
	 * Sorts by confidence DESC.
	 * 
	 * @author freddy
	 *
	 */
	protected static class ConfidenceComparator implements Comparator<PredictionResult<?>> {

		@Override
		public int compare(PredictionResult<?> r1, PredictionResult<?> r2) {
			return Double.compare(r2.confidence, r1.confidence);
		}
		
	}
}
