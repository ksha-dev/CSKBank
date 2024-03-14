package cache;

import exceptions.AppException;

public abstract class Cache<K, V> {
	public abstract V get(K key) throws AppException;
	
	protected abstract void put(K key, V value);

	protected abstract V fetchData(K key) throws AppException;

	public abstract void clear();
}
