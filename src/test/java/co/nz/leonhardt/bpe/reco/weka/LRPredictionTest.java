package co.nz.leonhardt.bpe.reco.weka;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.bpe.processing.CycleTimeExtractor;
import co.nz.leonhardt.bpe.reco.PredictionResult;
import co.nz.leonhardt.util.XesUtil;

/**
 * Tests the logistic regression based predictor.
 * 
 * @author freddy
 *
 */
public class LRPredictionTest {
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
	public void testPrediction() throws Exception {
		LRCycleTimePredictor ps = new LRCycleTimePredictor();
		CycleTimeExtractor cte = new CycleTimeExtractor(TimeUnit.MINUTES);
		
		ps.learn(learnLog);
		
		double acceptedError = 10000.0; 
		
		for(XTrace trace: learnLog) {
			Long realTime = cte.extractMetric(trace);
			PredictionResult<Double> prediction = ps.predict(trace);
			Assert.assertNotNull(prediction);
			System.out.println(prediction + ", [Truth] " + realTime);
			Assert.assertEquals(realTime, prediction.result, acceptedError);
		}
	}
	
	@Test
	public void testCrossValidation() throws Exception {
		LRCycleTimePredictor ps = new LRCycleTimePredictor();
		ps.crossValidate(learnLog);
	}
}
