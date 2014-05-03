package co.nz.leonhardt.sim.event.end;

import co.nz.leonhardt.sim.common.BpemEnabledModel;

/**
 * Event representing the denial of a Loan Application.
 * 
 * A_DECLINED in original log.
 * 
 * @author freddy
 *
 */
public class DeclinedEvent extends EndEvent {

	/**
	 * Creates a new application declined event.
	 * When triggered, this event closes the current case.
	 * 
	 * @param owner
	 */
	public DeclinedEvent(BpemEnabledModel owner) {
		super(owner, "Declined", "A_DECLINED");
	}
}
