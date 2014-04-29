package co.nz.leonhardt.bpe.reco;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
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
	public void testRegressionPrediction() {
		PredictionService<Double> ps = new LRCycleTimePredictor();
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
		LROutcomeClassifier ps = new LROutcomeClassifier();
		OutcomeExtractor oe = new OutcomeExtractor();
		
		ps.learn(learnLog);
		
		Outcome trueOutcome = oe.extractMetric(learnLog.get(0));
		ClassificationResult<Outcome> result = ps.predict(learnLog.get(0));
		
		for(PredictionResult<Outcome> pr: result) {
			System.out.println(pr.result + "- " + pr.confidence);
		}
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
	@Ignore("this fails and needs debgging in JSAT")
	public void testRegressionClassificationCV() {
		LROutcomeClassifier ps = new LROutcomeClassifier();
		
		ps.learn(learnLog);
		ps.crossValidate(learnLog);
	}
}
