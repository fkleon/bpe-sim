package co.nz.leonhardt.sim.model.event.init;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import co.nz.leonhardt.sim.model.entity.Resource;

public class AssessApplicationStartEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {
	
	public AssessApplicationStartEvent(BpemEnabledModel owner) {
		super(owner, "Start Assessing Application", true);
	}
	
	public void eventRoutine(LoanApplication businessCase, Resource resource) {
		// get a reference to the model
		SimulationModel model = (SimulationModel) getModel();
		
		// send business event
		fireEventLog(businessCase, Activity.W_AssessApplication.toString(), StandardModel.START, resource);
		
		//model.applicationQueue.remove(who);
		
		AssessApplicationEndEvent event = new AssessApplicationEndEvent(model);
		event.schedule(businessCase, resource, model.getApplicationAssessmentTimeSpan());
	}

}
