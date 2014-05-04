package co.nz.leonhardt.sim.model;

import java.util.List;

import co.nz.leonhardt.bpe.model.Digraph;

public class ProcessModel {

	private Digraph<Activity> directedGraph;
	
	/**
	 * Creates a new loan application process model.
	 */
	public ProcessModel() {		
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
		directedGraph.add(Activity.W_ContactCustomer, Activity.A_CANCELLED);

		directedGraph.add(Activity.O_SENT_BACK, Activity.W_ValidateApplication);
		directedGraph.add(Activity.W_ValidateApplication, Activity.A_DECLINED);
		directedGraph.add(Activity.W_ValidateApplication, Activity.A_ACTIVATED);
	}
	
	public List<Activity> getDecisionSpace(Activity state) {
		return directedGraph.outboundNeighbors(state);
	}
	
	public static void main(String... args) {
		ProcessModel pm = new ProcessModel();
		System.out.println(pm.directedGraph);
	}
}
