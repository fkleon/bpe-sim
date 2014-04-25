package co.nz.leonhardt.bpe.reco;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public interface PredictionService<T> {

	public void learn(XLog logs);
	
	public T predict(XTrace partialTrace);
}
