package co.nz.leonhardt.sim.model.event.finalize;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import co.nz.leonhardt.sim.model.entity.Resource;

public class ValidateApplicationStartEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {

	public ValidateApplicationStartEvent(BpemEnabledModel owner) {
		super(owner, "Start Validate Application", true);
	}

	@Override
	public void eventRoutine(LoanApplication who1, Resource who2) {
		SimulationModel model = (SimulationModel) getModel();
		fireEventLog(who1, Activity.W_ValidateApplication.toString(), StandardModel.START, who2);
		
		ValidateApplicationEndEvent event = new ValidateApplicationEndEvent(model);
		event.schedule(who1, who2, model.getApplicationValidationTimeSpan());
	}

}
