package co.nz.leonhardt.bpe.processing;

/**
 * 
 * @author freddy
 *
 * @param <T>
 */
public abstract class NumericalMetricExtractor<T extends Number> implements MetricExtractor<T> {

	/**
	 * Returns the unique name associated with this numeric attribute
	 * @return
	 * @deprecated use getMetricName() instead.
	 */
	@Deprecated
	public String getNumericName() {
		return this.getMetricName();
	}
}
