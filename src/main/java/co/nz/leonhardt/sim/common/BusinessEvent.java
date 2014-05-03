package co.nz.leonhardt.sim.common;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.bpe.logs.EventLog;
import co.nz.leonhardt.sim.model.entity.Resource;

/**
 * Interface of a business event.
 * 
 * @author freddy
 *
 */
public interface BusinessEvent {
	
	/**
	 * Fires the given EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param log the log
	 */
	public void fireEventLog(BusinessCase businessCase, EventLog log);
	
	/**
	 * Fires an EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param conceptName the concept name
	 * @param lifecycleTransition the lifecycle transition
	 */
	public void fireEventLog(BusinessCase businessCase, String conceptName, StandardModel lifecycleTransition);
	
	/**
	 * Fires an EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param conceptName the concept name
	 * @param lifecycleTransition the lifecycle transition
	 * @param resource the resource
	 */
	public void fireEventLog(BusinessCase businessCase, String conceptName, StandardModel lifecycleTransition, String resource);
	
	/**
	 * Fires an EventLog.
	 * 
	 * @param businessCase the associated case
	 * @param conceptName the concept name
	 * @param lifecycleTransition the lifecycle transition
	 * @param resource the resource
	 */
	public void fireEventLog(BusinessCase businessCase, String conceptName, StandardModel lifecycleTransition, Resource resource);
	
	/**
	 * Closes and finalizes the given business case.
	 * No further events should be added after this has happened.
	 * 
	 * @param bCase
	 */
	public void closeCase(BusinessCase bCase);
}
