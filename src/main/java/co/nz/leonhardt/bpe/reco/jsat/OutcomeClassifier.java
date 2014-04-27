package co.nz.leonhardt.bpe.reco.jsat;

import java.util.concurrent.TimeUnit;

import jsat.DataSet;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.datatransform.DataModelPipeline;
import jsat.datatransform.DataTransformFactory;
import jsat.regression.LogisticRegression;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.Outcome;
import co.nz.leonhardt.bpe.processing.AmountRequestedExtractor;
import co.nz.leonhardt.bpe.processing.CycleTimeExtractor;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.bpe.processing.RandomMetricExtractor;
import co.nz.leonhardt.bpe.processing.TraceLengthExtractor;
import co.nz.leonhardt.bpe.reco.DataExtractionFactory;
import co.nz.leonhardt.bpe.reco.PredictionService;

/**
 * Classifier for outcome.
 * 
 * Uses logistic regression.
 * 
 * @author freddy
 *
 */
public class OutcomeClassifier implements PredictionService<Outcome> {
	
	/** The factory to create data points (for learning). */
	protected final DataExtractionFactory<DataPoint, DataSet> learnDPF;

	/** The factory to create data points (for predicting). */
	protected final DataExtractionFactory<DataPoint, DataSet> predictDPF;

	/** The pipeline: Regressor and all transformations. */
	protected final DataModelPipeline dmp;

	/**
	 * Creates a new Outcome classifier.
	 */
	public OutcomeClassifier() {
		/*
		 * LEARN
		 * First variable should be target variable.
		 * 
		 */
		// Features to extract
		learnDPF = JSATFactory.create()
				.withNumerics(
					new TraceLengthExtractor(),
					new RandomMetricExtractor(),
					new AmountRequestedExtractor(),
					new CycleTimeExtractor(TimeUnit.MINUTES))
				.withCategories(
					new OutcomeExtractor()); // Target variable first!
		
		/*
		 * CLASSIFY
		 * Must not contain target variable!
		 * TODO: check, actually for a classifier this should be OK
		 * 
		 */
		predictDPF = JSATFactory.create()
				.withNumerics(
					new TraceLengthExtractor(),
					new RandomMetricExtractor(),
					new AmountRequestedExtractor(),
					new CycleTimeExtractor(TimeUnit.MINUTES));
				//.withCategories(
				//	new OutcomeExtractor()); // TODO check
		
		/*
		 * Data Pipeline
		 */
		
		// Classifier to use
		Classifier baseClassifier = new LogisticRegression();
		
		// Transformations to do
		DataTransformFactory[] factories = new DataTransformFactory[] {
				//new UnitVarianceTransform.UnitVarianceTransformFactory(),
				//new PNormNormalization.PNormNormalizationFactory(2.0),
				//new PolynomialTransform.PolyTransformFactory(3),
				//new StandardizeTransform.StandardizeTransformFactory()
		};

		dmp = new DataModelPipeline(baseClassifier, factories);
	}

	@Override
	public void learn(XLog log) {
		DataSet data = learnDPF.extractDataSet(log);
		ClassificationDataSet dataSet = new ClassificationDataSet(data, 0);
		dmp.trainC(dataSet);
	}

	@Override
	public Outcome predict(XTrace partialTrace) {
		DataPoint dp = learnDPF.extractDataPoint(partialTrace);
		
		CategoricalResults cr = dmp.classify(dp);
		int ml = cr.mostLikely();
		
		System.out.println("Prediction: " + Outcome.fromInt(ml) + " (probability: " + cr.getProb(ml) + ")");
		
		return Outcome.fromInt(ml);
	}

	@Override
	public void crossValidate(XLog logs) {
		// TODO Auto-generated method stub
		
	}
	 
}
