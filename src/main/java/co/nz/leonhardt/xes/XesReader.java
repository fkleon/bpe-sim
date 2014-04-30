package co.nz.leonhardt.xes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;

public class XesReader {
	
	final static String FINANCIAL_LOG_XES = "/home/freddy/downloads/Master/Event Logs/BPIC'12/logs/financial_log.xes";
	final static String FINANCIAL_LOG_SMALL_XES = "/home/freddy/cloud/dropbox/Uni-shared/MA/code/financial_log_small.xes";
	final static String FINANCIAL_LOG_MXML_GZ = "/home/freddy/downloads/Master/Event Logs/BPIC'12/logs/financial_log.mxml.gz";
	final static String FINANCIAL_LOG_FINAL_XES_GS = "/home/freddy/cloud/dropbox/Uni-shared/MA/data/financial_log_preprocessed_FINAL.xes.gz";
	public static void main(String[] args) throws Exception {
		XParser xesParser = new XesXmlParser();
		XParser xesGzParser = new XesXmlGZIPParser();
		XParser mxmlParser = new XMxmlGZIPParser();
		File xesFile = new File(FINANCIAL_LOG_XES);
		File mxmlFile = new File(FINANCIAL_LOG_MXML_GZ);
		File xesSmallFile = new File(FINANCIAL_LOG_SMALL_XES);
		
		File xesFinalFile = new File(FINANCIAL_LOG_FINAL_XES_GS);
		
		//InputStream xesIS = new FileInputStream(xesFile);
		//InputStream mxmlIS = new FileInputStream(mxmlFile);
		
		long startTime = System.currentTimeMillis();
		
		List<XLog> logs = xesGzParser.parse(xesFinalFile);
		//List<XLog> logs = mxmlParser.parse(mxmlFile);
		
		System.out.println("Parsed " + logs.size() + " logs in " + (System.currentTimeMillis() - startTime) + " ms.");
		
		// weird XML date format, java is not supporting colons in timezone
		//DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); //2009-11-25T19:45:32.345+02:00
	
		for (XLog log: logs) {
			System.out.println("Log size: " + log.size());
			
			//Collection<Date> dates = getDatesFor(log, A_SUBMITTED);
			List<Date> dates = getDatesFor(log);
			long[] intervals = new long[dates.size()-1];
			
			//intervals[0] = dates.get(1).getTime() - dates.get(0).getTime();
			
			
			
			for(int i = 0; i < dates.size() - 1; i++) {
				//System.out.println(dates.get(i));
				//long intervalSecs = date
				intervals[i] = (dates.get(i+1).getTime() - dates.get(i).getTime())/1000;
				
				System.out.println("int [min]: " + (intervals[i] / (60)));
			}
			
			DescriptiveStatistics stats = new DescriptiveStatistics();
			
			for(long interval: intervals) {
				stats.addValue(interval);
			}
			
			System.out.println(stats);
			
			String outputPathAmounts = "/home/freddy/cloud/dropbox/Uni-shared/MA/data/financial_log_submission_intervals.csv";

			try (Writer writer = new BufferedWriter(new FileWriter(outputPathAmounts, false))) {
				for (long amount: intervals) {
					writer.append(amount + ",");
				}
			}
		}
	}
	
	private XParser xesParser = new XesXmlParser();
	
    public XLog readFromFile(String inputFilePath) throws Exception {
    	File xesFile = new File(inputFilePath);
		List<XLog> logs = xesParser.parse(xesFile);
		return logs.iterator().next();
    }
    
    public void writeToFile(String outputFilePath, XLog log) throws IOException {
    	XSerializer xesSerializer = new XesXmlSerializer();
    	OutputStream fos = new FileOutputStream(outputFilePath);
    	try {
			xesSerializer.serialize(log, fos);
		} finally {
			fos.close();
		}
    }
	
	static final String A_SUBMITTED = "A_SUBMITTED";
	static final String ATTR_CONCEPT = "concept:name";
	static final String ATTR_TIMESTAMP = "time:timestamp";

	/**
	 * Get dates for all traces.
	 * 
	 * @param traces
	 * @return
	 */
	private static List<Date> getDatesFor(Collection<XTrace> traces) {
		XTimeExtension timeExt = XTimeExtension.instance();
		List<Date> dates = new ArrayList<>();
	
		for(XTrace trace: traces) {
			
			XEvent event = trace.get(0);
			Date traceDate= timeExt.extractTimestamp(event);
			//XAttribute attr = trace.getAttributes().get("REG_DATE");
			//String reg_date = attr.toString();
			//Date traceDate = DatatypeConverter.parseDateTime(reg_date).getTime();
			
			dates.add(traceDate);
		}
		
		return dates;
	}
	
	/**
	 * Get dates for all events with given concept name.
	 * 
	 * @param traces
	 * @param conceptName
	 * @return
	 */
	private static List<Date> getDatesFor(Collection<XTrace> traces, String conceptName) {
		List<Date> dates = new ArrayList<>();
	
		for(XTrace trace: traces) {
			XAttribute attr = trace.getAttributes().get("AMOUNT_REQ");
			String amount_req = attr.toString();
			
			for(XEvent event: trace) {
				String eventConceptName = getConceptName(event);
								
				if(conceptName.equals(eventConceptName)) {
					// hit
					String eventTimestamp = getTimestamp(event);
					Date eventDate = DatatypeConverter.parseDateTime(eventTimestamp).getTime();
					dates.add(eventDate);
					
					System.out.println(eventDate + ": " + eventConceptName + ", amount: " + amount_req);
				}
			}
		}
		
		return dates;
	}

	private static String getConceptName(XEvent event) {
		XAttribute eventConcept = event.getAttributes().get(ATTR_CONCEPT);
		
		if(eventConcept != null) {
			return eventConcept.toString();
		} else {
			return null;
		}
	}
	
	private static String getTimestamp(XEvent event) {
		XAttribute eventTimestamp = event.getAttributes().get(ATTR_TIMESTAMP);
		
		if(eventTimestamp != null) {
			return eventTimestamp.toString();
		} else {
			return null;
		}
	}
}
