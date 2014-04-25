package co.nz.leonhardt.master.util;


import org.deckfour.xes.model.XLog;
import org.junit.Assert;
import org.junit.Test;

import co.nz.leonhardt.util.XesUtil;

/**
 * Tests for the XesUtil.
 * 
 * @author freddy
 *
 */
public class XesUtilTest {

	@Test
	public void testFromResource() throws Exception {
		XLog xLog = XesUtil.parseFrom("/logs/extraction_test_log.xes");
		
		Assert.assertNotNull(xLog);
		Assert.assertFalse(xLog.isEmpty());
	}
}
