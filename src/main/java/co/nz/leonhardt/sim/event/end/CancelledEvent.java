package co.nz.leonhardt.sim.event.end;


import co.nz.leonhardt.sim.common.BpemEnabledModel;

/**
 * Event representing the cancellation of a Loan Application.
 * 
 * A_CANCELLED in original log.
 * 
 * @author freddy
 *
 */
public class CancelledEvent extends EndEvent {

	/**
	 * Creates a new application cancelled event.
	 * When triggered, this event closes the current case.
	 * 
	 * @param owner
	 */
	public CancelledEvent(BpemEnabledModel owner) {
		super(owner, "Cancelled", "A_CANCELLED");
	}
}
