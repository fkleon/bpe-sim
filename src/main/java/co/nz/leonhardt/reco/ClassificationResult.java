package co.nz.leonhardt.reco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A classification result may contain more information than a simple {@link PredictionResult}.
 * E.g. multiple classifications.
 * 
 * @author freddy
 *
 * @param <T>
 */
public class ClassificationResult<T> implements Iterable<PredictionResult<T>> {
	
	/** Sorted by confidence DESC */
	public final List<PredictionResult<T>> results;
	
	/**
	 * Creates a prediction result with the given outcome and confidence.
	 * 
	 * @param result
	 * @param confidence
	 */
	public ClassificationResult(final List<PredictionResult<T>> results) {
		List<PredictionResult<T>> tempResults = new ArrayList<>(results);
		Collections.sort(tempResults, new PredictionResult.ConfidenceComparator());
		this.results = Collections.unmodifiableList(tempResults);
	}
	
	public T getBestResult() {
		return this.results.get(0).result;
	}

	@Override
	public Iterator<PredictionResult<T>> iterator() {
		return results.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
			.append("[Classification Result]").append("\n");
		
		for(int i = 0; i < results.size(); i++) {
			sb.append("#").append(i+1).append(" = ").append(results.get(i)).append("\n");
		};
		
		return sb.toString();
	}
}
