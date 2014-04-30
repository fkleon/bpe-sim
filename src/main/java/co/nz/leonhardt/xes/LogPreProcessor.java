package co.nz.leonhardt.xes;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XElement;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

/**
 * Log preprocessor for financial logs from BPIC 2012.
 * 
 * @author freddy
 *
 */
public class LogPreProcessor {

	final static String ROOT_PATH = "/home/freddy/downloads/Master/Event Logs/BPIC'12/";
	final static String FINANCIAL_LOG_XES = ROOT_PATH + "logs/financial_log.xes";
	final static String FINANCIAL_LOG_SMALL_XES = ROOT_PATH + "logs/financial_log_small.xes";

	final static Logger l = Logger.getLogger("LogPreProcessor");
	
	public static void main(String... args) throws Exception {
		XesReader xesReader = new XesReader();
		EventNameTranslator ent = new EventNameTranslator();
		
		l.info("Reading log from " + FINANCIAL_LOG_XES);
		
		XLog log = xesReader.readFromFile(FINANCIAL_LOG_XES);
		XLog processedLog = (XLog) log.clone();
		processedLog.clear();
		
		l.info("Processing log..");

		List<String> amounts = new ArrayList<>(14000);
		
		int i = 0;
		for (XTrace trace: log) {
			//XTrace processedTrace = processTrace(trace);
			//processedLog.add(processedTrace);
			
			XAttribute attr = trace.getAttributes().get("AMOUNT_REQ");
			String amount_req = attr.toString();
			System.out.println(amount_req);

			amounts.add(amount_req);
			
			if(i++ == 50) {
				//break;
			}
		}
		
		String outputPath = ROOT_PATH + "logs/financial_log_processed.xes";
		
		l.info("Writing log to " + outputPath);

		//xesReader.writeToFile(outputPath, processedLog);
		
		String outputPathAmounts = ROOT_PATH + "logs/financial_log_amounts.csv";

		try (Writer writer = new BufferedWriter(new FileWriter(outputPathAmounts, false))) {
			for (String amount: amounts) {
				writer.append(amount + ",");
			}
		}
	}
	
	private static XesHelper xesHelper = new XesHelper();
	
	private static XTrace processTrace(XTrace trace) {
		l.info("Processing trace " + xesHelper.getConceptName(trace));
		
		XTrace processedTrace = new XTraceImpl(cloneAttributes(trace));
		
		for (XEvent event: trace) {
			XEvent processedEvent = processEvent(event);
			processedTrace.add(processedEvent);
		}
		
		return processedTrace;
	}
	
	private static XEvent processEvent(XEvent event) {
		l.info("Processing trace " + xesHelper.getConceptName(event));

		XEvent processedEvent = new XEventImpl(cloneAttributes(event));
		
		return processedEvent;
		
	}
	
	private static XAttributeMap cloneAttributes(XElement elem) {
		return (XAttributeMap) elem.getAttributes().clone();
	}
	
}
