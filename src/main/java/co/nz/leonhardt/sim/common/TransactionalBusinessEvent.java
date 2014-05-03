package co.nz.leonhardt.sim.common;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.bpe.BPEM;
import co.nz.leonhardt.bpe.logs.EventLog;
import co.nz.leonhardt.sim.event.Resource;
import desmoj.core.simulator.EventOf2Entities;

/**
 * A business event related to a business case and a resource.
 *
 * @author freddy
 *
 * @param <C>
 * @param <R>
 */
public abstract class TransactionalBusinessEvent<C extends BusinessCase, R extends Resource> extends EventOf2Entities<C, R>{

	/** The BPEM environment */
	protected final BPEM bpemEnvironment;
	
	/**
	 * Creates a new business event with the given BPEM-enabled model.
	 * 
	 * @param owner a BPEM-enabled model
	 * @param name the name of this event
	 * @param showInTrace
	 */
	public TransactionalBusinessEvent(BpemEnabledModel owner, String name,
			boolean showInTrace) {
		super(owner, name, showInTrace);
		
		this.bpemEnvironment = owner.getBpemEnvironment();
	}
	
	/**
	 * Fires the given EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param log the log
	 */
	protected void fireEventLog(C businessCase, EventLog log) {
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
	protected void fireEventLog(C businessCase, String conceptName, StandardModel lifecycleTransition) {
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
	protected void fireEventLog(C businessCase, String conceptName, StandardModel lifecycleTransition, R resource) {
		EventLog log = new EventLog(presentTime().getTimeAsDate());
		log.setConceptName(conceptName);
		log.setLifecycleTransition(lifecycleTransition);
		log.setResource(resource.getName());
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
