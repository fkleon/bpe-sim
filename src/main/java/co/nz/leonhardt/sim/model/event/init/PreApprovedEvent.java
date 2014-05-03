package co.nz.leonhardt.sim.model.event.init;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;

/**
 * Event representing the pre-approval of a Loan Application.
 * 
 * A_PREACCEPTED in original log.
 * 
 * @author freddy
 *
 */
public class PreApprovedEvent extends SimpleBusinessEvent<LoanApplication> {

	/** The resource associated with this event */
	protected final String resource;
	
	public PreApprovedEvent(BpemEnabledModel owner) {
		super(owner, "PreApprove", true);
		
		this.resource = "112";	
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		SimulationModel model = (SimulationModel) getModel();
		
		// send business event
		fireEventLog(who, Activity.A_PREACCEPTED.toString(), StandardModel.COMPLETE, resource);
		
		//model.applicationQueue.remove(who);
		
		
		AssessApplicationStartEvent event = new AssessApplicationStartEvent(model);
		event.schedule(who, model.getAvailableResource(), model.getApplicationInitialAssessmentTimeSpan());
//		if(!model.idleResources.isEmpty()) {
//			// draw and do
//			
//		} else {
//			
//		}
	}
}
