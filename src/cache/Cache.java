package cache;

import java.util.LinkedList;
import java.util.Map;

import exceptions.AppException;

public abstract class Cache<K, V> {

	private int capacity;
	private Map<K, V> cacheData;
	private LinkedList<K> cacheKeyOrder;

	protected Cache(int capacity) {
		this.capacity = capacity;
	}

	protected abstract V fetchData(K key) throws AppException;

	public final V get(K key) throws AppException {
		if (cacheData.containsKey(key)) {
			cacheKeyOrder.remove(key);
			cacheKeyOrder.addFirst(key);
			return cacheData.get(key);
		} else {
			V value = fetchData(key);
			put(key, value);
			return value;
		}
	}

	private final void put(K key, V val) {
		if (cacheKeyOrder.size() >= capacity) {
			K keyRemoved = cacheKeyOrder.removeLast();
			cacheData.remove(keyRemoved);
		}
		cacheKeyOrder.addFirst(key);
		cacheData.put(key, val);
	}

	final void clear() {
		cacheData.clear();
		cacheKeyOrder.clear();
	}
}
