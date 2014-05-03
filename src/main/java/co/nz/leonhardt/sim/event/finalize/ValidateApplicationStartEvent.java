package co.nz.leonhardt.sim.event.finalize;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;
import co.nz.leonhardt.sim.event.Resource;

public class ValidateApplicationStartEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {

	public ValidateApplicationStartEvent(BpemEnabledModel owner) {
		super(owner, "Start Validate Application", true);
	}

	@Override
	public void eventRoutine(LoanApplication who1, Resource who2) {
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		fireEventLog(who1, "W_Validate Application", StandardModel.START, who2);
		
		ValidateApplicationEndEvent event = new ValidateApplicationEndEvent(model);
		event.schedule(who1, who2, model.getApplicationValidationTimeSpan());
	}

}
