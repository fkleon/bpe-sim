package co.nz.leonhardt.xes;

import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XElement;
import org.deckfour.xes.model.XEvent;

/**
 * 
 * @author freddy
 *
 */
public class XesHelper {
	static final String ATTR_CONCEPT = "concept:name";
	static final String ATTR_TIMESTAMP = "time:timestamp";
	
	public String getConceptName(XElement elem) {
		XAttribute elemConcept = elem.getAttributes().get(ATTR_CONCEPT);
		
		if(elemConcept != null) {
			return elemConcept.toString();
		} else {
			return null;
		}
	}
	
	public Date getDate(XElement elem) {
		String elemTimestamp = getTimestamp(elem);
		return DatatypeConverter.parseDateTime(elemTimestamp).getTime();
	}
	
	public String getTimestamp(XElement elem) {
		XAttribute elemTimestamp = elem.getAttributes().get(ATTR_TIMESTAMP);
		
		if(elemTimestamp != null) {
			return elemTimestamp.toString();
		} else {
			return null;
		}
	}
}
