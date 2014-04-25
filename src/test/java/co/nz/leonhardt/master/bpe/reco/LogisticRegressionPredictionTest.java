package co.nz.leonhardt.master.bpe.reco;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.bpe.categories.Outcome;
import co.nz.leonhardt.bpe.processing.CycleTimeExtractor;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.bpe.reco.CycleTimePredictor;
import co.nz.leonhardt.bpe.reco.OutcomeClassifier;
import co.nz.leonhardt.bpe.reco.PredictionService;
import co.nz.leonhardt.util.XesUtil;

/**
 * Tests the regression-based recommender.
 * 
 * @author freddy
 *
 */
public class LogisticRegressionPredictionTest {
	private static XLog learnLog;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		learnLog = XesUtil.parseFrom("/logs/regression_test_log.xes");
	}
	
	@Before
	public void before() {
		Assert.assertNotNull(learnLog);
	}
	
	@Test
	public void testRegressionPrediction() {
		PredictionService<Double> ps = new CycleTimePredictor();
		CycleTimeExtractor cte = new CycleTimeExtractor(TimeUnit.MINUTES);
		
		ps.learn(learnLog);
		
		Long realTime = cte.extractMetric(learnLog.get(0));
		Double predictedTime = ps.predict(learnLog.get(0));
		Assert.assertNotNull(predictedTime);
		System.out.println("Predicted: " + predictedTime + ", Truth: " + realTime);
		
		realTime = cte.extractMetric(learnLog.get(2));
		predictedTime = ps.predict(learnLog.get(2));
		Assert.assertNotNull(predictedTime);
		System.out.println("Predicted: " + predictedTime + ", Truth: " + realTime);
	}
	
	@Test
	public void testRegressionClassification() {
		PredictionService<Outcome> ps = new OutcomeClassifier();
		OutcomeExtractor oe = new OutcomeExtractor();
		
		ps.learn(learnLog);
		
		Outcome trueOutcome = oe.extractMetric(learnLog.get(0));
		Outcome predictedOutcome = ps.predict(learnLog.get(0));
		Assert.assertNotNull(predictedOutcome);
		Assert.assertEquals(trueOutcome, predictedOutcome);
		System.out.println("Predicted: " + predictedOutcome + ", Truth: " + trueOutcome);

		
		trueOutcome = oe.extractMetric(learnLog.get(2));
		predictedOutcome = ps.predict(learnLog.get(2));
		Assert.assertNotNull(predictedOutcome);
		Assert.assertEquals(trueOutcome, predictedOutcome);
		System.out.println("Predicted: " + predictedOutcome + ", Truth: " + trueOutcome);
	}
}
