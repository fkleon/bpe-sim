package co.nz.leonhardt.bpe.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Time helpers.
 * 
 * @author freddy
 *
 */
public class TimeUtil {
	
	/**
	 * Calculates the time difference between the given dates in the target
	 * time unit.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param target
	 * @return
	 */
	public static Long calcTimeDiff(Date startDate, Date endDate, TimeUnit target) {
		return calcTimeDiff(startDate.getTime(), endDate.getTime(), target);
	}
	
	/**
	 * Calculates the time difference between the given milliseconds in the target
	 * time unit.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param target
	 * @return
	 */
	public static Long calcTimeDiff(Long millisStart, Long millisEnd, TimeUnit target) {
		Long targetStart =  target.convert(millisStart, TimeUnit.MILLISECONDS);
		Long targetEnd = target.convert(millisEnd, TimeUnit.MILLISECONDS);
		return targetEnd - targetStart;
	}
}
