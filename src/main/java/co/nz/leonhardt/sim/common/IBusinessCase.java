package co.nz.leonhardt.sim.common;

import java.util.Date;

/**
 * Interface representing a business case.
 * 
 * @author freddy
 *
 */
public interface IBusinessCase {

	/**
	 * @return the UUID of this case
	 */
	public String getUuid();
	
	/**
	 * @return the start date of this case
	 */
	public Date getStartDate();
}
