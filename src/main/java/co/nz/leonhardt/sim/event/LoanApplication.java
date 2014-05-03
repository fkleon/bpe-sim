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

	private final Long amountRequested;
	
	private Integer customerContactAttempts = 0;
	
	public LoanApplication(Model owner, final Long amountRequested, final Date dateSubmitted) {
		super(owner, "LoanApplication", true, dateSubmitted);
		
		this.amountRequested = amountRequested;
	}

	public Long getAmountRequested() {
		return amountRequested;
	}
	
	public Integer getCustomerContactAttempts() {
		return customerContactAttempts;
	}
	
	public void increaseCustomerContactAttempts() {
		customerContactAttempts++;
	}
}
