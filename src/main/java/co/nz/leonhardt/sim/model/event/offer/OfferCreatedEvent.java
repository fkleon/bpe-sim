package co.nz.leonhardt.sim.model.event.offer;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import desmoj.core.simulator.TimeSpan;

public class OfferCreatedEvent extends SimpleBusinessEvent<LoanApplication> {

	public OfferCreatedEvent(BpemEnabledModel owner) {
		super(owner, "Offer Created", true);
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		SimulationModel model = (SimulationModel) getModel();
		
		// send business event
		fireEventLog(who, Activity.O_CREATED.toString(), StandardModel.COMPLETE, resource);
		
		OfferSentEvent ose = new OfferSentEvent(model);
		ose.schedule(who, new TimeSpan(5, TimeUnit.SECONDS));
	}
}
