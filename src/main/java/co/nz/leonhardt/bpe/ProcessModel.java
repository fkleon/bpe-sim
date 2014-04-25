package co.nz.leonhardt.bpe;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class ProcessModel {

	private SetMultimap<String, String> possibleMoves;
	
	public ProcessModel() {
		possibleMoves = HashMultimap.create();
	}
	
	public Set<String> getDecisionSpace(String state) {
		return possibleMoves.get(state);
	}
}
