package co.nz.leonhardt.sim.process;

import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;

/**
 * A process-based simulation.
 * 
 * @author freddy
 *
 */
public class LoanApplicationApp {
	
	/**
	 * Runs the model.
	 *
	 * @param args is an array of command-line arguments (will be ignored here)
	 */
	public static void main(String[] args) {
		// create model and experiment
		LoanApplicationModel model = new LoanApplicationModel();
		Experiment exp = new Experiment("LoanApplicationExperiment",
				TimeUnit.MINUTES, TimeUnit.MINUTES, null);
		// and connect them
		model.connectToExperiment(exp);

		// set experiment parameters
		exp.setShowProgressBar(true);
		TimeInstant stopTime = new TimeInstant(5000, TimeUnit.MINUTES);
		exp.stop(stopTime);
		exp.tracePeriod(new TimeInstant(0), new TimeInstant(500,
				TimeUnit.MINUTES));	// set the period of the trace
		exp.debugPeriod(new TimeInstant(0), new TimeInstant(50,
				TimeUnit.MINUTES)); // and debug output

		// start experiment
		exp.start();

		   // --> now the simulation is running until it reaches its end criterion
		   // ...
		   // ...
		   // <-- afterwards, the main thread returns here
		
		// generate report and shut everything off
		exp.report();
		exp.finish();
	}
}
