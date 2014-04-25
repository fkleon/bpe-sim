package co.nz.leonhardt.sim.process;

import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

/**
 * This class represents a process source, which continually generates cases in
 * order to keep the simulation running.
 * 
 * It will create a new case, activate it (so that it arrives at the system)
 * and then wait until the next case arrival is due.
 */
public class LoanApplicationGenerator extends SimProcess {

	/**
	 * CaseGenerator constructor comment.
	 * 
	 * @param owner
	 *            the model this case generator belongs to
	 * @param name
	 *            this case generator's name
	 * @param showInTrace
	 *            flag to indicate if this process shall produce output for the
	 *            trace
	 */
	public LoanApplicationGenerator(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
	}

	/**
	 * describes this process's life cycle: continually generate new cases.
	 */
	@Override
	public void lifeCycle() {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();

		while (true) {
			// create a new application
			double amount = model.getApplicationSubmissionAmount();
			LoanApplication application = new LoanApplication(model, amount);

			// now let the newly created case roll on the parking-lot
			// which means we will activate it after this case generator
			application.activateAfter(this);

			// wait until next case arrival is due
			hold(new TimeSpan(model.getCaseSubmissionTime(), TimeUnit.MINUTES));
		}
	}

}
