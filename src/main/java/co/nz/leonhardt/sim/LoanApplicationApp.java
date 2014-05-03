package co.nz.leonhardt.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
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
	public static void main(String[] args) throws IOException {
		// create BPEM environment
		BPEM bpem = new BPEMFacade();
		
		// create model and experiment
		LoanApplicationModel model = new LoanApplicationModel(bpem);
		Experiment exp = new Experiment("LoanApplicationExperiment",
				TimeUnit.SECONDS, TimeUnit.MINUTES, null);
		model.setAmountSample(loadAmounts());
		// and connect them
		model.connectToExperiment(exp);

		// set experiment parameters
		exp.setShowProgressBar(false);
		TimeInstant stopTime = new TimeInstant(4*7*24*60, TimeUnit.MINUTES);
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
		
		exportLogs(bpem, "simLog.xes.gz");
	}
	
	/**
	 * Reads a sample of amounts.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static List<Integer> loadAmounts() throws IOException {
		List<Integer> amounts = new ArrayList<>(13088);
		InputStream is = LoanApplicationApp.class.getResourceAsStream("/financial_log_amounts_transp.csv");
		try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
		    for(String line; (line = br.readLine()) != null; ) {
		        // process the line.
		    	amounts.add(Integer.parseInt(line));
		    }
		    // line is not visible here.
		}
		return amounts;
	}
	
	/**
	 * Exports the log from the given BPEM to the given path.
	 * 
	 * @param bpem
	 * @param path
	 */
	public static void exportLogs(BPEM bpem, String path) {
		XLog log = bpem.exportLog();
		XSerializer xSer = new XesXmlGZIPSerializer();
		
		try(OutputStream fos = new FileOutputStream(path)) {
			xSer.serialize(log, fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
