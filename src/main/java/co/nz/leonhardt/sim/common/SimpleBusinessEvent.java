package co.nz.leonhardt.sim.common;

import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;

import co.nz.leonhardt.bpe.logs.EventLog;
import co.nz.leonhardt.sim.event.Resource;
import desmoj.core.simulator.Event;

public class SimpleBusinessEvent<C extends BusinessCase> extends Event<C>
		implements BusinessEvent {

	/** The resource associated with this event */
	protected String resource;

	/**
	 * The connector to the BPEM system
	 */
	protected BpemConnector bpemConnector;

	/**
	 * Creates a new simple business event with the given BPEM-enabled model.
	 * 
	 * @param owner
	 *            a BPEM-enabled model
	 * @param name
	 *            the name of this event
	 * @param showInTrace
	 */
	public SimpleBusinessEvent(BpemEnabledModel owner, String name,
			boolean showInTrace) {
		super(owner, name, showInTrace);
		this.bpemConnector = new BpemConnector(owner);
	}

	@Override
	public void eventRoutine(C businessCase) {
		// send generic log
		EventLog log = new EventLog(presentTime().getTimeAsDate());
		log.setConceptName(this.getClass().getSimpleName());
		log.setLifecycleTransition(StandardModel.COMPLETE);
		bpemConnector.fireEventLog(businessCase, log);
	}

	@Override
	public void fireEventLog(BusinessCase businessCase, EventLog log) {
		bpemConnector.fireEventLog(businessCase, log);
	}

	@Override
	public void fireEventLog(BusinessCase businessCase, String conceptName,
			StandardModel lifecycleTransition) {
		bpemConnector.fireEventLog(businessCase, conceptName,
				lifecycleTransition);
	}

	@Override
	public void fireEventLog(BusinessCase businessCase, String conceptName,
			StandardModel lifecycleTransition, String resource) {
		bpemConnector.fireEventLog(businessCase, conceptName,
				lifecycleTransition, resource);
	}

	@Override
	public void fireEventLog(BusinessCase businessCase, String conceptName,
			StandardModel lifecycleTransition, Resource resource) {
		bpemConnector.fireEventLog(businessCase, conceptName,
				lifecycleTransition, resource.getName());
	}

	@Override
	public void closeCase(BusinessCase bCase) {
		bpemConnector.closeCase(bCase);
	}

}
