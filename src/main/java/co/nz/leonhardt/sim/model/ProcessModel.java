package co.nz.leonhardt.sim.model;

import java.util.Set;

import co.nz.leonhardt.bpe.model.Digraph;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class ProcessModel {

	private Digraph<Activity> directedGraph;
	
	private SetMultimap<String, String> possibleMoves;
	
	public ProcessModel() {
		possibleMoves = HashMultimap.create();
		
		directedGraph = new Digraph<>();
		
		directedGraph.add(Activity.A_SUBMITTED, Activity.A_DECLINED);
		directedGraph.add(Activity.A_SUBMITTED, Activity.A_PREACCEPTED);

		directedGraph.add(Activity.A_PREACCEPTED, Activity.W_AssessApplication);
		directedGraph.add(Activity.W_AssessApplication, Activity.A_DECLINED);
		directedGraph.add(Activity.W_AssessApplication, Activity.O_CREATED);
		directedGraph.add(Activity.O_CREATED, Activity.O_SENT);
		directedGraph.add(Activity.O_SENT, Activity.W_ContactCustomer);

		directedGraph.add(Activity.W_ContactCustomer, Activity.W_ContactCustomer);
		directedGraph.add(Activity.W_ContactCustomer, Activity.O_CREATED);
		directedGraph.add(Activity.W_ContactCustomer, Activity.O_SENT_BACK);

		directedGraph.add(Activity.O_SENT_BACK, Activity.W_ValidateApplication);
		directedGraph.add(Activity.W_ValidateApplication, Activity.A_DECLINED);
		directedGraph.add(Activity.W_ValidateApplication, Activity.A_ACTIVATED);
	}
	
	public Set<String> getDecisionSpace(String state) {
		return possibleMoves.get(state);
	}
	
	public static void main(String... args) {
		ProcessModel pm = new ProcessModel();
		System.out.println(pm.directedGraph);
	}
}
