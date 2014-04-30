package co.nz.leonhardt.bpe.categories;

import jsat.classifiers.CategoricalData;

/**
 * Represents a nominal value.
 * 
 * @author freddy
 *
 */
public interface NominalValue {

	/**
	 * Returns the int value of this category.
	 * 
	 * @return
	 */
	public int getIntValue();
	
	/**
	 * Returns the categorial data object associated with this enum.
	 * 
	 * @return
	 */
	public CategoricalData getCategoricalData();
	
}
