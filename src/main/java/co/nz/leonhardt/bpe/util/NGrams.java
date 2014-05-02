package co.nz.leonhardt.bpe.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tokenizes the given string into n-grams of the given size.
 * 
 * @author freddy
 *
 */
public class NGrams {
	
	/** The original input text */
	private final String text;
	
	/** All n-grams */
	private final List<String> nGrams = new ArrayList<>();
	
	private final Map<String, AtomicInteger> nGramCount = new HashMap<>();
	
	public NGrams(final String text) {
		this(text, text.length());
	}
	
	public NGrams(final String text, final int maxSize) {
		this.text = text;
		
		buildNGrams(text.toCharArray(), maxSize);
		
		System.out.println(nGrams);
	}
	
	/**
	 * Builds n-grams of a given size.
	 * 
	 * @param chars
	 * @param maxSize
	 */
	private void buildNGrams(char[] chars, int maxSize) {
		int nGramSize = 0;
		StringBuilder sb = null;

		for(int i = 0; i < chars.length; i++) {
			String cha = Character.toString(chars[i]);
		
			// 1 add the char itself
			sb = new StringBuilder(cha);
			add(cha);
			nGramSize = 1;
			
			// insert prevs of the char
			while (i > 0 && nGramSize < maxSize) {
				cha = Character.toString(chars[--i]);
				sb.insert(0,cha);
				add(sb.toString());
				nGramSize++;
			}

			// go back to initial position
			i += nGramSize-1;
		}
	}
	
	private void add(String nGram) {
		nGrams.add(nGram);
		if (nGramCount.containsKey(nGram)) {
			nGramCount.get(nGram).incrementAndGet();
		} else {
			nGramCount.put(nGram, new AtomicInteger(1));
		}
	}
	
	public String getText() {
		return text;
	}
	
	public List<String> getNGrams() {
		return nGrams;
	}
	
	public int getNumDuplicates(int minSize) {
		int duplicateCount = 0;
		for (String nGram: nGramCount.keySet()) {
			if(nGram.length() >= minSize) {
				int nGramCnt = nGramCount.get(nGram).get();
				if (nGramCnt > 1) {
					duplicateCount += nGramCnt;
				}
			}
		}
		
		return duplicateCount;
	}
	
}