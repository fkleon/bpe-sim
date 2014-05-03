package co.nz.leonhardt.sim.model.event;


import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.model.Activity;

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
		super(owner, "Cancelled", Activity.A_CANCELLED);
	}
}
