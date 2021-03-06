package co.nz.leonhardt.reco.javaml;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.Outcome;
import co.nz.leonhardt.bpe.processing.AmountRequestedExtractor;
import co.nz.leonhardt.bpe.processing.CycleTimeExtractor;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.bpe.processing.RandomMetricExtractor;
import co.nz.leonhardt.bpe.processing.TraceLengthExtractor;
import co.nz.leonhardt.bpe.processing.WorkTimeExtractor;
import co.nz.leonhardt.reco.DataExtractionFactory;
import co.nz.leonhardt.reco.PredictionResult;
import co.nz.leonhardt.reco.PredictionService;

/**
 * Classifier for outcome.
 * 
 * Uses k-nearest-neighbours.
 * 
 * @author freddy
 *
 */
public class KNNOutcomeClassifier implements PredictionService<PredictionResult<Outcome>> {
	
	/** The factory to create data points (for learning). */
	private final DataExtractionFactory<Instance,Dataset> learnIF;

	private final Classifier classifier;
	
	/**
	 * Creates a new Outcome classifier.
	 */
	public KNNOutcomeClassifier() {
		/*
		 * LEARN
		 * First variable should be target variable.
		 * 
		 */
		// Features to extract
		learnIF = JavaMLFactory.create()
				.withClass(
					new OutcomeExtractor())
				.withNumerics(
					new TraceLengthExtractor(),
					//new RandomMetricExtractor(),
					new AmountRequestedExtractor(),
					new CycleTimeExtractor(TimeUnit.MINUTES),
					new WorkTimeExtractor(TimeUnit.MINUTES));

		classifier = new KNearestNeighbors(5);
	}

	@Override
	public void learn(XLog logs) {
		Dataset data = learnIF.extractDataSet(logs);
		
		classifier.buildClassifier(data);
	}

	@Override
	public PredictionResult<Outcome> predict(XTrace partialTrace) {
		Instance instance = learnIF.extractDataPoint(partialTrace);
		
		Outcome predictedClassValue = (Outcome)classifier.classify(instance);
		
		//System.out.println("Prediction: " + predictedClassValue);
		
		return new PredictionResult<Outcome>(predictedClassValue);
	}

	@Override
	public void crossValidate(XLog logs) {
		Dataset data = learnIF.extractDataSet(logs);
		System.out.println(data);
		CrossValidation cv = new CrossValidation(classifier);
		
		Map<Object, PerformanceMeasure> pm = cv.crossValidation(data);
		//Map<Object, PerformanceMeasure> pm = EvaluateDataset.testDataset(classifier, data);
		for(Object o:pm.keySet()) {
		    System.out.println(o+", accuracy: "+pm.get(o).getAccuracy() + ", error rate: " + pm.get(o).getErrorRate());
		}
	}
}
