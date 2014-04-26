package co.nz.leonhardt.bpe.reco;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jsat.DataSet;
import jsat.classifiers.DataPoint;
import jsat.datatransform.DataModelPipeline;
import jsat.datatransform.DataTransformFactory;
import jsat.datatransform.PNormNormalization;
import jsat.datatransform.PolynomialTransform;
import jsat.datatransform.StandardizeTransform;
import jsat.datatransform.UnitVarianceTransform;
import jsat.regression.LogisticRegression;
import jsat.regression.RegressionDataSet;
import jsat.regression.Regressor;

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
	
	/** The factory to create data points (for learning). */
	private final DataPointFactory learnDPF;

	/** The factory to create data points (for predicting). */
	private final DataPointFactory predictDPF;

	/** The pipeline: Regressor and all transformations. */
	private final DataModelPipeline dmp;
	
	/**
	 * Creates a new cycle time predictor.
	 */
	public CycleTimePredictor() {
		/*
		 * LEARN
		 * First variable should be target variable.
		 * 
		 */
		// Features to extract
		learnDPF = DataPointFactory.create()
				.withNumerics(
					//new BiasMetricExtractor(),
					new CycleTimeExtractor(TimeUnit.MINUTES), // Target variable first!
					new TraceLengthExtractor(), 
					new AmountRequestedExtractor())
				.withCategories(
					new OutcomeExtractor());
		
		/*
		 * REGRESS
		 * Must not contain target variable!
		 * 
		 */
		predictDPF = DataPointFactory.create()
				.withNumerics(
						new TraceLengthExtractor(), 
						new AmountRequestedExtractor())
					.withCategories(
						new OutcomeExtractor());
		
		/*
		 * Data Pipeline
		 */
		
		// Regressor to use
		Regressor baseRegressor = new LogisticRegression();
		
		// Transformations to do
		DataTransformFactory[] factories = new DataTransformFactory[] {
				//new UnitVarianceTransform.UnitVarianceTransformFactory(),
				//new PNormNormalization.PNormNormalizationFactory(2.0),
				//new PolynomialTransform.PolyTransformFactory(2),
				//new StandardizeTransform.StandardizeTransformFactory()
		};

		dmp = new DataModelPipeline(baseRegressor, factories);
	}
	
	@Override
	public void learn(XLog logs) {
		List<DataPoint> data = new ArrayList<>(logs.size());
		
		//PolynomialTransform trans = new PolynomialTransform(1);

		for(XTrace trace: logs) {
			DataPoint dp = learnDPF.extractDataPoint(trace);
			data.add(dp);
		}
		
		//TODO: this assumes cycle time is always in the 1st place
		RegressionDataSet dataSet = new RegressionDataSet(data, 0);
		
		// apply transformations
		//dtp.learnApplyTransforms(dataSet);
		printSet(dataSet);
		
		dmp.train(dataSet);
		
		//lr.train(dataSet);
		
		//System.out.println("learned coefficients: " + lr.getCoefficents());
		//System.out.println("bias: " + lr.getBias());
	}
	
	
	@Override
	public Double predict(XTrace partialTrace) {
		// The data point must not contain the target value!
		DataPoint dp = predictDPF.extractDataPoint(partialTrace);
		
		return dmp.regress(dp);
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
