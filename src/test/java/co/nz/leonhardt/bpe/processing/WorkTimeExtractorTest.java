package co.nz.leonhardt.bpe.processing;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.bpe.processing.WorkTimeExtractor;

/**
 * Tests for the work time extractor.
 * 
 * @author freddy
 *
 */
public class WorkTimeExtractorTest {

	/** The factory */
	private static XFactory xFac;
	
	@BeforeClass
	public static void beforeClass() {
		xFac = new XFactoryBufferedImpl();
	}
	
	/**
	 * Builds an event with the given properties.
	 * 
	 * @param name the concept name
	 * @param transition the transition phase
	 * @param relMinutes the date in relation to the first event
	 * @return the event
	 */
	private static XEvent buildEvent(String name, StandardModel transition, Integer relMinutes) {
		XTimeExtension tExt = XTimeExtension.instance();
		XConceptExtension cExt = XConceptExtension.instance();
		XLifecycleExtension lcExt = XLifecycleExtension.instance();
		
		XEvent event = xFac.createEvent();
		cExt.assignName(event, name);
		tExt.assignTimestamp(event, new Date(relMinutes.longValue()*60*1000));
		lcExt.assignStandardTransition(event, transition);
		
		return event;
	}
	
	@Test
	public void testValidSingle() {
		WorkTimeExtractor wte = new WorkTimeExtractor(TimeUnit.MINUTES);
		
		XTrace curTrace;
		XEvent e1, e2, e3, e4, e5, e6;
		Long expected;
		
		// Case A: work\S, work\C
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.START, 0); // matches e2 (2min)
		e2 = buildEvent("work", StandardModel.COMPLETE, 2);
		curTrace.add(e1);
		curTrace.add(e2);
		expected = 2l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
		
