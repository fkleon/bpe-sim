package co.nz.leonhardt.bpe.reco;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jsat.DataSet;
import jsat.classifiers.DataPoint;
import jsat.datatransform.DataTransformProcess;
import jsat.datatransform.PolynomialTransform;
import jsat.datatransform.StandardizeTransform;
import jsat.regression.LogisticRegression;
import jsat.regression.RegressionDataSet;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.processing.AmountRequestedExtractor;
import co.nz.leonhardt.bpe.processing.CycleTimeExtractor;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.bpe.processing.TraceLengthExtractor;

/**
 * A cycle time predictor.
 * 
 * Uses logistic regression.
 * 
 * @author freddy
 *
 */
public class CycleTimePredictor implements PredictionService<Double> {

	//private final EventProcessor ep;
	
	private final LogisticRegression lr;
	
	/** The factory to create data points. */
	private final DataPointFactory dpf;
	
	private final DataTransformProcess dtp;
	
	/**
	 * Creates a new cycle time predictor.
	 */
	public CycleTimePredictor() {
		//ep = new EventProcessor();
		lr = new LogisticRegression();
		
		dpf = DataPointFactory.create()
				.withNumerics(
					//new BiasMetricExtractor(),
					new CycleTimeExtractor(TimeUnit.MINUTES), // Target variable first!
					new TraceLengthExtractor(), 
					new AmountRequestedExtractor())
				.withCategories(
					new OutcomeExtractor());
		
		dtp = new DataTransformProcess(
				//new StandardizeTransform.StandardizeTransformFactory(),
				new PolynomialTransform.PolyTransformFactory(2));
	}
	
	@Override
	public void learn(XLog logs) {
		List<DataPoint> data = new ArrayList<>(logs.size());
		
		//PolynomialTransform trans = new PolynomialTransform(1);

		for(XTrace trace: logs) {
			DataPoint dp = dpf.extractDataPoint(trace);
			data.add(dp);
		}
		
		//TODO: this assumes cycle time is always in the 1st place
		RegressionDataSet dataSet = new RegressionDataSet(data, 0);
		
		// apply transformations
		//dtp.learnApplyTransforms(dataSet);
		//printSet(dataSet);
		
		lr.train(dataSet);
		
		System.out.println("learned coefficents: " + lr.getCoefficents());
		System.out.println("bias: " + lr.getBias());
	}
	
	@Deprecated
	private void poly(DataSet dataSet) {
		PolynomialTransform pf = new PolynomialTransform(3);
		dataSet.applyTransform(pf);
		
		printSet(dataSet);
	}

	@Deprecated
	private void standardize(DataSet dataSet) {
		StandardizeTransform st = new StandardizeTransform(dataSet);
		System.out.println("num num vars: "+ dataSet.getNumNumericalVars());
		dataSet.applyTransform(st);
		
		printSet(dataSet);
	}
	
	@Override
	public Double predict(XTrace partialTrace) {
		DataPoint dp = dpf.extractDataPoint(partialTrace);
		
		// apply transformations
		//dp = dtp.transform(dp);
		
		return lr.regress(dp);
	}
	
	private void printSet(DataSet dataSet) {
		System.out.println("Data Points:");
		for(DataPoint dp: dataSet.getDataPoints()) {
			System.out.println(dp);
		}
		
		if(dataSet instanceof RegressionDataSet) {
			System.out.println("Target values:");
			RegressionDataSet rDataSet = (RegressionDataSet)dataSet;
			System.out.println(rDataSet.getTargetValues());
		}
		
	}

}
