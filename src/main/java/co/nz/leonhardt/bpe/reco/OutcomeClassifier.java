package co.nz.leonhardt.bpe.reco;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;
import jsat.datatransform.DataTransformProcess;
import jsat.datatransform.PolynomialTransform;
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

	private final LogisticRegression lr;

	private final DataPointFactory dpf;
		
	private final DataTransformProcess dtp;

	/**
	 * Creates a new Outcome classifier.
	 */
	public OutcomeClassifier() {
		lr = new LogisticRegression();
		
		dpf = DataPointFactory.create()
				.withNumerics(
					//new BiasMetricExtractor(),
					new TraceLengthExtractor(),
					new RandomMetricExtractor(),
					new AmountRequestedExtractor(),
					new CycleTimeExtractor(TimeUnit.MINUTES))
				.withCategories(
					new OutcomeExtractor());
		
		dtp = new DataTransformProcess(
				//new StandardizeTransform.StandardizeTransformFactory(),
				new PolynomialTransform.PolyTransformFactory(3));

	}

	@Override
	public void learn(XLog logs) {
		List<DataPoint> data = new ArrayList<>(logs.size());
		
		for(XTrace trace: logs) {
			DataPoint dp = dpf.extractDataPoint(trace);
			data.add(dp);
			//System.out.println(dp);
		}
		
		ClassificationDataSet dataSet = new ClassificationDataSet(data, 0);

		// apply transformations
		//dtp.learnApplyTransforms(dataSet);

		lr.trainC(dataSet);
		
		System.out.println("learned coefficents: " + lr.getCoefficents());
		System.out.println("bias: " + lr.getBias());
	}

	@Override
	public Outcome predict(XTrace partialTrace) {
		DataPoint dp = dpf.extractDataPoint(partialTrace);
		
		// apply transformations
		//dp = dtp.transform(dp);
		
		CategoricalResults cr = lr.classify(dp);
		int ml = cr.mostLikely();
		
		System.out.println("Prediction: " + Outcome.fromInt(ml) + " (probability: " + cr.getProb(ml) + ")");
		
		return Outcome.fromInt(ml);
	}
}
