package co.nz.leonhardt.bpe.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ArrayListFactory<T> {
	
	private ArrayList<T> list;
	
	private ArrayListFactory() {
		this.list = new ArrayList<T>();
	}
	
	public static <T> ArrayListFactory<T> emptyOf(Class<T> tClass) {
		return new ArrayListFactory<T>();
	}
	
	public ArrayListFactory<T> withAll(Collection<T> items) {
		list.addAll(items);
		return this;
	}
	
	public ArrayListFactory<T> with(T items) {
		list.addAll(Arrays.asList(items));
		return this;
	}
	
	public ArrayListFactory<T> withAll(T[] items) {
		list.addAll(Arrays.asList(items));
		return this;
	}

	public ArrayList<T> build() {
		return list;
	}
	
}
