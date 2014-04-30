package co.nz.leonhardt.bpe.reco.weka;


import org.deckfour.xes.model.XLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.bpe.categories.Outcome;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.bpe.reco.ClassificationResult;
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
		learnLog = XesUtil.parseFrom("/logs/simLog.xes");
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
		
		Outcome trueOutcome = oe.extractMetric(learnLog.get(0));
		ClassificationResult<Outcome> result = ps.predict(learnLog.get(0));
		System.out.println("Predicted: " + result.getBestResult() + ", Truth: " + trueOutcome);
		Assert.assertNotNull(result.getBestResult());
		Assert.assertEquals(trueOutcome, result.getBestResult());

		trueOutcome = oe.extractMetric(learnLog.get(2));
		result = ps.predict(learnLog.get(2));
		System.out.println("Predicted: " + result.getBestResult() + ", Truth: " + trueOutcome);
		Assert.assertNotNull(result.getBestResult());
		Assert.assertEquals(trueOutcome, result.getBestResult());

		trueOutcome = oe.extractMetric(learnLog.get(learnLog.size()-1));
		result = ps.predict(learnLog.get(learnLog.size()-1));
		System.out.println("Predicted: " + result.getBestResult() + ", Truth: " + trueOutcome);
		Assert.assertNotNull(result.getBestResult());
		Assert.assertEquals(trueOutcome, result.getBestResult());
	}
}
