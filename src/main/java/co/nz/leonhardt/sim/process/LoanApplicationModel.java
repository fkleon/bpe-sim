package co.nz.leonhardt.sim.process;

import desmoj.core.dist.BoolDistBernoulli;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistUniform;
import desmoj.core.dist.DiscreteDistPoisson;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.TimeSpan;

/**
 * This is the model class. It is the main class of a simple process-oriented
 * model of the loading zone of a container terminal. Trucks arrive at the
 * terminal to load containers. They wait in line until a van carrier (VC) is
 * available to fetch a certain container and load it onto the truck. After
 * loading is completed, the truck leaves the terminal while the van carrier
 * serves the next truck.
 */
public class LoanApplicationModel extends Model {

	protected static int NUM_VC = 2;
	private ContDistExponential applicationSubmissionAmount;
	private DiscreteDistPoisson applicationSubmissionTime;
	private BoolDistBernoulli applicationDeclinedImmediately;
	
	
	private ContDistUniform serviceTime;
	protected ProcessQueue<LoanApplication> caseQueue;
	protected ProcessQueue<VanCarrier> idleVCQueue;

	public LoanApplicationModel() {
		super(null, "LoanApplicationModel", true, true);
	}

	/**
	 * Returns a description of the model to be used in the report.
	 * 
	 * @return model description as a string
	 */
	@Override
	public String description() {
		return "This model describes.."; //TODO
	}

	/**
	 * Activates dynamic model components (simulation processes).
	 * 
	 * This method is used to place all events or processes on the internal
	 * event list of the simulator which are necessary to start the simulation.
	 * 
	 * In this case, the truck generator and the van carrier(s) have to be
	 * created and activated.
	 */
	@Override
	public void doInitialSchedules() {
		// create and activate the van carrier(s)
		for (int i = 0; i < NUM_VC; i++) {
			VanCarrier vanCarrier = new VanCarrier(this);
			vanCarrier.activate(new TimeSpan(0));
			// Use TimeSpan to activate a process after a span of time relative
			// to actual simulation time,
			// or use TimeInstant to activate the process at an absolute point
			// in time.
		}

		// create and activate the truck generator process
		LoanApplicationGenerator generator = new LoanApplicationGenerator(this, "LoanSubmission",
				false);
		generator.activate(new TimeSpan(0));
	}

	/**
	 * Initialises static model components like distributions and queues.
	 */
	@Override
	public void init() {
		// initialise the serviceTimeStream
		// Parameters:
		// this = belongs to this model
		// "ServiceTimeStream" = the name of the stream
		// 3.0 = minimum time in minutes to deliver a container
		// 7.0 = maximum time in minutes to deliver a container
		// true = show in report?
		// false = show in trace?
		serviceTime = new ContDistUniform(this, "ServiceTimeStream", 3.0, 7.0,
				true, false);

		// initialise the truckArrivalTimeStream
		// Parameters:
		// this = belongs to this model
		// "TruckArrivalTimeStream" = the name of the stream
		// 3.0 = mean time in minutes between arrival of trucks
		// true = show in report?
		// false = show in trace?
		applicationSubmissionAmount = new ContDistExponential(this,
				"LoanApplicationSubmissionAmountStream", 13573.3560785512, true, false);
		applicationSubmissionTime = new DiscreteDistPoisson(this,
				"LoanApplicationSubmissionTimeStream", 36.0, true, false);

		applicationDeclinedImmediately = new BoolDistBernoulli(this,
				"ApplicationDeclinedImmediately", 0.26, true, false);
		
		// necessary because an inter-arrival time can not be negative, but
		// a sample of an exponential distribution can...
		applicationSubmissionAmount.setNonNegative(true);
		applicationSubmissionTime.setNonNegative(true);

		// initialise the truckQueue
		// Parameters:
		// this = belongs to this model
		// "Truck Queue" = the name of the Queue
		// true = show in report?
		// true = show in trace?
		caseQueue = new ProcessQueue<LoanApplication>(this, "Application Queue", true, true);

		// initialise the idleVCQueue
		// Parameters:
		// this = belongs to this model
		// "idle VC Queue" = the name of the Queue
		// true = show in report?
		// true = show in trace?
		idleVCQueue = new ProcessQueue<VanCarrier>(this, "idle VC Queue", true,
				true);
	}

	/**
	 * Returns a sample of the random stream used to determine the time needed
	 * to fetch the container for a truck from the storage area and the time the
	 * VC needs to load it onto the truck.
	 * 
	 * @return double a serviceTime sample
	 */
	public double getServiceTime() {
		return serviceTime.sample();
	}

	/**
	 * Returns a sample of the random stream used to determine the next application's
	 * amount.
	 * 
	 * @return double a sample
	 */
	public double getApplicationSubmissionAmount() {
		return applicationSubmissionAmount.sample();
	}
	
	public double getCaseSubmissionTime() {
		return applicationSubmissionTime.sample();
	}
	
	public boolean getApplicationDeclinedImmediately() {
		return applicationDeclinedImmediately.sample();
	}

}
