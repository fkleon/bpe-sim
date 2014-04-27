package co.nz.leonhardt.sim.event;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.BusinessEvent;
import desmoj.core.simulator.Event;

/**
 * Event representing the submission of a Loan Application.
 * 
 * @author freddy
 *
 */
public class LoanApplicationSubmissionEvent extends BusinessEvent<LoanApplication> {

	/** The resource associated with this event */
	protected final String resource;
	
	public LoanApplicationSubmissionEvent(BpemEnabledModel owner) {
		super(owner, "LoanApplicationSubmission", true);
		
		this.resource = "WebForm";
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		
		// send business event
		fireEventLog(who, "A_SUBMITTED", StandardModel.COMPLETE, resource);
		
		model.applicationQueue.insert(who);
		
		Event<LoanApplication> event;
		// check if application is declined
		if (model.isApplicationDeclinedImmediately()) {
			event = new LoanApplicationDeclinedEvent(model);
			//event.schedule(who, model.getApplicationPreCheckTimeSpan());
			
			//fireEventLog(who, "A_DECLINED", LifecycleTransition.COMPLETE, "LoanChecker");
			//model.applicationQueue.remove(who);
		} else {
			event = new LoanApplicationPreApprovedEvent(model);
			//fireEventLog(who, "A_PREACCEPTED", LifecycleTransition.COMPLETE, "LoanChecker");
		}
		
		event.schedule(who, model.getApplicationPreCheckTimeSpan());
	}
}
