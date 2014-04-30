package co.nz.leonhardt.bpe.reco.weka;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
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
 * Uses a J48 Decision Tree.
 * 
 * @author freddy
 *
 */
public class DTOutcomeClassifier implements PredictionService<ClassificationResult<Outcome>> {

	Classifier classifier;
	
	DataExtractionFactory<Instance, Instances> dataFactory;
	/**
	 * Creates a new decision treee outcome classifier.
	 */
	public DTOutcomeClassifier() {
		classifier = new J48();
		dataFactory = WekaFactory.create()
				.withClass(
						new OutcomeExtractor())
				.withNumerics(
						new TraceLengthExtractor(),
						new RandomMetricExtractor(),
						new AmountRequestedExtractor(),
						new CycleTimeExtractor(TimeUnit.MINUTES),
						new WorkTimeExtractor(TimeUnit.MINUTES));
	}
	
	@Override
	public void learn(XLog log) throws Exception {
		Instances dataSet = dataFactory.extractDataSet(log);
		classifier.buildClassifier(dataSet);
	}

	@Override
	public ClassificationResult<Outcome> predict(XTrace partialTrace) throws Exception {
		Instance instance = dataFactory.extractDataPoint(partialTrace);
		
		//Double classification = classifier.classifyInstance(instance);
		double[] distribution = classifier.distributionForInstance(instance);
		
		List<PredictionResult<Outcome>> prResults = new ArrayList<>();
		for(int i = 0; i < distribution.length; i++) {
			Outcome prOutcome = Outcome.fromInt(i);
			double prConfidence = distribution[i];
			PredictionResult<Outcome> pr = new PredictionResult<Outcome>(prOutcome, prConfidence);
			prResults.add(pr);
		}
		return new ClassificationResult<Outcome>(prResults);
	}

	@Override
	public void crossValidate(XLog log) throws Exception {
		// TODO Auto-generated method stub
		Instances dataSet = dataFactory.extractDataSet(log);
		Evaluation eval = new Evaluation(dataSet);
		eval.crossValidateModel(classifier, dataSet, 10, new Random(1));
		
		System.out.println("RMSE: " + eval.rootMeanSquaredError());
	}

}
