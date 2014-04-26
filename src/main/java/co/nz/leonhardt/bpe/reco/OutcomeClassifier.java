package co.nz.leonhardt.bpe.reco;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import jsat.DataSet;
import jsat.SimpleDataSet;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.datatransform.DataModelPipeline;
import jsat.datatransform.DataTransform;
import jsat.datatransform.DataTransformFactory;
import jsat.datatransform.PCA;
import jsat.datatransform.ZeroMeanTransform;
import jsat.graphing.CategoryPlot;
import jsat.graphing.ClassificationPlot;
import jsat.graphing.ParallelCoordinatesPlot;
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
		
		// Regressor to use
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
		
		visualizeCategories(dataSet);
	}

	@Override
	public Outcome predict(XTrace partialTrace) {
		DataPoint dp = learnDPF.extractDataPoint(partialTrace);
		
		/*
		List<DataPoint> dps = new ArrayList<>();
		dps.add(dp);
		ClassificationDataSet cDataSet = new ClassificationDataSet(new SimpleDataSet(dps), 0);
		visualizeClassification(cDataSet, dmp);
		*/
		
		CategoricalResults cr = dmp.classify(dp);
		
		int ml = cr.mostLikely();
		
		System.out.println("Prediction: " + Outcome.fromInt(ml) + " (probability: " + cr.getProb(ml) + ")");
		
		return Outcome.fromInt(ml);
	}
	
	private void visualizeCategories(ClassificationDataSet cDataSet) {
		//PCA needs the data samples to have a mean of ZERO, so we need a transform to ensue this property as well
        DataTransform zeroMean = new ZeroMeanTransform(cDataSet);
        cDataSet.applyTransform(zeroMean);
        
        //PCA is a transform that attempts to reduce the dimensionality while maintaining all the variance in the data. 
        //PCA also allows us to specify the exact number of dimensions we would like 
        DataTransform pca = new PCA(cDataSet, 2);
        
        //We can now apply the transformations to our data set
        cDataSet.applyTransform(pca);
        
        CategoryPlot plot = new CategoryPlot(cDataSet);
	        
	     JFrame jFrame = new JFrame("2D Visualization");
	     jFrame.add(plot);
	     jFrame.setSize(400, 400);
	     jFrame.setVisible(true); 
	     jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void visualizeClassification(ClassificationDataSet cDataSet, Classifier cl) {
		//PCA needs the data samples to have a mean of ZERO, so we need a transform to ensue this property as well
        DataTransform zeroMean = new ZeroMeanTransform(cDataSet);
        cDataSet.applyTransform(zeroMean);
        
        //PCA is a transform that attempts to reduce the dimensionality while maintaining all the variance in the data. 
        //PCA also allows us to specify the exact number of dimensions we would like 
        DataTransform pca = new PCA(cDataSet, 2);
        
        //We can now apply the transformations to our data set
        cDataSet.applyTransform(pca);
        
        CategoryPlot plot = new ClassificationPlot(cDataSet, cl);
	        
	     JFrame jFrame = new JFrame("2D Visualization");
	     jFrame.add(plot);
	     jFrame.setSize(400, 400);
	     jFrame.setVisible(true); 
	     jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
