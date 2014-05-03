package co.nz.leonhardt.util;

import java.io.File;
import java.io.InputStream;

import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

/**
 * Xes Helper.
 * 
 * @author freddy
 *
 */
public class XesUtil {

	private static XParser xParser = new XesXmlParser();
	private static XParser xGzipParser = new XesXmlGZIPParser();

		
	public static XLog parseFrom(File f) throws Exception {
		return xParser.parse(f).get(0);
	}
	
	public static XLog parseFrom(InputStream is) throws Exception {
		return xParser.parse(is).get(0);
	}
	
	public static XLog parseFrom(String resource) throws Exception {
		InputStream is = XesUtil.class.getResourceAsStream(resource);
		if(resource.endsWith("gz")) {
			return parseFromGZ(is);
		} else {
			return parseFrom(is);
		}
	}
	
	public static XLog parseFromGZ(InputStream is) throws Exception {
		return xGzipParser.parse(is).get(0);
	}
	
}
