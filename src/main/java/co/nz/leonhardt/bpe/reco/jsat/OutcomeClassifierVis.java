package co.nz.leonhardt.bpe.reco.jsat;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.datatransform.DataTransform;
import jsat.datatransform.PCA;
import jsat.datatransform.ZeroMeanTransform;
import jsat.graphing.CategoryPlot;
import jsat.graphing.ClassificationPlot;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.Outcome;

/**
 * Classifier for outcome with visualisation.
 * 
 * Uses logistic regression.
 * 
 * Only to be used for testing.
 * 
 * @author freddy
 *
 */
public class OutcomeClassifierVis extends OutcomeClassifier {

	/**
	 * Creates a new Outcome classifier.
	 */
	public OutcomeClassifierVis() {
		super();
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
	     
	     // FIXME hack to prevent JUnit tests from ending
	     try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void crossValidate(XLog logs) {
		// TODO Auto-generated method stub
		
	}
}