		// Case B: work\S, work\C, work\S, work\C
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.START, 0); // matches e2 (2min)
		e2 = buildEvent("work", StandardModel.COMPLETE, 2);
		e3 = buildEvent("work", StandardModel.START, 3); // matches e4 (2min)
		e4 = buildEvent("work", StandardModel.COMPLETE, 5);
		curTrace.add(e1);
		curTrace.add(e2);
		curTrace.add(e3);
		curTrace.add(e4);
		expected = 4l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
	}
	
	@Test
	public void testValidNestedSingle() {
		WorkTimeExtractor wte = new WorkTimeExtractor(TimeUnit.MINUTES);

		XTrace curTrace;
		XEvent e1, e2, e3, e4, e5, e6;
		Long expected;
		
		// Case A: work\S, work\S, work\C, work\C
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.START, 0); // matches e3 (3min)
		e2 = buildEvent("work", StandardModel.START, 2); // matches e4 (3min)
		e3 = buildEvent("work", StandardModel.COMPLETE, 3);
		e4 = buildEvent("work", StandardModel.COMPLETE, 5);
		curTrace.add(e1);
		curTrace.add(e2);
		curTrace.add(e3);
		curTrace.add(e4);
		expected = 6l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
	}
	
	@Test
	public void testInvalidSingle() {
		WorkTimeExtractor wte = new WorkTimeExtractor(TimeUnit.MINUTES);
		
		XTrace curTrace;
		XEvent e1, e2, e3, e4, e5, e6;
		Long expected;
		
		// Case A: work\S
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.START, 0); // no complete
		curTrace.add(e1);
		expected = 0l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
		
		// Case B: work\C
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.COMPLETE, 0); // no start
		curTrace.add(e1);
		expected = 0l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
		
		// Case C: work\C, work\S
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.COMPLETE, 0); // complete before start
		e2 = buildEvent("work", StandardModel.START, 2);
		curTrace.add(e1);
		curTrace.add(e2);
		expected = 0l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
	}
	
	@Test
	public void testValidMultiple() {
		WorkTimeExtractor wte = new WorkTimeExtractor(TimeUnit.MINUTES);

		XTrace curTrace;
		XEvent e1, e2, e3, e4, e5, e6;
		Long expected;
		
		// Case A: work\S, work2\S, work2\C, work\C
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.START, 0); // matches e4 (5min)
		e2 = buildEvent("work2", StandardModel.START, 2); // matches e3 (1min)
		e3 = buildEvent("work2", StandardModel.COMPLETE, 3);
		e4 = buildEvent("work", StandardModel.COMPLETE, 5);
		curTrace.add(e1);
		curTrace.add(e2);
		curTrace.add(e3);
		curTrace.add(e4);
		expected = 6l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
		
		// Case B: work\S, work2\S, work\C, work2\C
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.START, 0); // matches e3 (3min)
		e2 = buildEvent("work2", StandardModel.START, 2); // matches e4 (5min)
		e3 = buildEvent("work", StandardModel.COMPLETE, 3);
		e4 = buildEvent("work2", StandardModel.COMPLETE, 7);
		curTrace.add(e1);
		curTrace.add(e2);
		curTrace.add(e3);
		curTrace.add(e4);
		expected = 8l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
	}
	
	@Test
	public void testValidNestedMulti() {
		WorkTimeExtractor wte = new WorkTimeExtractor(TimeUnit.MINUTES);

		XTrace curTrace;
		XEvent e1, e2, e3, e4, e5, e6, e7, e8;
		Long expected;
		
		// Case A:
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.START, 0); // matches e8 (5min)
		e2 = buildEvent("work", StandardModel.START, 0); // matches e7 (5min)
		e3 = buildEvent("work2", StandardModel.START, 2); // matches e6 (1min)
		e4 = buildEvent("work2", StandardModel.START, 2); // matches e5 (1min)
		e5 = buildEvent("work2", StandardModel.COMPLETE, 3);
		e6 = buildEvent("work2", StandardModel.COMPLETE, 3);
		e7 = buildEvent("work", StandardModel.COMPLETE, 5);
		e8 = buildEvent("work", StandardModel.COMPLETE, 5);
		curTrace.add(e1);
		curTrace.add(e2);
		curTrace.add(e3);
		curTrace.add(e4);
		curTrace.add(e5);
		curTrace.add(e6);
		curTrace.add(e7);
		curTrace.add(e8);
		expected = 12l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
	}
	
	@Test
	public void testInvalidMultiple() {
		WorkTimeExtractor wte = new WorkTimeExtractor(TimeUnit.MINUTES);

		XTrace curTrace;
		XEvent e1, e2, e3, e4, e5, e6;
		Long expected;
		
		// Case A:
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.COMPLETE, 0); // no start
		e2 = buildEvent("work2", StandardModel.START, 2); // matches e3 (1min)
		e3 = buildEvent("work2", StandardModel.COMPLETE, 3);
		e4 = buildEvent("work", StandardModel.COMPLETE, 5);
		curTrace.add(e1);
		curTrace.add(e2);
		curTrace.add(e3);
		curTrace.add(e4);
		expected = 1l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
		
		// Case B:
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.COMPLETE, 0); // complete before start
		e2 = buildEvent("work2", StandardModel.COMPLETE, 2); // complete before start
		e3 = buildEvent("work", StandardModel.START, 3);
		e4 = buildEvent("work2", StandardModel.START, 7);
		curTrace.add(e1);
		curTrace.add(e2);
		curTrace.add(e3);
		curTrace.add(e4);
		expected = 0l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
		
		// Case C:
		curTrace = xFac.createTrace();
		e1 = buildEvent("work", StandardModel.START, 0); // matches e4 (7min)
		e2 = buildEvent("work2", StandardModel.COMPLETE, 2); // complete before start
		e3 = buildEvent("work2", StandardModel.START, 3);
		e4 = buildEvent("work", StandardModel.COMPLETE, 7);
		curTrace.add(e1);
		curTrace.add(e2);
		curTrace.add(e3);
		curTrace.add(e4);
		expected = 7l;
		Assert.assertEquals(expected, wte.extractMetric(curTrace));
	}
}
