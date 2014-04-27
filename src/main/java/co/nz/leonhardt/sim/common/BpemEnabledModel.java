package co.nz.leonhardt.sim.common;

import co.nz.leonhardt.bpe.BPEM;
import desmoj.core.simulator.Model;

/**
 * An simulation model supporting a Business Process Execution and Monitoring
 * environment.
 * 
 * @author freddy
 *
 */
public abstract class BpemEnabledModel extends Model {

	/** The BPEM associated to this model */
	protected BPEM bpemEnvironment;
	
	/**
	 * Creates a new BPEM-enabled simulation model.
	 * 
	 * @param bpemEnvironment
	 * @param owner
	 * @param name
	 * @param showInReport
	 * @param showInTrace
	 */
	public BpemEnabledModel(BPEM bpemEnvironment, Model owner, String name, boolean showInReport,
            boolean showInTrace) {
		super(owner, name, showInReport, showInTrace);
		this.bpemEnvironment = bpemEnvironment;
	}
	
	/**
	 * Returns the Business Process Execution and Monitoring environment
	 * associated to this model.
	 * 
	 * @return
	 */
	public BPEM getBpemEnvironment() {
		return bpemEnvironment;
	}
	
}
