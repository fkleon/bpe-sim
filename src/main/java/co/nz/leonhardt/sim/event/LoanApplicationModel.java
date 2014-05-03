package co.nz.leonhardt.sim.event;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import co.nz.leonhardt.bpe.BPEM;
import co.nz.leonhardt.sim.common.BpemEnabledModel;
import co.nz.leonhardt.sim.event.init.LoanApplicationGeneratorEvent;
import desmoj.core.advancedModellingFeatures.Res;
import desmoj.core.dist.BoolDistBernoulli;
import desmoj.core.dist.ContDist;
import desmoj.core.dist.ContDistExponential;
import desmoj.core.dist.ContDistGamma;
import desmoj.core.dist.ContDistNormal;
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
	private ContDist applicationAmountRequested;
	private List<Integer> amountSample;

	/**
	 * Random number stream used to draw a submission time for an application.
	 */
	private ContDistGamma applicationSubmissionTime;
	
	/**
	 * Random boolean stream used to draw the condition of an immediately declined
	 * application.
	 */
	private BoolDistBernoulli applicationDeclinedImmediately;
	private BoolDistBernoulli applicationDeclinedAfterAssessment;

	/**
	 * Random number stream used to draw a pre-check time for an application.
	 */
	private DiscreteDistPoisson applicationPreCheckTime;
	
	private ContDistExponential applicationInitialAssessmentTime;
	private ContDistNormal applicationAssessmentTime;

	private ContDistNormal offerCreationTime;
	
	private ContDistNormal customerInitialContactTime;	
	private BoolDistBernoulli customerContactSuccessful;
	private ContDistExponential customerContactAttemptTime;
	private ContDistExponential customerContactTime;

	
	public Queue<LoanApplication> applicationQueue;
	public Queue<Resource> idleResources;
	
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
	
	public void setAmountSample(List<Integer> amounts) {
		this.amountSample = amounts;
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
//		applicationAmountRequested = new ContDistWeibull(this,
//				"AmountRequestedStream", meanAmountRequested, 2.0, true, true);
//		applicationAmountRequested = new ContDistWeibull(this,
//				"AmountRequestedStream", meanAmountRequested, 0.5, true, true);
//		applicationAmountRequested = new ContDistExponential(this,
//				"AmountRequestedStream", 13000.0, true, false);
		applicationAmountRequested = new ContDistGamma(this,
				"AmountRequestedStream", 2, 13000.0, true, false);
		
		// Stream for submission intervals
		double meanArrivalTime = 17.23; // minutes
		applicationSubmissionTime = new ContDistGamma(this,
				"SubmissionTimeStream", 2, meanArrivalTime, true, false);
		//applicationSubmissionTime = new DiscreteDistPoisson(this,
		//		"LoanApplicationSubmissionTimeStream", 36.0, true, false);

		// Stream for applications declined immediately
		// Bernoulli with 26% probability of declining the application
		applicationDeclinedImmediately = new BoolDistBernoulli(this,
				"ApplicationDeclinedImmediately", 0.26, true, false);
		applicationDeclinedAfterAssessment = new BoolDistBernoulli(this,
				"ApplicationDeclinedAfterAssessment", 0.17, true, false);
		
		// Stream for pre-check time
		// Discrete Poisson Distribution with mean of 2 minutes TODO check
		applicationPreCheckTime = new DiscreteDistPoisson(this,
				"PreCheckTimeStream", 2.0, true, false);
		
		applicationInitialAssessmentTime = new ContDistExponential(this,
				"InitialAssessmentTime", 60.0, true, false);
		applicationAssessmentTime = new ContDistNormal(this,
				"AssessmentTimeStream", 20.0, 10.0, true, false);
		applicationAssessmentTime.setNonNegative(true);
		
		offerCreationTime = new ContDistNormal(this,
				"OfferCreationTimeStream", 10.0, 5.0, true, false);
		offerCreationTime.setNonNegative(true);

		customerInitialContactTime = new ContDistNormal(this,
				"AssessmentTimeStream", 30.0, 15.0, true, false);
		customerInitialContactTime.setNonNegative(true);
		customerContactSuccessful = new BoolDistBernoulli(this,
				"CustomerContactSuccessful", 0.60, true, false);
		customerContactAttemptTime = new ContDistExponential(this,
				"CustomerContactAttemptTimeStream", 20.0, true, false);
		customerContactAttemptTime.setNonNegative(true);
		customerContactTime = new ContDistExponential(this,
				"CustomerContactTimeStream", 11.0, true, false);
		// Queue for submitted applications
		applicationQueue = new Queue<LoanApplication>(this, "Application Queue", true, true);
		
		john = new Resource(this, "John");
	}
	
	final Random rand = new Random();
	
	public Long getApplicationAmountRequested() {
		//return applicationAmountRequested.sample().longValue();
		// draw a sample
		return amountSample.get(rand.nextInt(amountSample.size())).longValue();
	}
	
	public Double getApplicationSubmissionTime() {
		return applicationSubmissionTime.sample();
	}

	public TimeSpan getApplicationSubmissionTimeSpan() {
		return new TimeSpan(getApplicationSubmissionTime(), TimeUnit.MINUTES);
	}
	
	public Boolean isApplicationDeclinedImmediately() {
		return applicationDeclinedImmediately.sample();
	}
	
	public Boolean isApplicationDeclinedAfterAssessment() {
		return applicationDeclinedAfterAssessment.sample();
	}
	
	public Long getApplicationPreCheckTime() {
		return applicationPreCheckTime.sample();
	}
	
	public TimeSpan getApplicationPreCheckTimeSpan() {
		return new TimeSpan(getApplicationPreCheckTime(), TimeUnit.MINUTES);
	}
	
	public TimeSpan getApplicationInitialAssessmentTimeSpan() {
		return new TimeSpan(applicationInitialAssessmentTime.sample(), TimeUnit.MINUTES);
	}
	
	public TimeSpan getApplicationAssessmentTimeSpan() {
		return new TimeSpan(applicationAssessmentTime.sample(), TimeUnit.MINUTES);
	}
	
	public TimeSpan getOfferCreationTimeSpan() {
		return new TimeSpan(offerCreationTime.sample(), TimeUnit.MINUTES);
	}
	
	public Boolean isCustomerContactSuccessful() {
		return customerContactSuccessful.sample();
	}
	
	public TimeSpan getCustomerInitialContactTimeSpan() {
		return new TimeSpan(customerInitialContactTime.sample(), TimeUnit.MINUTES);
	}
	public TimeSpan getCustomerContactAttemptTimeSpan() {
		return new TimeSpan(customerContactAttemptTime.sample(), TimeUnit.HOURS);
	}
	
	public TimeSpan getCustomerContactTimeSpan() {
		return new TimeSpan(customerContactTime.sample(), TimeUnit.MINUTES);
	}
	
	private Resource john;
	
	public Resource getAvailableResource() {
		return john; //TODO
	}
}
