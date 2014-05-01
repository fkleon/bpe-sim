package co.nz.leonhardt.bpe.reco.jsat;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import co.nz.leonhardt.bpe.categories.Outcome;
import co.nz.leonhardt.bpe.processing.CycleTimeExtractor;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.bpe.reco.ClassificationResult;
import co.nz.leonhardt.bpe.reco.PredictionResult;
import co.nz.leonhardt.bpe.reco.PredictionService;
import co.nz.leonhardt.bpe.reco.jsat.LRCycleTimePredictor;
import co.nz.leonhardt.bpe.reco.jsat.LROutcomeClassifier;
import co.nz.leonhardt.util.XesUtil;

/**
 * Tests the regression-based recommender.
 * 
 * @author freddy
 *
 */
public class LogisticRegressionTest {
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
	public void testRegressionPrediction() throws Exception {
		PredictionService<Double> ps = new LRCycleTimePredictor();
		CycleTimeExtractor cte = new CycleTimeExtractor(TimeUnit.MINUTES);
		
		double acceptedError = 1.5;
		
		ps.learn(learnLog);
		
		for(XTrace trace: learnLog) {
			Long realTime = cte.extractMetric(trace);
			Double predictedTime = ps.predict(trace);
			Assert.assertNotNull(predictedTime);
			System.out.println("Predicted: " + predictedTime + ", Truth: " + realTime);
			Assert.assertEquals(realTime, predictedTime, acceptedError);
		}
	}
	
	@Test
	public void testRegressionClassification() {
		LROutcomeClassifier ps = new LROutcomeClassifier();
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
	
	@Test
	@Ignore("this fails and needs debugging in JSAT")
	public void testRegressionClassificationCV() {
		LROutcomeClassifier ps = new LROutcomeClassifier();
		
		ps.learn(learnLog);
		ps.crossValidate(learnLog);
	}
	
	@Test
	public void testRegressionPredictionCV() {
		LRCycleTimePredictor ps = new LRCycleTimePredictor();
		
		ps.learn(learnLog);
		ps.crossValidate(learnLog);
	}
}
