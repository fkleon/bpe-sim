package co.nz.leonhardt.bpe.reco.jsat;

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
import co.nz.leonhardt.bpe.processing.WorkTimeExtractor;
import co.nz.leonhardt.bpe.reco.DataExtractionFactory;
import co.nz.leonhardt.bpe.reco.PredictionService;

/**
 * A cycle time predictor.
 * 
 * Uses logistic regression.
 * 
 * @author freddy
 *
 */
public class LRCycleTimePredictor implements PredictionService<Double> {
	
	/** The factory to create data points (for learning). */
	private final DataExtractionFactory<DataPoint, DataSet> learnDPF;

	/** The factory to create data points (for predicting). */
	private final DataExtractionFactory<DataPoint, DataSet> predictDPF;

	/** The pipeline: Regressor and all transformations. */
	private final DataModelPipeline dmp;
	
	/**
	 * Creates a new cycle time predictor.
	 */
	public LRCycleTimePredictor() {
		/*
		 * LEARN
		 * First variable should be target variable.
		 * 
		 */
		// Features to extract
		learnDPF = JSATFactory.create()
				.withNumerics(
					//new BiasMetricExtractor(),
					new CycleTimeExtractor(TimeUnit.MINUTES), // Target variable first!
					new TraceLengthExtractor(), 
					new AmountRequestedExtractor(),
					new WorkTimeExtractor(TimeUnit.MINUTES))
				.withCategories(
					new OutcomeExtractor());
		
		/*
		 * REGRESS
		 * Must not contain target variable!
		 * 
		 */
		predictDPF = JSATFactory.create()
				.withNumerics(
						new TraceLengthExtractor(), 
						new AmountRequestedExtractor(),
						new WorkTimeExtractor(TimeUnit.MINUTES))
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
	public void learn(XLog log) {
		DataSet data = learnDPF.extractDataSet(log);
		//TODO: this assumes cycle time is always in the 1st place
		RegressionDataSet dataSet = new RegressionDataSet(data.getDataPoints(), 0);
		
		// apply transformations
		printSet(dataSet);
		dmp.train(dataSet);
		
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

	@Override
	public void crossValidate(XLog logs) {
		// TODO Auto-generated method stub
		
	}

}
