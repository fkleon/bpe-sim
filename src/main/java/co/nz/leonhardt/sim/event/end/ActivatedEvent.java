package co.nz.leonhardt.sim.event.end;

import co.nz.leonhardt.sim.common.BpemEnabledModel;

/**
 * Event representing the activation of a Loan Application.
 * 
 * A_ACTIVATED in original log.
 * 
 * @author freddy
 *
 */
public class ActivatedEvent extends EndEvent {

	/**
	 * Creates a new application declined event.
	 * When triggered, this event closes the current case.
	 * 
	 * @param owner
	 */
	public ActivatedEvent(BpemEnabledModel owner) {
		super(owner, "Activated", "A_ACTIVATED");
	}
}
