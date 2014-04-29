package co.nz.leonhardt.bpe.reco;


import org.deckfour.xes.model.XLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.bpe.categories.Outcome;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.bpe.reco.PredictionService;
import co.nz.leonhardt.bpe.reco.javaml.KNNOutcomeClassifier;
import co.nz.leonhardt.util.XesUtil;

/**
 * Tests the kmeans-based recommender.
 * 
 * @author freddy
 *
 */
public class KNNClassificationTest {
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
	public void testClassification() {
		KNNOutcomeClassifier ps = new KNNOutcomeClassifier();
		OutcomeExtractor oe = new OutcomeExtractor();
		
		ps.learn(learnLog);
		
		Outcome trueOutcome = oe.extractMetric(learnLog.get(0));
		Outcome predictedOutcome = ps.predict(learnLog.get(0)).result;
		Assert.assertNotNull(predictedOutcome);
		Assert.assertEquals(trueOutcome, predictedOutcome);
		System.out.println("Predicted: " + predictedOutcome + ", Truth: " + trueOutcome);

		
		trueOutcome = oe.extractMetric(learnLog.get(2));
		predictedOutcome = ps.predict(learnLog.get(2)).result;
		Assert.assertNotNull(predictedOutcome);
		Assert.assertEquals(trueOutcome, predictedOutcome);
		System.out.println("Predicted: " + predictedOutcome + ", Truth: " + trueOutcome);
	}
	
	@Test
	public void testCrossValidation() {
		KNNOutcomeClassifier ps = new KNNOutcomeClassifier();
		
		//ps.learn(learnLog);
		ps.crossValidate(learnLog);
	}
}
