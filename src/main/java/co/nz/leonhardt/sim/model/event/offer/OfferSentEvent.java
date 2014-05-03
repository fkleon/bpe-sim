package co.nz.leonhardt.sim.model.event.offer;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;

public class OfferSentEvent extends SimpleBusinessEvent<LoanApplication> {

	public OfferSentEvent(BpemEnabledModel owner) {
		super(owner, "Offer Sent", true);
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		SimulationModel model = (SimulationModel) getModel();
		
		// send business event
		fireEventLog(who, Activity.O_SENT.toString(), StandardModel.COMPLETE, resource);
		
		ContactCustomerStartEvent e = new ContactCustomerStartEvent(model);
		e.schedule(who, model.getAvailableResource(), model.getCustomerInitialContactTimeSpan());
	}
}
