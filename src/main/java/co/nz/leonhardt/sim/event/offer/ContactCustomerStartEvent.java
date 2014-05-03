package co.nz.leonhardt.sim.event.offer;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.TransactionalBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;
import co.nz.leonhardt.sim.event.Resource;

public class ContactCustomerStartEvent extends TransactionalBusinessEvent<LoanApplication, Resource> {

	public ContactCustomerStartEvent(BpemEnabledModel owner) {
		super(owner, "Start Contacting Customer", true);
	}

	@Override
	public void eventRoutine(LoanApplication who, Resource resource) {
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		fireEventLog(who, "W_Contacting Customer", StandardModel.START, resource);
		
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
