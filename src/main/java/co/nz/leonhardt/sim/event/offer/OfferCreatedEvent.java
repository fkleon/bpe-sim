package co.nz.leonhardt.sim.event.offer;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;
import desmoj.core.simulator.TimeSpan;

public class OfferCreatedEvent extends SimpleBusinessEvent<LoanApplication> {

	public OfferCreatedEvent(BpemEnabledModel owner) {
		super(owner, "Offer Created", true);
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		
		// send business event
		fireEventLog(who, "O_CREATED", StandardModel.COMPLETE, resource);
		
		OfferSentEvent ose = new OfferSentEvent(model);
		ose.schedule(who, new TimeSpan(5, TimeUnit.SECONDS));
	}
}
