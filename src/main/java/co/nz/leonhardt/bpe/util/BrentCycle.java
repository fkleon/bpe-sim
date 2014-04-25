package co.nz.leonhardt.bpe.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class implementing Brent's cycle detection (The Teleporting Turtle)
 * 
 * @author freddy
 * 
 */
public class BrentCycle {

	private final List<Integer> func;
	private int lam, mu;
	
	public BrentCycle(final List<Integer> list, int x0) {
		this.func = list;

		findCycle(x0);
	}

	private void findCycle(int x0) {
		// main phase: search successive powers of two
		int power, lam;
		power = lam = 1;
		int turtle = x0;
		int rabbit = f(x0); // f(x0) is the element/node next to x0.
		
		while(turtle != rabbit) {
			if (power == lam) { // time to start a new power of two?
				turtle = rabbit;
				power *= 2;
				lam = 0;
			}
			rabbit = f(rabbit);
			lam++;
		}
		
		// Find the position of the first repetition of length λ
		mu = 0;
		turtle = rabbit = x0;
		
		for(int i = 0; i < lam; i++) {
			rabbit = f(rabbit);
		} // The distance between the hare and tortoise is now λ.
		
		// Next, the hare and tortoise move at same speed till they agree
		while (turtle != rabbit) {
			turtle = f(turtle);
			rabbit = f(rabbit);
			mu++;
		}
		
		this.lam = lam;
	}
	
	public int getLengthOfCycle() {
		return lam;
	}
	
	public int getPositionOfCycle() {
		return mu+1;
	}
	
	/** function to return value of function f(x) **/
	private int f(int p) {
		return func.get(p);
	}
	
	public static void main(String... args) {
		Random rand = new Random();
		int listSize = 9;
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < listSize; i++)
            list.add(rand.nextInt(5)+1);
        
        StringBuffer sb = new StringBuffer();
        for(Integer v: list) {
        	sb.append(v).append(",");
        }
        System.out.println(sb.toString());
        
        BrentCycle bc = new BrentCycle(list, 0);
        
        System.out.println("Length: " + bc.getLengthOfCycle());
        System.out.println("Pos: " + bc.getPositionOfCycle());
	}
}
