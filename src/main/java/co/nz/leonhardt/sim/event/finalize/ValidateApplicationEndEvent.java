package co.nz.leonhardt.sim.event.finalize;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;
import co.nz.leonhardt.sim.event.Resource;
import co.nz.leonhardt.sim.event.end.ActivatedEvent;
import desmoj.core.simulator.TimeSpan;

public class ValidateApplicationEndEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {

	public ValidateApplicationEndEvent(BpemEnabledModel owner) {
		super(owner, "End Validate Application", true);
	}

	@Override
	public void eventRoutine(LoanApplication who1, Resource who2) {
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		fireEventLog(who1, "W_Validate Application", StandardModel.COMPLETE, who2);
		
		ActivatedEvent event = new ActivatedEvent(model);
		event.schedule(who1, new TimeSpan(12.0, TimeUnit.MINUTES));
	}

}
