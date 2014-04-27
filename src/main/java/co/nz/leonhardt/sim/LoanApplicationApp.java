package co.nz.leonhardt.sim;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;

import co.nz.leonhardt.bpe.BPEM;
import co.nz.leonhardt.bpe.BPEMFacade;
import co.nz.leonhardt.bpe.logs.RAMProcessLogStorage;
import co.nz.leonhardt.sim.event.LoanApplicationModel;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;

/**
 * An event-based loan application simulation.
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
		// create BPEM environment
		BPEM bpem = new BPEMFacade();
		
		// create model and experiment
		Model model = new LoanApplicationModel(bpem);
		Experiment exp = new Experiment("LoanApplicationExperiment",
				TimeUnit.SECONDS, TimeUnit.MINUTES, null);
		// and connect them
		model.connectToExperiment(exp);

		// set experiment parameters
		exp.setShowProgressBar(false);
		TimeInstant stopTime = new TimeInstant(1500, TimeUnit.MINUTES);
		exp.stop(stopTime);
		exp.tracePeriod(new TimeInstant(0), new TimeInstant(500,
				TimeUnit.MINUTES));	// set the period of the trace
		exp.debugPeriod(new TimeInstant(0), new TimeInstant(250,
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
		
		exportLogs(bpem, "simLog.xes");
	}
	
	public static void exportLogs(BPEM bpem, String path) {
		XLog log = bpem.exportLog();
		XSerializer xSer = new XesXmlSerializer();
		
		try(OutputStream fos = new FileOutputStream(path)) {
			xSer.serialize(log, fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
