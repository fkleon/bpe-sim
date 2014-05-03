package co.nz.leonhardt.sim.event.offer;


import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.common.SimpleBusinessEvent;
import co.nz.leonhardt.sim.event.LoanApplication;
import co.nz.leonhardt.sim.event.LoanApplicationModel;
import co.nz.leonhardt.sim.event.finalize.ValidateApplicationStartEvent;

public class OfferSentBackEvent extends SimpleBusinessEvent<LoanApplication> {

	public OfferSentBackEvent(BpemEnabledModel owner) {
		super(owner, "Offer Sent Back", true);
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		
		// send business event
		fireEventLog(who, "O_SENT_BACK", StandardModel.COMPLETE, resource);
		
		ValidateApplicationStartEvent event = new ValidateApplicationStartEvent(model);
		event.schedule(who, model.getAvailableResource(), model.getApplicationInitialValidationTimeSpan());
	}
}
