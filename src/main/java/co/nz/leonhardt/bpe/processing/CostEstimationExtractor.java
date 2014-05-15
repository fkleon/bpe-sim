package co.nz.leonhardt.bpe.processing;

import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.categories.Outcome;

/**
 * Returns the estimated cost of the given trace.
 * 
 * @author freddy
 *
 */
public class CostEstimationExtractor extends NumericalMetricExtractor<Double> {

	private WorkTimeExtractor wte;
	private OutcomeExtractor oe;
	
	public CostEstimationExtractor() {
		wte = new WorkTimeExtractor(TimeUnit.HOURS);
		oe = new OutcomeExtractor();
	}
	
	@Override
	public Double extractMetric(XTrace trace) {
		Outcome outcome = oe.extractMetric(trace);
		Long workTime = wte.extractMetric(trace);
		
		Double estimatedCost = workTime.doubleValue();
		
		if(outcome == null) {
			return estimatedCost; // no penalty, still running
		}
		
		switch(outcome) {
			case ACCEPTED: return estimatedCost * 0.5; // profit
			case DECLINED: return estimatedCost * 1.5; // penalty
			case CANCELLED: return estimatedCost * 1.2; // penalty
			//case UNDECIDED: return workTime.doubleValue(); // no penalty..
		}
			
		return estimatedCost;
	}

	@Override
	public String getMetricName() {
		return "CostEstimation";
	}

}
