package co.nz.leonhardt.sim.event;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.sim.common.BusinessEvent;
import desmoj.core.simulator.Model;

/**
 * Event representing the submission of a Loan Application.
 * 
 * @author freddy
 *
 */
public class LoanApplicationPreApprovedEvent extends BusinessEvent<LoanApplication> {

	/** The resource associated with this event */
	protected final String resource;
	
	public LoanApplicationPreApprovedEvent(Model owner) {
		super(owner, "LoanApplicationPreApprove", true);
		
		this.resource = "112";	
	}

	@Override
	public void eventRoutine(LoanApplication who) {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();
		
		// send business event
		fireEventLog(who, "A_PREAPPROVED", StandardModel.COMPLETE, resource);
		
		//model.applicationQueue.remove(who);
	}
}
