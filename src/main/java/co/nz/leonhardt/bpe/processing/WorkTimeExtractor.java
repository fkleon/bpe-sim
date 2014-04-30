package co.nz.leonhardt.bpe.processing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.util.TimeUtil;

/**
 * Extracts the total work time of a case, i.e.
 * the time spent between START and COMPLETE states.
 * 
 * @author freddy
 *
 */
public class WorkTimeExtractor extends NumericalMetricExtractor<Long> {

	private final TimeUnit timeUnit;
	
	private Logger l = Logger.getLogger("WorkTimeExtractor");

	/**
	 * Creates a new work time extractor which returns the work
	 * time of a trace in the given time unit.
	 * 
	 * @param timeUnit
	 */
	public WorkTimeExtractor(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
	
	@Override
	public Long extractMetric(XTrace trace) {
		XTimeExtension tExt = XTimeExtension.instance();
		
		Long totalWorkload = 0l;
		
		for (Entry<XEvent, XEvent> match: matchLifeCycles(trace).entrySet()) {
			Date startDate = tExt.extractTimestamp(match.getKey());
			Date endDate= tExt.extractTimestamp(match.getValue());
			
			//System.out.println("match " + startDate + ", " + endDate);
			
			totalWorkload += TimeUtil.calcTimeDiff(startDate, endDate, timeUnit);
		}

		return totalWorkload;
	}
	
	/**
	 * Tries to find matching START - COMPLETE pairs in the
	 * given trace.
	 * 
	 * @param trace
	 * @return a mapping of matches found.
	 */
	// TODO Some kind of bracket matcher algorithms could me more efficient?
	private Map<XEvent, XEvent> matchLifeCycles(XTrace trace) {
		Map<XEvent, XEvent> matchedEvents = new HashMap<>();
		
		// Contains positions of already matched complete events so that we don't
		// match them multiple times
		Set<Integer> completeEvents = new HashSet<>();
		
		for(int i = 0; i < trace.size(); i++) {
			XEvent curEvent = trace.get(i);
			String curName = extractConceptName(curEvent);
					
			if(hasTransition(curEvent, StandardModel.START)) {
				// find associated complete event
				for(int j = i; j < trace.size(); j++) {
					// ignore if j was already matched
					if(completeEvents.contains(j)) {
						continue;
					}
					
					XEvent cEvent = trace.get(j);
					
					if(hasTransition(cEvent, StandardModel.COMPLETE) &&
							curName.equals(extractConceptName(cEvent))) {
						// end event.
						matchedEvents.put(curEvent, cEvent);
						
						// remember matched event's position
						completeEvents.add(j);
						break;
					}
					
					l.fine("Did not complete: " + curName);
				}
				continue;
			}
			
			if(hasTransition(curEvent, StandardModel.COMPLETE)) {
				l.fine("Did not start: " + curName);
			}
		}
		
		return matchedEvents;
	}
	
	/**
	 * Checks if the given event satisfies the given transition.
	 * 
	 * @param event
	 * @param transition
	 * @return true if transition matches, false else
	 */
	private boolean hasTransition(XEvent event, StandardModel transition) {
		XLifecycleExtension lcExt = XLifecycleExtension.instance();
		
		String target = transition.toString().toLowerCase();
		String actual = lcExt.extractTransition(event);
		
		if(actual != null && target.equals(actual.toLowerCase())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Extracts the concept name.
	 * 
	 * @param elem
	 * @return The name of "none" as string
	 */
	private String extractConceptName(XAttributable elem) {
		XConceptExtension cExt = XConceptExtension.instance();
		String eName = cExt.extractName(elem);
		return eName == null ? "none" : eName;
	}

	@Deprecated
	private List<XEvent> extractEventsByLifecycle(XTrace trace, StandardModel lifecycle) {
		List<XEvent> events = new ArrayList<>();
		
		XLifecycleExtension lcExt = XLifecycleExtension.instance();
		String lcString = lifecycle.toString().toLowerCase();
		
		for (XEvent event: trace) {
			String transition = lcExt.extractTransition(event);
			
			if(transition != null && lcString.equals(transition.toLowerCase())) {
				events.add(event);
			}
		}
		
		return events;
	}
	
	
	@Override
	public String getMetricName() {
		return "WorkTime";
	}

	
}
