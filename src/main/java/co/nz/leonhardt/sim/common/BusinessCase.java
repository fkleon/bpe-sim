package co.nz.leonhardt.sim.common;

import java.util.Date;
import java.util.UUID;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

/**
 * Entity representing a business case.
 * 
 * It has a UUID and a start date.
 * 
 * @author freddy
 *
 */
public class BusinessCase extends Entity implements IBusinessCase {
	
	/** The UUID of this case */
	protected final String uuid;
	
	/** The start date of this case */
	protected final Date startDate;
	
	/**
	 * Creates a new business case.
	 * Uses the current simulation time and generates a new UUID.
	 * 
	 * @param owner
	 * @param name
	 * @param showInTrace
	 */
	public BusinessCase(Model owner, String name, boolean showInTrace) {
		super(owner, name, showInTrace);
		
		this.uuid = UUID.randomUUID().toString();
		this.startDate = presentTime().getTimeAsDate();
	}
	
	/**
	 * Creates a new business case.
	 * Uses the given time and generates a new UUID.
	 * 
	 * @param owner
	 * @param name
	 * @param showInTrace
	 * @param startDate
	 */
	public BusinessCase(Model owner, String name, boolean showInTrace, final Date startDate) {
		super(owner, name, showInTrace);
		
		this.uuid = UUID.randomUUID().toString();
		this.startDate = startDate;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}
}
