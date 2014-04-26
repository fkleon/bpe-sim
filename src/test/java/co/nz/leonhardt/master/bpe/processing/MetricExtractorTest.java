package co.nz.leonhardt.master.bpe.processing;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.bpe.processing.AmountRequestedExtractor;
import co.nz.leonhardt.bpe.processing.CycleTimeExtractor;
import co.nz.leonhardt.util.XesUtil;

/**
 * Tests the metric extractors.
 * 
 * @author freddy
 *
 */
public class MetricExtractorTest {
	
	public static XTrace testTrace;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		XLog xLog = XesUtil.parseFrom("/logs/extraction_test_log.xes");
		testTrace = xLog.get(0);
	}
	
	@Before
	public void before() {
		Assert.assertNotNull(testTrace);
	}

	@After
	public void after() {
		
	}
	
	@AfterClass
	public static void afterClass() {
		
	}
	
	@Test
	public void testCycleTimeExtractor() {
		long expectedCycleTime = 3; // minutes
		
		// test extract minutes
		CycleTimeExtractor ctEx = new CycleTimeExtractor(TimeUnit.MINUTES);
		long cycleTime = ctEx.extractMetric(testTrace);

		Assert.assertEquals(expectedCycleTime, cycleTime);
		
		// test extract seconds
		expectedCycleTime = 180; // seconds
		ctEx = new CycleTimeExtractor(TimeUnit.SECONDS);
		cycleTime = ctEx.extractMetric(testTrace);

		Assert.assertEquals(expectedCycleTime, cycleTime);
	}
	
	@Test
	public void testAmountRequestedExtractor() {
		double expectedAmount = 38643.48456129144;
		
		// test extract amount
		AmountRequestedExtractor arEx = new AmountRequestedExtractor();
		double amount = arEx.extractMetric(testTrace);

		Assert.assertEquals(expectedAmount, amount, 0.0);
	}
}
