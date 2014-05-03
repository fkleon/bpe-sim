package co.nz.leonhardt.bpe.categories;

import jsat.classifiers.CategoricalData;

/**
 * Possible outcomes of a case.
 * 
 * @author freddy
 *
 */
public enum Outcome implements NominalValue {
	ACCEPTED(0), DECLINED(1), UNDECIDED(2), CANCELLED(3),;

	private final int intValue;
	
	private Outcome(int intValue) {
		this.intValue = intValue;
	};

	/**
	 * Creates an outcome from a string.
	 * 
	 * @param outcome
	 * @return
	 */
	public static Outcome fromString(String outcome) {
		switch (outcome.toUpperCase()) {
			case "ACCEPTED":
				return ACCEPTED;
			case "DECLINED":
				return DECLINED;
			case "UNDECIDED":
				return UNDECIDED;
			default:
				return null;
		}
	}
	
	/**
	 * Creates an outcome from an int.
	 * 
	 * @param outcome
	 * @return
	 */
	public static Outcome fromInt(int outcome) {
		switch (outcome) {
			case 0:
				return ACCEPTED;
			case 1:
				return DECLINED;
			case 2:
				return UNDECIDED;
			default:
				throw new IllegalArgumentException("No such category.");
		}
	}

	@Override
	public CategoricalData getCategoricalData() {
		CategoricalData cd = new CategoricalData(Outcome.values().length);
		cd.setCategoryName("Outcome");

		int i = 0;
		for (Outcome o : Outcome.values()) {
			cd.setOptionName(o.toString(), i++);
		}

		return cd;
	}

	@Override
	public int getIntValue() {
		return this.intValue;
	}
}