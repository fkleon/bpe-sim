package co.nz.leonhardt.bpe.categories;

import jsat.classifiers.CategoricalData;

public interface CategoricalEnum {

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
