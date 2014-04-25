package co.nz.leonhardt.sim.event;

import java.util.concurrent.TimeUnit;

import co.nz.leonhardt.bpe.EventRepository;
import co.nz.leonhardt.bpe.logs.CaseLog;
import desmoj.core.simulator.ExternalEvent;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeSpan;

/**
 * This class represents an entity (and event) source, which continually
 * generates applications (and their submission events) in order to keep the
 * simulation running.
 * 
 * It will create a new application, schedule its submission at the IT system
 * (i.e. create and schedule an submission event) and then schedule itself for
 * the point in time when the next truck appication is due.
 */
public class LoanApplicationGeneratorEvent extends ExternalEvent {

	public LoanApplicationGeneratorEvent(Model owner) {
		super(owner, "LoanApplication Generator", true);
	}
	
	/**
	 * The eventRoutine() describes the generation of a new application.
	 * 
	 * It creates a new application, a new ApplicationSubmissionEvent and
	 * schedules itself again for the next new application generation.
	 */
	@Override
	public void eventRoutine() {
		// get a reference to the model
		LoanApplicationModel model = (LoanApplicationModel) getModel();
	
		// draw requested amount
		Double amountRequested = model.getApplicationAmountRequested();
		
		// create a new application
		LoanApplication application = new LoanApplication(model, amountRequested, presentTime().getTimeAsDate());
		fireCaseLog(application);
		// create a new application submission event
		LoanApplicationSubmissionEvent applicationSubmission = new LoanApplicationSubmissionEvent(model);
		
		// and schedule it for the current point in time
		applicationSubmission.schedule(application, new TimeSpan(0));

		// schedule this application generator again for the next truck arrival time
		schedule(new TimeSpan(model.getApplicationSubmissionTime(), TimeUnit.MINUTES));
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
	protected void fireCaseLog(LoanApplication la) {
		CaseLog caseLog = new CaseLog(la.getUuid(), la.getStartDate());
		caseLog.setAmountRequested(la.getAmountRequested());
		
		EventRepository.getInstance().startTrace(caseLog);
	}

}
