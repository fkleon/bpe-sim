package co.nz.leonhardt.bpe.processing;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import co.nz.leonhardt.bpe.util.LRS;

/**
 * Extracts the size of the longest loop in the given trace.
 * 
 * @author freddy
 *
 */
public class LoopLengthExtractor extends NumericalMetricExtractor<Integer> {

	int curUniqueStringId;
	
	// TODO Use SuffixTree
	@Override
	public Integer extractMetric(XTrace trace) {
		// map event activities to unique one character representation
		Map<String,Character> uniqueString = new HashMap<>();
		curUniqueStringId = 65;
		
		StringBuilder sequence = new StringBuilder();
		StringBuilder reducedSequence = new StringBuilder();
		
		for(XEvent event: trace) {
			XConceptExtension conExt = XConceptExtension.instance();
			XLifecycleExtension lcExt = XLifecycleExtension.instance();
			
			String name = conExt.extractName(event);
			name += "\\" + lcExt.extractStandardTransition(event);

			if (!uniqueString.containsKey(name)) {
				uniqueString.put(name, getNextUniqueChar());
			}
			
			sequence.append(name).append(",");
			reducedSequence.append(uniqueString.get(name));
		}
		
		String lrs = LRS.lrs(reducedSequence.toString());
		
		/*
		System.out.println("sequence " + sequence.toString());
		System.out.println("reduced sequence " + reducedSequence.toString());
		System.out.println("longest repeated sequence: " + lrs);
		*/
		
		return lrs.length();
	}
	
	private Character getNextUniqueChar() {
		return Character.toChars(curUniqueStringId++)[0];
	}

	@Override
	public String getMetricName() {
		return "LongestLoopLength";
	}
}
