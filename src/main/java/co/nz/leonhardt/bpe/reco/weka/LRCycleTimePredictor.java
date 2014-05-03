package co.nz.leonhardt.bpe.reco.weka;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import co.nz.leonhardt.bpe.processing.AmountRequestedExtractor;
import co.nz.leonhardt.bpe.processing.CycleTimeExtractor;
import co.nz.leonhardt.bpe.processing.LoopLengthExtractor;
import co.nz.leonhardt.bpe.processing.OutcomeExtractor;
import co.nz.leonhardt.bpe.processing.TraceLengthExtractor;
import co.nz.leonhardt.bpe.processing.WorkTimeExtractor;
import co.nz.leonhardt.bpe.reco.DataExtractionFactory;
import co.nz.leonhardt.bpe.reco.PredictionResult;
import co.nz.leonhardt.bpe.reco.PredictionService;

/**
 * Predictor for cycle time.
 * 
 * Uses a Logistic Regression model.
 * 
 * @author freddy
 *
 */
public class LRCycleTimePredictor implements PredictionService<PredictionResult<Double>> {

	Classifier classifier;
	
	DataExtractionFactory<Instance, Instances> dataFactory;
	/**
	 * Creates a new cycle time predictor (Weka)
	 */
	public LRCycleTimePredictor() {
		classifier = new LinearRegression();
		dataFactory = WekaNumericalFactory.create()
				.withClass(
						new CycleTimeExtractor(TimeUnit.MINUTES))
				.withCategories(
						new OutcomeExtractor())
				.withNumerics(
						new TraceLengthExtractor(),
						//new RandomMetricExtractor(),
						new AmountRequestedExtractor(),
						new LoopLengthExtractor(),
						new WorkTimeExtractor(TimeUnit.MINUTES));
	}
	
	@Override
	public void learn(XLog log) throws Exception {
		Instances dataSet = dataFactory.extractDataSet(log);
		classifier.buildClassifier(dataSet);
	}

	@Override
	public PredictionResult<Double> predict(XTrace partialTrace) throws Exception {
		Instance instance = dataFactory.extractDataPoint(partialTrace);
		return new PredictionResult<Double>(classifier.classifyInstance(instance));
	}

	@Override
	public void crossValidate(XLog log) throws Exception {
		// TODO Auto-generated method stub
		Instances dataSet = dataFactory.extractDataSet(log);
		Evaluation eval = new Evaluation(dataSet);
		eval.crossValidateModel(classifier, dataSet, 10, new Random(1));
		
		System.out.println("RMSE: " + eval.rootMeanSquaredError());
		System.out.println(eval.toClassDetailsString());
	}

}
