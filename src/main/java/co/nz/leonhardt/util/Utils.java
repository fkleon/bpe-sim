package co.nz.leonhardt.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


/**
 * Helper utilities and methods.
 * 
 * @author Frederik Leonhardt
 *
 */
public class Utils {

	/**
	 * Returns the progress in percent.
	 * 
	 * @param now iteration currently in
	 * @param total number of iterations
	 * @return percentage of completion
	 */
	public static int calcProgress(int now, int total) {
		Float progress = (((float)now/(float)total)*100.f);
		return progress.intValue();
	}
	
	/**
	 * The TimeTracker class is a helper utility to easily
	 * measure elapsed time spans for debugging.
	 * 
	 * @author Frederik Leonhardt
	 *
	 */
	public static class TimeTracker {
		
		long startTime;
		
		/**
		 * Starts the timer.
		 */
		public void start() {
			this.startTime = System.currentTimeMillis();
			Logger.getAnonymousLogger().info("Started time tracking.");
		}
		
		/**
		 * Returns the milliseconds ellapsed since timer was started.
		 * @return
		 */
		public long elapsedMillis() {
			return (System.currentTimeMillis() - this.startTime);
		}
		
		/**
		 * Returns the time elapsed since timer was started,
		 * formated as String.
		 * @return
		 */
		public String elapsedTime() {
			long ms = elapsedMillis();
			
			long s = (ms / 1000) % 60;
			//long m = (ms / (1000 * 60)) % 60;
			//long h = (ms / (1000 * 60 * 60)) % 24;
			
			return String.format("%02d seconds, %02d milliseconds", s, ms-(s*1000));
		}
		
		/**
		 * Logs the elapsed time to an anonymous logger.
		 * @param description
		 */
		public void logElapsedTime(String description) {
			Logger.getAnonymousLogger().info("["+description+"] Elapsed: " + elapsedTime());
		}
	}
	
	// logging
	public void logf(String format, Object... args) {
		Logger.getAnonymousLogger().info(String.format(format, args));
	}
	
	public void debugf(String format, Object... args) {
		Logger.getAnonymousLogger().fine(String.format(format, args));
	}
	
	public void warnf(String format, Object... args) {
		Logger.getAnonymousLogger().warning(String.format(format, args));
	}
}
