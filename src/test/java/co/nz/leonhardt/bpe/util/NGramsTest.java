package co.nz.leonhardt.bpe.util;



import org.junit.Assert;
import org.junit.Test;

public class NGramsTest {

	@Test
	public void testNGrams() {
		String seq = "ABCD";
		
		NGrams ngrams = new NGrams(seq);
		
		Assert.assertNotNull(ngrams.getNGrams());
		Assert.assertEquals(10, ngrams.getNGrams().size());
		Assert.assertEquals(0, ngrams.getNumDuplicates(0));
	}
	
	@Test
	public void testNGramsDuplicates() {
		String seq = "ABAB";
		
		NGrams ngrams = new NGrams(seq);
		
		Assert.assertNotNull(ngrams.getNGrams());
		Assert.assertEquals(10, ngrams.getNGrams().size());
		Assert.assertEquals(2, ngrams.getNumDuplicates(2));
		
		seq = "ABCDBCDBCDEFG";
		
		ngrams = new NGrams(seq,3);
		
		Assert.assertNotNull(ngrams.getNGrams());
		Assert.assertEquals(36, ngrams.getNGrams().size());
		Assert.assertEquals(7, ngrams.getNumDuplicates(3));
	}
	
}
