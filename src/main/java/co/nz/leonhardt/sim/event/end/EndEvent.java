package co.nz.leonhardt.sim.event.end;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;

/**
 * Abstract end event for a loan application business case.
 * 
 * @author freddy
 *
 */
public abstract class EndEvent extends SimpleBusinessEvent<LoanApplication> {

	/** The resource associated with this event */
	protected final String resource;
	
	/** The activity of this end event */
	protected final String activityName;
	
	/**
	 * Creates a new application enve event.
	 * When triggered, this event closes the current case.
	 * 
	 * @param owner
	 */
	public EndEvent(BpemEnabledModel owner, String name, String activityName) {
		super(owner, name, true);
		
		this.activityName = activityName;
		this.resource = "112";	
	}
	
	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		
		// send business event
		fireEventLog(who, activityName, StandardModel.COMPLETE, resource);
		closeCase(who);
		
		model.applicationQueue.remove(who);
		
		sendTraceNote(who + " was " + activityName + " and leaves the system.");
	}
}
