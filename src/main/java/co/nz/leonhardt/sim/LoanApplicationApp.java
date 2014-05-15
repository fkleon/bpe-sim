package co.nz.leonhardt.sim;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;

import co.nz.leonhardt.bpe.BPEM;
import co.nz.leonhardt.bpe.BPEMFacade;
import co.nz.leonhardt.bpe.categories.Outcome;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.reco.PredictionResult;
import co.nz.leonhardt.sim.model.SimulationModel;
import co.nz.leonhardt.util.XesUtil;
import desmoj.core.simulator.Experiment;
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
	public static void main(String[] args) throws Exception {
		// create BPEM environment
		BPEM bpem = new BPEMFacade(loadTrainingLog());
		//BPEM bpem = new BPEMFacade();

		// create model and experiment
		SimulationModel model = new SimulationModel(bpem);
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
		
		// generate report and shut everything off
		exp.report();
		exp.finish();
		
		exportLogs(bpem, "simLog.xes.gz");
		exportPredictions(bpem, "predictions.csv");
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
	
	public static XLog loadTrainingLog() throws Exception {
		return XesUtil.parseFrom("/simLogClean.xes.gz");
	}
	
	/**
	 * Exports the log from the given BPEM to the given path.
	 * 
	 * @param bpem
	 * @param path
	 */
	public static void exportLogs(BPEM bpem, String path) {
		XLog log = bpem.exportCompletedLog();
		XSerializer xSer = new XesXmlGZIPSerializer();
		
		try(OutputStream fos = new FileOutputStream(path)) {
			xSer.serialize(log, fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Exports the prediction time time from the given BPEM as CSV to the given path.
	 * 
	 * @param bpem the {@link BPEM} to query
	 * @param path the path to export the CSV to
	 * @throws IOException
	 */
	public static void exportPredictions(BPEM bpem, String path) throws IOException {
		XLog log = bpem.exportCompletedLog();
		
		Map<String, String> trueOutcomes = new HashMap<>();
		OutcomeExtractor oe = new OutcomeExtractor();
		XConceptExtension xe = XConceptExtension.instance();
		for(XTrace trace: log) {
			String uuid = xe.extractName(trace);
			Outcome outcome = oe.extractMetric(trace);	
			trueOutcomes.put(uuid, outcome.toString());
		}
		
		Map<String, Collection<PredictionResult<?>>> predictions = bpem.exportPredictionTimeline();
		
		FileWriter fw = new FileWriter("/home/freddy/predictionTimeline.csv");
		
		for(Entry<String, Collection<PredictionResult<?>>> entry: predictions.entrySet()) {
			String caseUuid = entry.getKey();
			if(!trueOutcomes.containsKey(caseUuid)) {
				continue;
			}
			String trueOutcome = trueOutcomes.get(caseUuid);
			System.out.println("Prediction Timeline for: " + entry.getKey() + ", true outcome: " + trueOutcome);
			StringBuilder confidence = new StringBuilder();
			StringBuilder truth = new StringBuilder();
			StringBuilder confTruth = new StringBuilder();
			for(PredictionResult<?> result: entry.getValue()) {
				boolean isTruth = result.result.toString().equals(trueOutcome);
				//String append = result.result.toString().equals(trueOutcome) ? "CORRECT" : "WRONG";
				//System.out.println(result + ": " +append);
				truth.append(isTruth).append(",");
				confidence.append(result.confidence).append(",");
				
				confTruth.append(result.confidence).append(",").append(isTruth).append(",");
			}
			
			fw.append(confTruth.toString());
			fw.append(System.getProperty( "line.separator" ));
			//fw.append(confidence.toString());
			//fw.append(System.getProperty( "line.separator" ));
			//fw.append(truth.toString());
			//fw.append(System.getProperty( "line.separator" ));

			//System.out.println(confidence.toString());
			//System.out.println(truth.toString());
		}
		
		fw.flush();
		fw.close();
	}
}
