package co.nz.leonhardt.bpe.logs;

import java.util.Date;
import java.util.UUID;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;

import co.nz.leonhardt.bpe.categories.Outcome;

/**
 * Represents an event in the context of a Process Instance (case).
 * 
 * @author freddy
 *
 */
public class EventLog {
	
	private final String eventUuid;
	
	/* Time */
	private final Date timestamp;
	
	/* Concept */
	private String conceptName;
	
	/* Lifecycle */
	private XLifecycleExtension.StandardModel lifecycleTransition;
	
	/* Outcome */
	private Outcome outcome;
	
	/* Organizational */
	private String resource;
	private String role;
	private String group;
	
	public EventLog(final String uuid, final Date timestamp) {
		this.eventUuid = uuid;
		this.timestamp = timestamp;
	}
	
	public EventLog(final Date timestamp) {
		this.eventUuid = UUID.randomUUID().toString();
		this.timestamp = timestamp;
	}
	
	public EventLog() {
		this.eventUuid = UUID.randomUUID().toString();
		this.timestamp = new Date();
	}
	
	public String getEventUuid() {
		return eventUuid;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	public XLifecycleExtension.StandardModel getLifecycleTransition() {
		return lifecycleTransition;
	}

	public void setLifecycleTransition(XLifecycleExtension.StandardModel lifecycleTransition) {
		this.lifecycleTransition = lifecycleTransition;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Outcome getOutcome() {
		return outcome;
	}

	public void setOutcome(Outcome outcome) {
		this.outcome = outcome;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventLog [eventUuid=").append(eventUuid)
				.append(", timestamp=").append(timestamp)
				.append(", conceptName=").append(conceptName)
				.append(", lifecycleTransition=").append(lifecycleTransition)
				.append(", resource=").append(resource)
				.append("]");
		return builder.toString();
	}
	
	/**
	 * Converts this EventLog to an XEvent.
	 * 
	 * @return an XEvent
	 */
	public XEvent asXEvent() {
		XFactory fac = new XFactoryBufferedImpl();
		XEvent event = fac.createEvent(); //TODO keep ID
		
		if(timestamp != null) {
			/*
			 * <date key="time:timestamp" value="2011-10-01T00:38:44.546+02:00"/>
			 */
			XTimeExtension tExt = XTimeExtension.instance();
			tExt.assignTimestamp(event, timestamp);
		}
		
		if(lifecycleTransition != null) {
			/*
			 * <string key="lifecycle:transition" value="COMPLETE"/>
			 */
			XLifecycleExtension lcExt = XLifecycleExtension.instance();
			lcExt.assignStandardTransition(event, lifecycleTransition);
		}
		
		if(conceptName != null) {
			/*
			 * <string key="concept:name" value="A_SUBMITTED"/>
			 */
			XConceptExtension cExt = XConceptExtension.instance();
			cExt.assignName(event, conceptName);
		}
		
		if(resource != null) {
			/*
			 * <string key="org:resource" value="112"/>
			 */
			XOrganizationalExtension orgExt = XOrganizationalExtension.instance();
			orgExt.assignResource(event, resource);
		}
		
		if(role != null) {
			// not used in original log
			XOrganizationalExtension orgExt = XOrganizationalExtension.instance();
			orgExt.assignRole(event, role);
		}
		
		if(group != null) {
			// not used in original log
			XOrganizationalExtension orgExt = XOrganizationalExtension.instance();
			orgExt.assignGroup(event, group);
		}
		
		if(outcome != null) {
			// not used in original log
			XAttribute attr = fac.createAttributeLiteral("OUTCOME", outcome.toString(), null);
			event.getAttributes().put("OUTCOME", attr);
		}
		
		return event;
	}
}
