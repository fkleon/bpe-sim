package co.nz.leonhardt.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple implementation of IdGenerator.
 * Just uses integers in a row.
 * 
 * @author Frederik Leonhardt
 *
 */
public class SimpleIdGenerator implements IdGenerator<Integer> {
	private AtomicInteger nextId;
	
	private SimpleIdGenerator() {
		this.nextId = new AtomicInteger(1);
	}
	
	private static SimpleIdGenerator _INSTANCE;
	
	public static SimpleIdGenerator getInstance() {
		if (_INSTANCE == null) {
			_INSTANCE = new SimpleIdGenerator();
		}
		return _INSTANCE;
	}
	
	@Override
	public Integer getNextId() {
		return nextId.getAndIncrement();
	}

	@Override
	public long getNumIds() {
		return this.nextId.get();
	}
}