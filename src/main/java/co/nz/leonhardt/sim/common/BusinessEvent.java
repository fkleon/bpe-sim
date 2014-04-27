package co.nz.leonhardt.sim.common;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.bpe.BPEM;
import co.nz.leonhardt.bpe.logs.EventLog;
import desmoj.core.simulator.Event;

/**
 * A business event related to a business case.
 * 
 * @author freddy
 *
 * @param <E>
 */
public class BusinessEvent<E extends BusinessCase> extends Event<E> {

	/** The resource associated with this event */
	//protected final String resource;
	
	protected BPEM bpemEnvironment;
	
	/**
	 * Creates a new business event with the given BPEM-enabled model.
	 * 
	 * @param owner a BPEM-enabled model
	 * @param name the name of this event
	 * @param showInTrace
	 */
	public BusinessEvent(BpemEnabledModel owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
		this.bpemEnvironment = owner.getBpemEnvironment();
	}

	@Override
	public void eventRoutine(E businessCase) {
		// send generic log
		EventLog log = new EventLog(presentTime().getTimeAsDate());
		log.setConceptName(this.getClass().getSimpleName());
		log.setLifecycleTransition(StandardModel.COMPLETE);
		bpemEnvironment.addEvent(businessCase.getUuid(), log);		
	}
	
	/**
	 * Fires the given EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param log the log
	 */
	protected void fireEventLog(E businessCase, EventLog log) {
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
	protected void fireEventLog(E businessCase, String conceptName, StandardModel lifecycleTransition) {
		EventLog log = new EventLog(presentTime().getTimeAsDate());
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
	protected void fireEventLog(E businessCase, String conceptName, StandardModel lifecycleTransition, String resource) {
		EventLog log = new EventLog(presentTime().getTimeAsDate());
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
	
	private void sendTrace(EventLog log) {
		String traceNote = String.format("sends business event '%s' (%s).", log.getConceptName(), log.getLifecycleTransition());
		sendTraceNote(traceNote);
	}

}
