package co.nz.leonhardt.sim.model;

/**
 * Legal types of activities.
 * 
 * @author freddy
 *
 */
public enum Activity {

	A_SUBMITTED,
	A_PREACCEPTED,
	A_ACTIVATED,
	A_CANCELLED,
	A_DECLINED,
	
	O_CREATED,
	O_SENT,
	O_SENT_BACK,
	
	W_AssessApplication,
	W_AssessFraud,
	W_ContactCustomer,
	W_ValidateApplication,
}
