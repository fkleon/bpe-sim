package co.nz.leonhardt.sim.event.init;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.BusinessEvent;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;
import co.nz.leonhardt.sim.event.Resource;

public class AssessApplicationStartEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {
	
	public AssessApplicationStartEvent(BpemEnabledModel owner) {
		super(owner, "Start Assessing Application", true);
	}
	
	public void eventRoutine(LoanApplication businessCase, Resource resource) {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		
		// send business event
		fireEventLog(businessCase, "W_Assessing application", StandardModel.START, resource);
		
		//model.applicationQueue.remove(who);
		
		AssessApplicationEndEvent event = new AssessApplicationEndEvent(model);
		event.schedule(businessCase, resource, model.getApplicationAssessmentTimeSpan());
	}

}
