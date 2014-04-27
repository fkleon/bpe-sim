package co.nz.leonhardt.bpe;

import java.util.Map;

import org.deckfour.xes.model.XLog;

import co.nz.leonhardt.bpe.logs.CaseLog;
import co.nz.leonhardt.bpe.logs.EventLog;
import co.nz.leonhardt.bpe.logs.RAMProcessLogStorage;

/**
 * Facade implementing the BPEM interface.
 * 
 * 
 * @author freddy
 *
 */
public class BPEMFacade implements BPEM {

	/** The underlying process log storage */
	private RAMProcessLogStorage processLogStorage;
	
	/**
	 * Creates a new BPEMFacade backed by a RAM storage. 
	 */
	public BPEMFacade() {
		this.processLogStorage = new RAMProcessLogStorage();
	}
	
	@Override
	public void startTrace(CaseLog caseLog) {
		processLogStorage.startTrace(caseLog);
		
		//TODO setup KPI collectors
	}

	@Override
	public void endTrace(String caseUuid) {
		processLogStorage.endTrace(caseUuid);
		
		//TODO finalise KPI collectors
	}

	@Override
	public void addEvent(String caseUuid, EventLog eventLog) {
		processLogStorage.addEvent(caseUuid, eventLog);
		
		// TODO update KPI collectors
	}

	@Override
	public XLog exportLog() {
		return processLogStorage.export();
	}

	@Override
	public Map<?, ?> exportKPIs() {
		// TODO export KPIs
		return null;
	}

}
