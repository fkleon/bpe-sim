package co.nz.leonhardt.sim.model.event.init;

import co.nz.leonhardt.bpe.logs.CaseLog;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.sim.model.entity.LoanApplication;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.TimeSpan;

/**
 * This class represents an entity (and event) source, which continually
 * generates applications (and their submission events) in order to keep the
 * simulation running.
 * 
 * It will create a new application, schedule its submission at the IT system
 * (i.e. create and schedule an submission event) and then schedule itself for
 * the point in time when the next loan application is due.
 */
public class LoanApplicationGeneratorEvent extends ExternalEvent {

	private SimulationModel model; 
	
	public LoanApplicationGeneratorEvent(SimulationModel owner) {
		super(owner, "Loan Application Generator", true);
		
		this.model = owner;
	}
	
	/**
	 * The eventRoutine() describes the generation of a new application.
	 * 
	 * It creates a new application, a new ApplicationSubmissionEvent and
	 * schedules itself again for the next new application generation.
	 */
	@Override
	public void eventRoutine() {
		// draw requested amount
		Long amountRequested = model.getApplicationAmountRequested();
		
		// create a new application
		LoanApplication application = new LoanApplication(model, amountRequested, presentTime().getTimeAsDate());
		fireCaseLog(application);
		// create a new application submission event
		SubmissionEvent applicationSubmission = new SubmissionEvent(model);
		
		// and schedule it for the current point in time
		applicationSubmission.schedule(application, new TimeSpan(0));
		
		// schedule this application generator again for the next truck arrival time
		schedule(model.getApplicationSubmissionTimeSpan());
		// from inside to outside...
		// draw a new inter-arrival time value
		// wrap it in a TimeSpan object
		// and schedule this event for the current point in time + the
		// inter-arrival time
		
	}
	
	/**
	 * Starts a new trace for the given loan application case.
	 * 
	 * @param la
	 */
	private void fireCaseLog(LoanApplication la) {
		CaseLog caseLog = new CaseLog(la.getUuid(), la.getStartDate());
		caseLog.setAmountRequested(la.getAmountRequested());
		
		model.getBpemEnvironment().startTrace(caseLog);
	}

}
