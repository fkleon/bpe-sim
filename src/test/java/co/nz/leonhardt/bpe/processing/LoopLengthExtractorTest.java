package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.util.XesUtil;

public class LoopLengthExtractorTest {
	
	public static XLog testLog;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		testLog = XesUtil.parseFrom("/logs/loop_extraction_test_log.xes");
	}
	
	@Before
	public void before() {
		Assert.assertNotNull(testLog);
	}
	
	@Test
	public void testLCExtractor() {
		LoopLengthExtractor lce = new LoopLengthExtractor();
		
		Integer longestLoop = lce.extractMetric(testLog.get(0));
		Assert.assertNotNull(longestLoop);
		Assert.assertEquals(2, longestLoop.intValue());
		
		longestLoop = lce.extractMetric(testLog.get(1));
		Assert.assertNotNull(longestLoop);
		Assert.assertEquals(3, longestLoop.intValue());
		
		longestLoop = lce.extractMetric(testLog.get(2));
		Assert.assertNotNull(longestLoop);
		Assert.assertEquals(0, longestLoop.intValue());
	}
}
