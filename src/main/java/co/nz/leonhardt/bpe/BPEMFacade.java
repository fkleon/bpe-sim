package co.nz.leonhardt.bpe;

import java.util.Collection;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.Outcome;
import co.nz.leonhardt.bpe.logs.CaseLog;
import co.nz.leonhardt.bpe.logs.EventLog;
import co.nz.leonhardt.bpe.logs.RAMProcessLogStorage;
import co.nz.leonhardt.bpe.stat.EventStatistics;
import co.nz.leonhardt.bpe.stat.RAMStatisticsStorage;
import co.nz.leonhardt.reco.ClassificationResult;
import co.nz.leonhardt.reco.PredictionResult;
import co.nz.leonhardt.reco.weka.DTOutcomeClassifier;

/**
 * Facade implementing the BPEM interface.
 * 
 * 
 * @author freddy
 *
 */
public class BPEMFacade implements BPEM {

	/** The underlying process log storage */
	private RAMProcessLogStorage processLogStorage;
	
	private RAMStatisticsStorage statisticsStorage;
	
	private DTOutcomeClassifier outcomeClassifier;
	
	/**
	 * Creates a new BPEMFacade backed by a RAM storage.
	 * 
	 * @param trainingSet the log used to train the classifier
	 * @throws Exception 
	 */
	public BPEMFacade(XLog trainingSet) throws Exception {
		this.processLogStorage = new RAMProcessLogStorage();
		this.statisticsStorage = new RAMStatisticsStorage();
		this.outcomeClassifier = new DTOutcomeClassifier();
		this.outcomeClassifier.learn(trainingSet);
	}
	
	/**
	 * Creates a new BPEMFacade backed by a RAM storage.
	 * 
	 * @throws Exception 
	 */
	public BPEMFacade() throws Exception {
		this.processLogStorage = new RAMProcessLogStorage();
		this.statisticsStorage = new RAMStatisticsStorage();
	}
	
	@Override
	public void startTrace(CaseLog caseLog) {
		processLogStorage.startTrace(caseLog);
		
		//TODO setup KPI collectors
	}

	@Override
	public void endTrace(String caseUuid) {
		processLogStorage.endTrace(caseUuid);
		
		//TODO finalise KPI collectors
	}

	@Override
	public void addEvent(String caseUuid, EventLog eventLog) {
		processLogStorage.addEvent(caseUuid, eventLog);
		
		if(outcomeClassifier == null)
			return;
		
		// TODO update KPI collectors
		XTrace partialTrace = processLogStorage.getPartialTrace(caseUuid);
		try {
			ClassificationResult<Outcome> result = outcomeClassifier.predict(partialTrace);
			PredictionResult<Outcome> bestResult = result.results.get(0);
			statisticsStorage.addPredictionResult(caseUuid, bestResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public XLog exportFullLog() {
		return processLogStorage.export();
	}
	
	@Override
	public XLog exportCompletedLog() {
		return processLogStorage.getCompletedLog();
	}
	
	@Override
	public Map<String, Collection<PredictionResult<?>>> exportPredictionTimeline() {
		return statisticsStorage.exportPredictionTimeline();
	}

	@Override
	public Map<?, ?> exportKPIs() {
		// TODO export KPIs
		return null;
	}

	@Override
	public EventStatistics getStatistics(String eventName) {
		// TODO Auto-generated method stub
		return null;
	}

}
