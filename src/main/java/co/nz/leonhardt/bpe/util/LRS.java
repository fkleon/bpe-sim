package co.nz.leonhardt.bpe.util;

import java.util.Arrays;

/**
 * Longest Repeted Substring.
 * 
 * @author Robert Sedgewick and Kevin Wayne
 *
 */
public class LRS {
    /**
     * Return the longest common prefix of s and t
     * 
     * @param s
     * @param t
     * @return
     */
    public static String lcp(String s, String t) {
        int n = Math.min(s.length(), t.length());
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) != t.charAt(i))
                return s.substring(0, i);
        }
        return s.substring(0, n);
    }

    /**
     * Return the longest repeated string in s
     * 
     * @param s
     * @return
     */
    public static String lrs(String s) {

        // form the N suffixes
        int N  = s.length();
        String[] suffixes = new String[N];
        for (int i = 0; i < N; i++) {
            suffixes[i] = s.substring(i, N);
        }

        // sort them
        Arrays.sort(suffixes);

        // find longest repeated substring by comparing adjacent sorted suffixes
        String lrs = "";
        for (int i = 0; i < N - 1; i++) {
            String x = lcp(suffixes[i], suffixes[i+1]);
            if (x.length() > lrs.length())
                lrs = x;
        }
        return lrs;
    }
}
