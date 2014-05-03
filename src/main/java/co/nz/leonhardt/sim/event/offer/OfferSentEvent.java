package co.nz.leonhardt.sim.event.offer;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;

public class OfferSentEvent extends SimpleBusinessEvent<LoanApplication> {

	public OfferSentEvent(BpemEnabledModel owner) {
		super(owner, "Offer Sent", true);
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		
		// send business event
		fireEventLog(who, "O_SENT", StandardModel.COMPLETE, resource);
		
		ContactCustomerStartEvent e = new ContactCustomerStartEvent(model);
		e.schedule(who, model.getAvailableResource(), model.getCustomerInitialContactTimeSpan());
	}
}
