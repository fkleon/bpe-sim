package co.nz.leonhardt.sim.model.event.offer;


import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.model.Activity;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import co.nz.leonhardt.sim.model.event.finalize.ValidateApplicationStartEvent;

public class OfferSentBackEvent extends SimpleBusinessEvent<LoanApplication> {

	public OfferSentBackEvent(BpemEnabledModel owner) {
		super(owner, "Offer Sent Back", true);
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		SimulationModel model = (SimulationModel) getModel();
		
		// send business event
		fireEventLog(who, Activity.O_SENT_BACK.toString(), StandardModel.COMPLETE, resource);
		
		ValidateApplicationStartEvent event = new ValidateApplicationStartEvent(model);
		event.schedule(who, model.getAvailableResource(), model.getApplicationInitialValidationTimeSpan());
	}
}
