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
	 */
	public void learn(XLog log);
	
	/**
	 * Predicts/recommends based on the given trace.
	 * 
	 * @param partialTrace
	 * @return
	 */
	public T predict(XTrace partialTrace);
	
	/**
	 * Performs cross-validation on the given log.
	 * 
	 * @param log
	 */
	public void crossValidate(XLog log);
}
