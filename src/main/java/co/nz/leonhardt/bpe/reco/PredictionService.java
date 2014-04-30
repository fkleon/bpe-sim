package co.nz.leonhardt.bpe.reco;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * Generic prediction/recommendation service.
 * 
 * @author freddy
 *
 * @param <T>
 */
public interface PredictionService<T> {

	/**
	 * Learns from the givne log.
	 * 
	 * @param log
	 * @throws Exception if model could not be trained
	 */
	public void learn(XLog log) throws Exception;
	
	/**
	 * Predicts/recommends based on the given trace.
	 * 
	 * @param partialTrace
	 * @return
	 * @throws Exception if trace could not be predicted
	 */
	public T predict(XTrace partialTrace) throws Exception;
	
	/**
	 * Performs cross-validation on the given log.
	 * 
	 * @param log
	 * @throws Exception if cross-validation failed
	 */
	public void crossValidate(XLog log) throws Exception;
}
