package co.nz.leonhardt.util;

/**
 * An ID generator should generate unique IDs
 * and keep track of the number of generated IDs.
 * 
 * @author Frederik Leonhardt
 *
 */
public interface IdGenerator<T> {
	
	/**
	 * Return the next unassigned ID.
	 * @return
	 */
	public T getNextId();
	
	/**
	 * Return the total number of generated IDs.
	 * @return
	 */
	public long getNumIds();
}
