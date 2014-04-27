package co.nz.leonhardt.bpe.stat;

/**
 * Contains statistics about an event.
 * 
 * @author freddy
 *
 */
public class EventStatistics {
	
	private final String eventId;
	
	public EventStatistics(String eventId) {
		this.eventId = eventId;
	}

	public String getEventId() {
		return eventId;
	}

}
