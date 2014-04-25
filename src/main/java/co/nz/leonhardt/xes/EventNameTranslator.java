package co.nz.leonhardt.xes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Translates event names.
 * 
 * @author freddy
 *
 */
public class EventNameTranslator {

	/** The translation map */
	private static final Map<String, String> eventTranslations;
	static {
		Map<String, String> aMap = new HashMap<>();
		aMap.put("W_Afhandelen leads", "W_Fixing incoming lead");
		aMap.put("W_Completeren aanvraag",
				"W_Filling in information for the application");
		aMap.put("W_Valideren aanvraag", "W_Assessing the application");
		aMap.put("W_Nabellen offertes", "W_Calling after sent offers");
		aMap.put("W_Nabellen incomplete dossiers",
				"W_Calling to add missing information to the application");
		aMap.put("W_Wijzigen contractgegevens", "W_Changing contract data");
		aMap.put("W_Beoordelen fraude", "W_Assessing fraud");

		eventTranslations = Collections.unmodifiableMap(aMap);
	}
	
	private final Logger log = Logger.getLogger("EventNameTranslator");


	/**
	 * Translates the given string.
	 * 
	 * @param inputText
	 * @return
	 */
	public String translate(String inputText) {
		if (eventTranslations.containsKey(inputText)) {
			return eventTranslations.get(inputText);
		} else {
			log.info("No translation for: " + inputText);
			return inputText;
		}
	}
}
