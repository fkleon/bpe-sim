package co.nz.leonhardt.bpe;

import java.util.Map;

import org.deckfour.xes.model.XLog;

import co.nz.leonhardt.bpe.logs.CaseLog;
import co.nz.leonhardt.bpe.logs.EventLog;
import co.nz.leonhardt.bpe.stat.EventStatistics;

/**
 * The Business Process Execution and Monitoring environment.
 * 
 * @author freddy
 *
 */
public interface BPEM {

	/**
	 * Indicates the start of a business case as described in the
	 * given {@link CaseLog}.
	 * 
	 * @param caseLog the business case
	 */
	public void startTrace(CaseLog caseLog);
	
	/**
	 * Indicates the end of a business case with the given id.
	 * 
	 * @param caseUuid the unique id of the case to end
	 */
	public void endTrace(String caseUuid);
	
	/**
	 * Adds a new business event to the business case with the given id.
	 * 
	 * @param caseUuid the unique id of the business case associated to the event
	 * @param eventLog the event log describing the business event
	 */
	public void addEvent(String caseUuid, EventLog eventLog);
	
	/**
	 * Exports the underlying process storage as XLog.
	 * 
	 * @return
	 */
	public XLog exportLog();
	
	/**
	 * Exports the KPIs collected so far.
	 * 
	 * @return TODO: to define
	 */
	public Map<?, ?> exportKPIs();
	
	/**
	 * Returns the global statistics for a given event.
	 * Contains information about time, quality and cost dimension.
	 * 
	 * @param activityName
	 */
	public EventStatistics getStatistics(String activityName);
}
