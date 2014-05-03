package co.nz.leonhardt.sim.model.event.finalize;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import co.nz.leonhardt.sim.model.entity.Resource;
import co.nz.leonhardt.sim.model.event.ActivatedEvent;
import co.nz.leonhardt.sim.model.event.DeclinedEvent;
import desmoj.core.simulator.TimeSpan;

public class ValidateApplicationEndEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {

	public ValidateApplicationEndEvent(BpemEnabledModel owner) {
		super(owner, "End Validate Application", true);
	}

	@Override
	public void eventRoutine(LoanApplication who1, Resource who2) {
		SimulationModel model = (SimulationModel) getModel();
		fireEventLog(who1, Activity.W_ValidateApplication.toString(), StandardModel.COMPLETE, who2);
		
		if(model.isApplicationDeclinedAfterAssessment()) {
			DeclinedEvent event = new DeclinedEvent(model);
			event.schedule(who1, new TimeSpan(15.0, TimeUnit.SECONDS));
		} else {
			ActivatedEvent event = new ActivatedEvent(model);
			event.schedule(who1, new TimeSpan(12.0, TimeUnit.MINUTES));
		}
	}

}
