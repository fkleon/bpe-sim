package co.nz.leonhardt.reco;

import java.util.concurrent.TimeUnit;

import jsat.regression.LogisticRegression;
import jsat.regression.MultipleLinearRegression;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import co.nz.leonhardt.bpe.processing.CycleTimeExtractor;
import co.nz.leonhardt.reco.PredictionService;
import co.nz.leonhardt.reco.jsat.GenericCycleTimePredictor;
import co.nz.leonhardt.reco.jsat.LRCycleTimePredictor;
import co.nz.leonhardt.util.XesUtil;

public class RecoTest {

	static XLog xLog;
	
	@BeforeClass
	public static void init() throws Exception {
		xLog = XesUtil.parseFrom("/logs/simLog.xes.gz");
	}
	
	@Before
	public void before() {
		Assert.assertNotNull(xLog);
	}
	
	@Test
	public void testCV() throws Exception {
		PredictionService<Double> ps = new LRCycleTimePredictor();//new GenericCycleTimePredictor(new LogisticRegression());
		ps.crossValidate(xLog);
	}
	
	@Test
	public void testRegression() throws Exception {
		PredictionService<Double> ps = new GenericCycleTimePredictor(new MultipleLinearRegression());
		CycleTimeExtractor cte = new CycleTimeExtractor(TimeUnit.MINUTES);
		
		double acceptedError = 500;
		
		ps.learn(xLog);
		
		for(XTrace trace: xLog) {
			Long realTime = cte.extractMetric(trace);
			Double predictedTime = ps.predict(trace);
			Assert.assertNotNull(predictedTime);
			System.out.println("Predicted: " + predictedTime + ", Truth: " + realTime);
			Assert.assertEquals(realTime, predictedTime, acceptedError);
		}
	}
}
