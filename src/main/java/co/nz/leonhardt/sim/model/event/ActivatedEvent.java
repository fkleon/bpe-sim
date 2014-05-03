package co.nz.leonhardt.sim.model.event;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.model.Activity;

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
		super(owner, "Activated", Activity.A_ACTIVATED);
	}
}
