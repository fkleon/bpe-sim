package co.nz.leonhardt.sim.process;

import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

/**
 * This class represents a van carrier in the ProcessesExample model. The VC
 * waits until a truck requests its service. It will then fetch the container
 * and load it onto the truck. If there is another truck waiting it starts
 * serving this truck. Otherwise it waits again for the next truck to arrive.
 */
public class VanCarrier extends SimProcess {

	/**
	 * a reference to the model this process is a part of useful shortcut to
	 * access the model's static components
	 */
	private LoanApplicationModel model;

	/**
	 * Constructor of the van carrier process.
	 * 
	 * Used to create a new VC to serve trucks.
	 * 
	 * @param owner
	 *            the model this process belongs to
	 */
	public VanCarrier(Model owner) {
		super(owner, "Van Carrier", true);
		this.model = (LoanApplicationModel) owner;
	}

	/**
	 * Describes this van carrier's life cycle.
	 * 
	 * It will continually loop through the following steps: Check if there is a
	 * customer waiting.
	 * 
	 * If there is someone waiting
	 * 		a) remove customer from queue
	 * 		b) serve customer
	 * 		c) return to top
	 * 
	 * If there is no one waiting
	 * 		a) wait (passivate) until someone arrives (who reactivates the VC)
	 * 		b) return to top
	 */
	@Override
	public void lifeCycle() {
	    // the server is always on duty and will never stop working
		while (true) {
			// check if there is someone waiting
			if (model.caseQueue.isEmpty()) {
				// no truck waiting
				model.idleVCQueue.insert(this);
				passivate();
			} else {
				// truck waiting
				LoanApplication nextTruck = model.caseQueue.first();
				model.caseQueue.remove(nextTruck);

				// service
				hold(new TimeSpan(model.getServiceTime(), TimeUnit.MINUTES));
				nextTruck.activate(new TimeSpan(0.0));
			}
		}
	}

}
