package co.nz.leonhardt.sim.model.event.offer;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import co.nz.leonhardt.sim.model.entity.Resource;

public class ContactCustomerStartEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {

	public ContactCustomerStartEvent(BpemEnabledModel owner) {
		super(owner, "Start Contacting Customer", true);
	}

	@Override
	public void eventRoutine(LoanApplication who, Resource resource) {
		SimulationModel model = (SimulationModel) getModel();
		fireEventLog(who, Activity.W_ContactCustomer.toString(), StandardModel.START, resource);
		
//		if(model.isCustomerContactSuccessful()) {
//			ContactCustomerEndEvent event = new ContactCustomerEndEvent(model);
//			event.schedule(who, resource, model.getCustomerContactTimeSpan());
//			fireEventLog(who, "W_Contacting Customer", StandardModel.COMPLETE, resource);
//
//		}
		sendTraceNote("starting customer contact..");

		
		ContactCustomerEndEvent event = new ContactCustomerEndEvent(model);
		event.schedule(who, resource, model.getCustomerContactTimeSpan());
	}
}
