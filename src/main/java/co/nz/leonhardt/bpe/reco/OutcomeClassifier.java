package co.nz.leonhardt.bpe.reco;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
	private final DataPointFactory learnDPF;

	/** The factory to create data points (for predicting). */
	private final DataPointFactory predictDPF;

	/** The pipeline: Regressor and all transformations. */
	private final DataModelPipeline dmp;

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
		learnDPF = DataPointFactory.create()
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
		predictDPF = DataPointFactory.create()
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
	public void learn(XLog logs) {
		List<DataPoint> data = new ArrayList<>(logs.size());
		
		for(XTrace trace: logs) {
			DataPoint dp = learnDPF.extractDataPoint(trace);
			data.add(dp);
		}
		
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
}
