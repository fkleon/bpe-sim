package co.nz.leonhardt.sim.event;

import java.util.Date;

import co.nz.leonhardt.sim.common.BusinessCase;
import desmoj.core.simulator.Model;

/**
 * The Loan application entity encapsulates all information associated with an application.
 * 
 * This is an unique ID, the amount of money requested and the start date.
 * 
 * All necessary statistical
 * information are collected by the queue object. TODO check
 * 
 * @author freddy
 *
 */
public class LoanApplication extends BusinessCase {

	private final Double amountRequested;
	
	public LoanApplication(Model owner, final Double amountRequested, final Date dateSubmitted) {
		super(owner, "LoanApplication", true, dateSubmitted);
		
		this.amountRequested = amountRequested;
	}

	public Double getAmountRequested() {
		return amountRequested;
	}
}
