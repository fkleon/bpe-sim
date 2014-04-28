package co.nz.leonhardt.sim.event;

import java.util.concurrent.TimeUnit;

import co.nz.leonhardt.bpe.BPEM;
import co.nz.leonhardt.sim.common.BpemEnabledModel;
import desmoj.core.dist.BoolDistBernoulli;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistGamma;
import desmoj.core.dist.ContDistWeibull;
import desmoj.core.dist.DiscreteDist;
import desmoj.core.dist.DiscreteDistPoisson;
import desmoj.core.dist.Distribution;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Queue;
import desmoj.core.simulator.TimeSpan;

public class LoanApplicationModel extends BpemEnabledModel {

	/**
	 * Random number stream used to draw a requested amount for an application.
	 * Describes the money requested for a single loan application.
	 *
	 * See init() method for stream parameters.
	 */
	private ContDistWeibull applicationAmountRequested;

	/**
	 * Random number stream used to draw a submission time for an application.
	 */
	private ContDistGamma applicationSubmissionTime;
	
	/**
	 * Random boolean stream used to draw the condition of an immediately declined
	 * application.
	 */
	private BoolDistBernoulli applicationDeclinedImmediately;
	
	/**
	 * Random number stream used to draw a pre-check time for an application.
	 */
	private DiscreteDistPoisson applicationPreCheckTime;

	protected Queue<LoanApplication> applicationQueue;
	
	/**
	 * Creates a new loan application model with the given
	 * BPEM.
	 * 
	 * @param bpemEnvironment
	 */
	public LoanApplicationModel(BPEM bpemEnvironment) {
		super(bpemEnvironment, null, "LoanApplicationModel", true, true);
		
		this.bpemEnvironment = bpemEnvironment;
	}

	@Override
	public String description() {
		return "A loan application simulation";
	}

	/**
	 * Activates dynamic model components (events).
	 * 
	 * This method is used to place all events or processes on the internal
	 * event list of the simulator which are necessary to start the simulation.
	 * 
	 * In this case, the application generator event will have to be created and
	 * scheduled for the start time of the simulation.
	 */
	@Override
	public void doInitialSchedules() {
		// create the LoanApplicationGeneratorEvent
		LoanApplicationGeneratorEvent applicationGenerator = new LoanApplicationGeneratorEvent(this);

		// schedule for start of simulation
		applicationGenerator.schedule(new TimeSpan(0));
	}

	@Override
	public void init() {
		// Stream for application amounts
		// Exponential Distribution with mean of 13753
		double meanAmountRequested = 13573.3560785512;
		applicationAmountRequested = new ContDistWeibull(this,
				"AmountRequestedStream", meanAmountRequested, 1.0, true, true);
		//applicationAmountRequested = new ContDistWeibull(this,
		//		"AmountRequestedStream", meanAmountRequested, 0.5, true, true);
		//applicationAmountRequested = new ContDistExponential(this,
		//		"AmountRequestedStream", meanAmountRequested, true, false);
		
		// Stream for submission intervals
		double meanArrivalTime = 17.23; // minutes
		applicationSubmissionTime = new ContDistGamma(this,
				"LoanApplicationSubmissionTimeStream", 2, meanArrivalTime, true, false);
		//applicationSubmissionTime = new DiscreteDistPoisson(this,
		//		"LoanApplicationSubmissionTimeStream", 36.0, true, false);

		// Stream for applications declined immediately
		// Bernoulli with 26% probability of declining the application
		applicationDeclinedImmediately = new BoolDistBernoulli(this,
				"ApplicationDeclinedImmediately", 0.26, true, false);
		
		// Stream for pre-check time
		// Discrete Poisson Distribution with mean of 2 minutes TODO check
		applicationPreCheckTime = new DiscreteDistPoisson(this,
				"LoanApplicationPreCheckTimeStream", 2.0, true, false);
		
		// Queue for submitted applications
		applicationQueue = new Queue<LoanApplication>(this, "Application Queue", true, true);
	}
	
	protected Long getApplicationAmountRequested() {
		return applicationAmountRequested.sample().longValue();	
	}
	
	protected Double getApplicationSubmissionTime() {
		return applicationSubmissionTime.sample();
	}

	protected TimeSpan getApplicationSubmissionTimeSpan() {
		return new TimeSpan(getApplicationSubmissionTime(), TimeUnit.MINUTES);
	}
	
	protected Boolean isApplicationDeclinedImmediately() {
		return applicationDeclinedImmediately.sample();
	}
	
	protected Long getApplicationPreCheckTime() {
		return applicationPreCheckTime.sample();
	}
	
	protected TimeSpan getApplicationPreCheckTimeSpan() {
		return new TimeSpan(getApplicationPreCheckTime(), TimeUnit.MINUTES);
	}
	
}
