package co.nz.leonhardt.bpe.util;

import org.junit.Assert;
import org.junit.Test;

public class LRSTest {

	@Test
	public void testLRS() {
		String text = "ABCDECDEFCDECDECDEABCBC";
		
		String lrs = LRS.lrs(text);
		Assert.assertNotNull(lrs);
		Assert.assertEquals("CDECDE".length(), lrs.length());
	}
	
}
