package co.nz.leonhardt.sim.model.event.init;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import co.nz.leonhardt.sim.model.event.DeclinedEvent;
import desmoj.core.simulator.Event;

/**
 * Event representing the submission of a Loan Application.
 * 
 * @author freddy
 *
 */
public class SubmissionEvent extends SimpleBusinessEvent<LoanApplication> {

	public SubmissionEvent(BpemEnabledModel owner) {
		super(owner, "Submission", true);
		
		this.resource = "WebForm";
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		SimulationModel model = (SimulationModel) getModel();
		
		// send business event
		fireEventLog(who, Activity.A_SUBMITTED.toString(), StandardModel.COMPLETE, resource);
		
		model.applicationQueue.insert(who);
		
		Event<LoanApplication> event;
		
		// check if application is declined
		if (model.isApplicationDeclinedImmediately()) {
			event = new DeclinedEvent(model);
		} else {
			event = new PreApprovedEvent(model);
		}
		
		event.schedule(who, model.getApplicationPreCheckTimeSpan());
	}
}
