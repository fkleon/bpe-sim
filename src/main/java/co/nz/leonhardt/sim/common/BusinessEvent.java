package co.nz.leonhardt.sim.common;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.bpe.EventRepository;
import co.nz.leonhardt.bpe.logs.EventLog;
import desmoj.core.simulator.Event;
import desmoj.core.simulator.Model;

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
	
	public BusinessEvent(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
	}

	@Override
	public void eventRoutine(E businessCase) {
		// send generic log
		EventLog log = new EventLog(presentTime().getTimeAsDate());
		log.setConceptName(this.getClass().getSimpleName());
		log.setLifecycleTransition(StandardModel.COMPLETE);
		EventRepository.getInstance().addEvent(businessCase.getUuid(), log);		
	}
	
	/**
	 * Fires the given EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param log the log
	 */
	protected void fireEventLog(E businessCase, EventLog log) {
		EventRepository.getInstance().addEvent(businessCase.getUuid(), log);
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
		EventRepository.getInstance().addEvent(businessCase.getUuid(), log);
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
		EventRepository.getInstance().addEvent(businessCase.getUuid(), log);
		sendTrace(log);
	}
	
	private void sendTrace(EventLog log) {
		String traceNote = String.format("sends business event '%s' (%s).", log.getConceptName(), log.getLifecycleTransition());
		sendTraceNote(traceNote);
	}

}
