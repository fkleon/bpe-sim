package co.nz.leonhardt.bpe.reco.jsat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jsat.DataSet;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.classifiers.OneVSAll;
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
import co.nz.leonhardt.bpe.processing.WorkTimeExtractor;
import co.nz.leonhardt.bpe.reco.ClassificationResult;
import co.nz.leonhardt.bpe.reco.DataExtractionFactory;
import co.nz.leonhardt.bpe.reco.PredictionResult;
import co.nz.leonhardt.bpe.reco.PredictionService;

/**
 * Classifier for outcome.
 * 
 * Uses One-vs-all logistic regression.
 * 
 * @author freddy
 *
 */
public class LROutcomeClassifier implements PredictionService<ClassificationResult<Outcome>> {
	
	/** The factory to create data points (for learning). */
	protected final DataExtractionFactory<DataPoint, DataSet> learnDPF;

	/** The factory to create data points (for predicting). */
	protected final DataExtractionFactory<DataPoint, DataSet> predictDPF;

	/** The pipeline: Regressor and all transformations. */
	protected final DataModelPipeline dmp;

	/**
	 * Creates a new Outcome classifier.
	 */
	public LROutcomeClassifier() {
		/*
		 * LEARN
		 * First variable should be target variable.
		 * 
		 */
		// Features to extract
		learnDPF = JSATFactory.create()
				.withClass(new OutcomeExtractor())
				.withNumerics(
					new TraceLengthExtractor(),
					//new RandomMetricExtractor(),
					new AmountRequestedExtractor(),
					new CycleTimeExtractor(TimeUnit.MINUTES),
					new WorkTimeExtractor(TimeUnit.MINUTES));
				//.withCategories(
				//	new OutcomeExtractor()); // Target variable first!
		
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
					new CycleTimeExtractor(TimeUnit.MINUTES),
					new WorkTimeExtractor(TimeUnit.MINUTES));
				//.withCategories(
				//	new OutcomeExtractor()); // TODO check
		
		/*
		 * Data Pipeline
		 */
		
		// Classifier to use
		Classifier baseClassifier = new LogisticRegression();
		baseClassifier = new OneVSAll(baseClassifier, false);
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
		DataSet dataSet = learnDPF.extractDataSet(log);
		ClassificationDataSet cDataSet = new ClassificationDataSet(dataSet, 0);
		
		dmp.trainC(cDataSet);
	}

	@Override
	public ClassificationResult<Outcome> predict(XTrace partialTrace) {
		DataPoint dp = learnDPF.extractDataPoint(partialTrace);
		
		CategoricalResults cr = dmp.classify(dp);
		
		/*
		int ml = cr.mostLikely();
		System.out.println("Best Prediction: " + Outcome.fromInt(ml) + " (probability: " + cr.getProb(ml) + ")");
		*/
		
		List<PredictionResult<Outcome>> prResults = new ArrayList<>();
		for(int i = 0; i < cr.size(); i++) {
			Outcome prOutcome = Outcome.fromInt(i);
			double prConfidence = cr.getProb(i);
			PredictionResult<Outcome> pr = new PredictionResult<Outcome>(prOutcome, prConfidence);
			prResults.add(pr);
		}
		return new ClassificationResult<Outcome>(prResults);
	}

	@Override
	public void crossValidate(XLog log) {
		//Classifier naiveBayes = new OneVSAll(new NaiveBayes(), false);
		DataSet dataSet = learnDPF.extractDataSet(log);
		ClassificationDataSet cDataSet = new ClassificationDataSet(dataSet, 0);

		ClassificationModelEvaluation modelEvaluation = new ClassificationModelEvaluation(dmp, cDataSet);
		modelEvaluation.evaluateCrossValidation(10);
		
        System.out.println("Cross Validation error rate is " + 100.0*modelEvaluation.getErrorRate() + "%");
        
        //We can also obtain how long it took to train, and how long classification took
        System.out.println("Trainig time: " + modelEvaluation.getTotalTrainingTime()/1000.0 + " seconds");
        System.out.println("Classification time: " + modelEvaluation.getTotalClassificationTime()/1000.0 + " seconds\n");
        
        modelEvaluation.prettyPrintClassificationScores();
        
        //The model can print a 'Confusion Matrix' this tells us about the errors our classifier made. 
        //Each row represents all the data points that belong to a given class. 
        //Each column represents the predicted class
        //That means values in the diagonal indicate the number of correctly classifier points in each class. 
        //Off diagonal values indicate mistakes
        modelEvaluation.prettyPrintConfusionMatrix();
	}
	 
}
