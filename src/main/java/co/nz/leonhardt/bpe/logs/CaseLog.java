package co.nz.leonhardt.bpe.logs;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XTrace;

/**
 * Represents an case in the context of a Process Log.
 * 
 * Has a UUID, a timestamp and an amount requested.
 * 
 * @author freddy
 *
 */
public class CaseLog {
	
	private final String caseUuid;
	
	/* Time */
	private final Date timestamp;
	
	/* Amount requested */
	private Double amountRequested; //TODO this is actually application-specific
	
	/**
	 * Creates a new case log with the given uuid and timestamp.
	 * 
	 * @param uuid
	 * @param timestamp
	 */
	public CaseLog(final String uuid, final Date timestamp) {//, final Long relTimestamp) {
		this.caseUuid = uuid;
		this.timestamp = timestamp;
	}
	
	/**
	 * Creates a new case log with the given timestamp and a random uuid.
	 * 
	 * @param timestamp
	 */
	public CaseLog(final Date timestamp) {//final Long relTimestamp) {
		this.caseUuid = UUID.randomUUID().toString();
		this.timestamp = timestamp;
	}

	public String getCaseUuid() {
		return caseUuid;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public Double getAmountRequested() {
		return amountRequested;
	}

	public void setAmountRequested(Double amountRequested) {
		this.amountRequested = amountRequested;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CaseLog [caseUuid=").append(caseUuid)
				.append(", timestamp=").append(timestamp)
				.append(", amountRequested=").append(amountRequested)
				.append("]");
		return builder.toString();
	}

	/**
	 * Converts this caseLog to an XTrace.
	 * 
	 * @return an XTrace
	 */
	public XTrace asXTrace() {
		XFactory fac = new XFactoryBufferedImpl();
		XTrace trace = fac.createTrace(); //TODO keep ID
		
		if(timestamp != null) {
			/*
			 *  Doesn't use time extension!
			 *  
			 *  <date key="REG_DATE" value="2011-10-01T00:38:44.546+02:00"/>
			 */
			XAttribute attr = fac.createAttributeTimestamp("REG_DATE", timestamp, null);
			trace.getAttributes().put("REG_DATE", attr);
		}
		
		if(amountRequested != null) {
			/*
			 * <string key="AMOUNT_REQ" value="20000"/>
			 */
			XAttribute attr = fac.createAttributeLiteral("AMOUNT_REQ", amountRequested.toString(), null);
			trace.getAttributes().put("AMOUNT_REQ", attr);
		}
		
		/* 
		 * TODO concept name (was number/id)
		 * 
		 * <string key="concept:name" value="173688"/> 
		 */
		if(caseUuid != null) {
			XConceptExtension cExt = XConceptExtension.instance();
			cExt.assignName(trace, caseUuid);
		}
		
		return trace;
	}

	@Override
	public int hashCode() {
		return Objects.hash(amountRequested, caseUuid, timestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CaseLog other = (CaseLog) obj;
		if (amountRequested == null) {
			if (other.amountRequested != null)
				return false;
		} else if (!amountRequested.equals(other.amountRequested))
			return false;
		if (caseUuid == null) {
			if (other.caseUuid != null)
				return false;
		} else if (!caseUuid.equals(other.caseUuid))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}
}
