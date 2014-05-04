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
		
		// costs are used as probabilities
		directedGraph.add(Activity.A_SUBMITTED, Activity.A_DECLINED, 0.26);
		directedGraph.add(Activity.A_SUBMITTED, Activity.A_PREACCEPTED, 1-0.26);

		directedGraph.add(Activity.A_PREACCEPTED, Activity.W_AssessApplication, 1.0);
		directedGraph.add(Activity.W_AssessApplication, Activity.A_DECLINED, 0.17);
		directedGraph.add(Activity.W_AssessApplication, Activity.O_CREATED, 1-0.17);
		directedGraph.add(Activity.O_CREATED, Activity.O_SENT, 1.0);
		directedGraph.add(Activity.O_SENT, Activity.W_ContactCustomer, 1.0);

		directedGraph.add(Activity.W_ContactCustomer, Activity.W_ContactCustomer, 0.4);
		directedGraph.add(Activity.W_ContactCustomer, Activity.O_CREATED, 0.1);
		directedGraph.add(Activity.W_ContactCustomer, Activity.O_SENT_BACK, 0.4);
		directedGraph.add(Activity.W_ContactCustomer, Activity.A_CANCELLED, 0.1);

		directedGraph.add(Activity.O_SENT_BACK, Activity.W_ValidateApplication, 1.0);
		directedGraph.add(Activity.W_ValidateApplication, Activity.A_DECLINED, 0.17);
		directedGraph.add(Activity.W_ValidateApplication, Activity.A_ACTIVATED, 1-0.17);
	}
	
	public List<Activity> getDecisionSpace(Activity state) {
		return directedGraph.outboundNeighbors(state);
	}
	
	public static void main(String... args) {
		ProcessModel pm = new ProcessModel();
		System.out.println(pm.directedGraph);
	}
}
