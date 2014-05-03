package co.nz.leonhardt.sim.event.init;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.BusinessEvent;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;
import co.nz.leonhardt.sim.event.Resource;
import co.nz.leonhardt.sim.event.end.DeclinedEvent;
import co.nz.leonhardt.sim.event.offer.OfferCreatedEvent;
import desmoj.core.simulator.TimeSpan;

public class AssessApplicationEndEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {
	
	public AssessApplicationEndEvent(BpemEnabledModel owner) {
		super(owner, "Stop Assessing Application", true);
	}
	
	public void eventRoutine(LoanApplication businessCase, Resource resource) {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		
		// send business event
		fireEventLog(businessCase, "W_Assessing application", StandardModel.COMPLETE, resource);
		
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
