package co.nz.leonhardt.sim.model.event.offer;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import co.nz.leonhardt.sim.model.entity.Resource;
import co.nz.leonhardt.sim.model.event.CancelledEvent;
import desmoj.core.simulator.TimeSpan;

public class ContactCustomerEndEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {

	public ContactCustomerEndEvent(BpemEnabledModel owner) {
		super(owner, "End Contacting Customer", true);
	}

	@Override
	public void eventRoutine(LoanApplication who, Resource resource) {
		SimulationModel model = (SimulationModel) getModel();
		fireEventLog(who, Activity.W_ContactCustomer.toString(), StandardModel.COMPLETE, resource);
		
		if(model.isCustomerContactSuccessful()) {
			sendTraceNote("customer contact successful..");
			if(model.isNewOffer()) {
				OfferCreatedEvent event = new OfferCreatedEvent(model);
				event.schedule(who, new TimeSpan(5, TimeUnit.MINUTES));
			} else {
				//TODO
				OfferSentBackEvent event = new OfferSentBackEvent(model);
				event.schedule(who, new TimeSpan(12.0, TimeUnit.MINUTES));
			}
		} else {
			sendTraceNote("customer contact unsuccessful..");
			// contact 5 times, else cancel
			if(who.getCustomerContactAttempts() < 3) {
				who.increaseCustomerContactAttempts();
				ContactCustomerStartEvent e = new ContactCustomerStartEvent(model);
				e.schedule(who, resource, model.getCustomerContactAttemptTimeSpan());
			} else {
				CancelledEvent e = new CancelledEvent(model);
				e.schedule(who, new TimeSpan(3, TimeUnit.MINUTES));
			}
		}
	}
}
