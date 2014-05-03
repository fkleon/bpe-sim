package co.nz.leonhardt.sim.event.offer;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;
import co.nz.leonhardt.sim.event.Resource;
import co.nz.leonhardt.sim.event.end.ActivatedEvent;
import co.nz.leonhardt.sim.event.end.CancelledEvent;
import desmoj.core.simulator.TimeSpan;

public class ContactCustomerEndEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {

	public ContactCustomerEndEvent(BpemEnabledModel owner) {
		super(owner, "End Contacting Customer", true);
	}

	@Override
	public void eventRoutine(LoanApplication who, Resource resource) {
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		fireEventLog(who, "W_Contacting Customer", StandardModel.COMPLETE, resource);
		
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
