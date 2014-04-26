package co.nz.leonhardt.bpe.processing;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.logs.EventLog;
import co.nz.leonhardt.bpe.util.TimeUtil;

/**
 * Extracts the cycle time from a given trace.
 * 
 * @author freddy
 *
 */
public class CycleTimeExtractor implements NumericalMetricExtractor<Long> {

	private final TimeUnit timeUnit;
	
	/**
	 * Creates a new cycle time extractor which returns the cycle
	 * time of a trace in the given time unit.
	 * 
	 * @param timeUnit
	 */
	public CycleTimeExtractor(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
	
	@Deprecated
	private Long extractMetric(List<EventLog> eLogs) {
		int numLogs = eLogs.size();
		Date startDate = eLogs.get(0).getTimestamp();
		Date endDate = eLogs.get(numLogs-1).getTimestamp();
		return TimeUtil.calcTimeDiff(startDate, endDate, this.timeUnit);
	}
	
	@Override
	public Long extractMetric(XTrace trace) {
		int numLogs = trace.size();
		XTimeExtension tExt = XTimeExtension.instance();
		Date startDate = tExt.extractTimestamp(trace.get(0));
		Date endDate = tExt.extractTimestamp(trace.get(numLogs-1));
		//TODO fail early if date not available.
		return TimeUtil.calcTimeDiff(startDate, endDate, this.timeUnit);
	}

	@Override
	public String getNumericName() {
		return "CycleTime";
	}
	

}
