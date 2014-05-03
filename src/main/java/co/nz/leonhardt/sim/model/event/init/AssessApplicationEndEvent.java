package co.nz.leonhardt.sim.model.event.init;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import co.nz.leonhardt.sim.model.entity.Resource;
import co.nz.leonhardt.sim.model.event.DeclinedEvent;
import co.nz.leonhardt.sim.model.event.offer.OfferCreatedEvent;
import desmoj.core.simulator.TimeSpan;

public class AssessApplicationEndEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {
	
	public AssessApplicationEndEvent(BpemEnabledModel owner) {
		super(owner, "Stop Assessing Application", true);
	}
	
	public void eventRoutine(LoanApplication businessCase, Resource resource) {
		// get a reference to the model
		SimulationModel model = (SimulationModel) getModel();
		
		// send business event
		fireEventLog(businessCase, Activity.W_AssessApplication.toString(), StandardModel.COMPLETE, resource);
		
		if(model.isApplicationDeclinedAfterAssessment()) {
			DeclinedEvent event = new DeclinedEvent(model);
			event.schedule(businessCase, new TimeSpan(30.0, TimeUnit.SECONDS));
		} else {
			OfferCreatedEvent event = new OfferCreatedEvent(model);
			event.schedule(businessCase, model.getOfferCreationTimeSpan());
		}
		// pick up new work or release resource
//		if(!model.applicationQueue.isEmpty()) {
//			// draw
//		} else {
//			model.idleResources.insert(resource);
//		}
	}

}
