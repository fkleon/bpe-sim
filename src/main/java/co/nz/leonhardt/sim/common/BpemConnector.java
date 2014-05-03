package co.nz.leonhardt.sim.common;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.bpe.BPEM;
import co.nz.leonhardt.bpe.logs.EventLog;
import desmoj.core.simulator.Model;

public class BpemConnector {
	
	protected final BPEM bpemEnvironment;
	protected final Model model;
	
	public BpemConnector(BpemEnabledModel model) {
		this.bpemEnvironment = model.getBpemEnvironment();
		this.model = model;
	}

	/**
	 * Fires the given EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param log the log
	 */
	protected void fireEventLog(BusinessCase businessCase, EventLog log) {
		bpemEnvironment.addEvent(businessCase.getUuid(), log);
		sendTrace(log);
	}
	
	/**
	 * Fires an EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param conceptName the concept name
	 * @param lifecycleTransition the lifecycle transition
	 */
	protected void fireEventLog(BusinessCase businessCase, String conceptName, StandardModel lifecycleTransition) {
		EventLog log = new EventLog(model.presentTime().getTimeAsDate());
		log.setConceptName(conceptName);
		log.setLifecycleTransition(lifecycleTransition);
		bpemEnvironment.addEvent(businessCase.getUuid(), log);
		sendTrace(log);
	}
	
	/**
	 * Fires an EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param conceptName the concept name
	 * @param lifecycleTransition the lifecycle transition
	 * @param resource the resource
	 */
	protected void fireEventLog(BusinessCase businessCase, String conceptName, StandardModel lifecycleTransition, String resource) {
		EventLog log = new EventLog(model.presentTime().getTimeAsDate());
		log.setConceptName(conceptName);
		log.setLifecycleTransition(lifecycleTransition);
		log.setResource(resource);
		bpemEnvironment.addEvent(businessCase.getUuid(), log);
		sendTrace(log);
	}
	
	/**
	 * Closes and finalizes the given business case.
	 * No further events should be added after this has happened.
	 * 
	 * @param bCase
	 */
	protected void closeCase(BusinessCase bCase) {
		bpemEnvironment.endTrace(bCase.getUuid());
	}
	
	protected void sendTrace(EventLog log) {
		String traceNote = String.format("sends business event '%s' (%s).", log.getConceptName(), log.getLifecycleTransition());
		model.sendTraceNote(traceNote);
	}
}
