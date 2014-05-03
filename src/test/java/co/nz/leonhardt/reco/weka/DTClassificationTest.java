package co.nz.leonhardt.reco.weka;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.bpe.categories.Outcome;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.reco.ClassificationResult;
import co.nz.leonhardt.reco.weka.DTOutcomeClassifier;
import co.nz.leonhardt.util.XesUtil;

/**
 * Tests the j48 decision tree based recommender.
 * 
 * @author freddy
 *
 */
public class DTClassificationTest {
	private static XLog learnLog;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		learnLog = XesUtil.parseFrom("/logs/simLog.xes.gz");
	}
	
	@Before
	public void before() {
		Assert.assertNotNull(learnLog);
	}
	
	@Test
	public void testClassification() throws Exception {
		DTOutcomeClassifier ps = new DTOutcomeClassifier();
		OutcomeExtractor oe = new OutcomeExtractor();
		
		ps.learn(learnLog);
		
		
		for(XTrace trace: learnLog) {
			Outcome trueOutcome = oe.extractMetric(trace);
			ClassificationResult<Outcome> classification = ps.predict(trace);
			Assert.assertNotNull(classification);
			System.out.println(classification.results.get(0) + ", [Truth] " + trueOutcome);
			//Assert.assertEquals(realTime, prediction.result, acceptedError);
		}
	}
	
	@Test
	public void testCrossValidation() throws Exception {
		DTOutcomeClassifier ps = new DTOutcomeClassifier();
		ps.crossValidate(learnLog);
	}
}
