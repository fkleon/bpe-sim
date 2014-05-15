package co.nz.leonhardt.bpe.stat;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import co.nz.leonhardt.reco.PredictionResult;

public class RAMStatisticsStorage {
	
	private Multimap<String, PredictionResult<?>> predictionResults;
	
	public RAMStatisticsStorage() {
		this.predictionResults = ArrayListMultimap.create();
	}
	
	public void addPredictionResult(String caseUuid, PredictionResult<?> result) {
		predictionResults.put(caseUuid, result);
	}
	
	public Map<String, Collection<PredictionResult<?>>> exportPredictionTimeline() {
		return predictionResults.asMap();
	}

}
