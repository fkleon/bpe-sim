package co.nz.leonhardt.sim.event.init;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.BusinessEvent;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;

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
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		
		// send business event
		fireEventLog(who, "A_PREACCEPTED", StandardModel.COMPLETE, resource);
		
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
