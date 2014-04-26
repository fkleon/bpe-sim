package co.nz.leonhardt.master.bpe.processing;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.util.XesUtil;

public class CategoricalExtractorTest {
	
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
	
	@Test
	//TODO
	public void testOutcomeExtractor() {
		String expectedOutcome = null;
		
		// test extract amount
		OutcomeExtractor oEx = new OutcomeExtractor();
		//String outcome = arEx.extractMetric(testTrace);

		//Assert.assertEquals(expectedOutcome, outcome);
	}
}
