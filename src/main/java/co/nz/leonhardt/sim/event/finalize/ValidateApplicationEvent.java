package co.nz.leonhardt.sim.event.finalize;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.Resource;

public class ValidateApplicationEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {

	public ValidateApplicationEvent(BpemEnabledModel owner) {
		super(owner, "Validate Application", true);
	}

	@Override
	public void eventRoutine(LoanApplication who1, Resource who2) {
		// TODO Auto-generated method stub
		
	}

}
