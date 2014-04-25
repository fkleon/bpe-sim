package co.nz.leonhardt.sim.process;

import co.nz.leonhardt.util.SimpleIdGenerator;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;

/**
 * Represents a case.
 * 
 * @author freddy
 *
 */
public class LoanApplication extends SimProcess {

	LoanApplicationModel model;
	Double amountRequested;
	Integer id;

	public LoanApplication(Model model, Double amountRequested) {
		super(model, "LoanApplication", true);
		this.model = (LoanApplicationModel) model;
		this.amountRequested = amountRequested;
		this.id = SimpleIdGenerator.getInstance().getNextId();
	}

	@Override
	public void lifeCycle() {
		model.caseQueue.insert(this);
		
		String traceNote = String.format("Application #%d: Request for EUR %.2f at time T+%s.", id, amountRequested, presentTime());
		sendTraceNote(traceNote);
		sendTraceNote("CaseQueuelength: " + model.caseQueue.length());

		if(model.getApplicationDeclinedImmediately()) {
			sendTraceNote("Application was declined immediately and leaves system.");
			model.caseQueue.remove(this);
			return;
		}
		
		if (!model.idleVCQueue.isEmpty()) {
			VanCarrier vanCarrier = model.idleVCQueue.first();
			// remove the van carrier from the queue
			model.idleVCQueue.remove(vanCarrier);
			vanCarrier.activateAfter(this);
		}

		// wait for service
		passivate();

		sendTraceNote("Application was serviced and leaves system.");
	}
}
