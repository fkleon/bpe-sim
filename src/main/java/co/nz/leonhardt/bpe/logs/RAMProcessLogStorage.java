package co.nz.leonhardt.bpe.logs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import co.nz.leonhardt.bpe.categories.Outcome;

/**
 * Collects all completed traces and partial traces.
 * 
 * Provides methods to export the logs into XES format.
 * 
 * @author freddy
 *
 */
public class RAMProcessLogStorage {
	
	protected Multimap<String, EventLog> events;
	
	protected HashMap<String, CaseLog> completedTraces;
	protected HashMap<String, CaseLog> partialTraces;
	
	/**
	 * Creates a new EventRepository
	 */
	public RAMProcessLogStorage() {
		this.events = ArrayListMultimap.create();
		
		this.completedTraces = new HashMap<>();
		this.partialTraces = new HashMap<>();
	}
	
	/**
	 * Adds the given event to the given case.
	 * 
	 * @param caseUuid
	 * @param e
	 */
	public void addEvent(final String caseUuid, final EventLog e) {
		//System.out.println(e);
		if(!events.containsKey(caseUuid)) {
			System.out.println("There is no case with this ID yet: " + caseUuid);
		}
		events.put(caseUuid, e);
	}
	
	/**
	 * Starts a case and adds an artificial START event at the beginning.
	 * 
	 * @param bCaseLog
	 */
	public void startTrace(final CaseLog bCaseLog) {
		partialTraces.put(bCaseLog.getCaseUuid(), bCaseLog);
		EventLog artificialStartEvent = new EventLog(bCaseLog.getTimestamp());
		artificialStartEvent.setConceptName("START");
		artificialStartEvent.setLifecycleTransition(StandardModel.COMPLETE);
		events.put(bCaseLog.getCaseUuid(), artificialStartEvent);
	}
	
	/**
	 * Closes a case and adds an artificial END event at the end.
	 * 
	 * @param bCase
	 */
	public void endTrace(String caseUuid) {
		//String caseUuid = bCase.getUuid();
		
		// add artificial end event
		List<EventLog> caseEvents = (List<EventLog>) events.get(caseUuid);
		EventLog lastEvent = caseEvents.get(caseEvents.size()-1);
		
		EventLog artificialEndEvent = new EventLog(lastEvent.getTimestamp());
		artificialEndEvent.setConceptName("END");
		artificialEndEvent.setLifecycleTransition(StandardModel.COMPLETE);
		
		// find outcome
		String lastConceptName = lastEvent.getConceptName();
		if(lastConceptName.equals("A_ACTIVATED")) {
			artificialEndEvent.setOutcome(Outcome.ACCEPTED);
		} else if(lastConceptName.equals("A_CANCELLED")) {
			artificialEndEvent.setOutcome(Outcome.CANCELLED);
		} else {
			artificialEndEvent.setOutcome(Outcome.DECLINED);
		}
		
		events.put(caseUuid, artificialEndEvent);

		// move to completed set
		CaseLog cLog = partialTraces.remove(caseUuid);
		completedTraces.put(caseUuid, cLog);
	}
	
	/**
	 * Returns all cases, i.e. both the partial and the completed traces.
	 * 
	 * @return
	 */
	public Set<CaseLog> getAllCases() {
		Set<CaseLog> cases = new HashSet<>();
		cases.addAll(completedTraces.values());
		cases.addAll(partialTraces.values());
		return cases;
	}
	
	/**
	 * Returns the partial trace for the given case.
	 * 
	 * @param caseUuid
	 * @return
	 */
	public XTrace getPartialTrace(String caseUuid) {
		//TODO hack
		if(!partialTraces.containsKey(caseUuid)) {
			throw new IllegalArgumentException("nosuchtrace");
		}
		
		//XFactory xFac = new XFactoryBufferedImpl();
		
		CaseLog cLog = partialTraces.get(caseUuid);
		XTrace trace = cLog.asXTrace();
		for(EventLog eLog: events.get(caseUuid)) {
			trace.add(eLog.asXEvent());
		}
		
		return trace;
	}
	
	/**
	 * Returns a log containing only completed traces.
	 * 
	 * @return
	 */
	public XLog getCompletedLog() {
		XFactory xFac = new XFactoryBufferedImpl();
		XLog log = xFac.createLog();
		
		// build traces
		for(CaseLog cLog: completedTraces.values()) {
			XTrace trace = cLog.asXTrace();
			for(EventLog eLog: events.get(cLog.getCaseUuid())) {
				XEvent event = eLog.asXEvent();
				trace.add(event);
			}
			log.add(trace);
		}
		
		return log;
	}
	
	/**
	 * Returns a log containing all cases.
	 * 
	 * @return
	 */
	public XLog export() {
		String logLine = String.format("Exporting %d cases with %d events..", events.keySet().size(), events.size());
		System.out.println(logLine);
		
		XFactory xFac = new XFactoryBufferedImpl();
		XLog log = xFac.createLog();
		
		// build traces
		for(CaseLog cLog: getAllCases()) {
			XTrace trace = cLog.asXTrace();
			for(EventLog eLog: events.get(cLog.getCaseUuid())) {
				XEvent event = eLog.asXEvent();
				trace.add(event);
			}
			log.add(trace);
		}
		
		return log;
	}
}
