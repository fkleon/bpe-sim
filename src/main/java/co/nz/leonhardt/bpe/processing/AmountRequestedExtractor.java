package co.nz.leonhardt.bpe.processing;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XTrace;

/**
 * Extracts the amount requested from a loan application.
 * 
 * @author freddy
 *
 */
public class AmountRequestedExtractor extends NumericalMetricExtractor<Double> {

	@Override
	public Double extractMetric(XTrace trace) {
		XAttribute attr = trace.getAttributes().get("AMOUNT_REQ");
		String amount_req = attr.toString();
		return Double.parseDouble(amount_req);
	}

	@Override
	public String getMetricName() {
		return "AmountRequested";
	}

}
