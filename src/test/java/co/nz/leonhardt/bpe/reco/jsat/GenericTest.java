package co.nz.leonhardt.bpe.reco.jsat;

import java.util.concurrent.TimeUnit;

import jsat.classifiers.linear.LinearL1SCD;
import jsat.classifiers.linear.LogisticRegressionDCD;
import jsat.classifiers.linear.SMIDAS;
import jsat.classifiers.svm.DCD;
import jsat.regression.LogisticRegression;
import jsat.regression.MultipleLinearRegression;
import jsat.regression.StochasticRidgeRegression;

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
public class GenericTest {
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
		PredictionService<Double> ps = new GenericCycleTimePredictor(new MultipleLinearRegression());
		CycleTimeExtractor cte = new CycleTimeExtractor(TimeUnit.MINUTES);
		
		double acceptedError = 3;
		
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
	public void testRegressionPredictionCV() {
		LRCycleTimePredictor ps = new LRCycleTimePredictor();
		
		ps.learn(learnLog);
		ps.crossValidate(learnLog);
	}
}
