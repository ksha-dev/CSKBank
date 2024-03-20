package cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import exceptions.AppException;
import redis.clients.jedis.Jedis;

public class RedisCache<K, V> extends Cache<K, V> {

	private static final String HOST_NAME = "localhost";
	private static final int PORT = 6379;
	private static final Jedis REDIS_CONNECTOR = new Jedis(HOST_NAME, PORT);
	private final String CACHE_KEY;
	private final int CACHE_SIZE;

	public RedisCache(Class<? extends Cache<K, V>> cacheClass, int capacity) {
		CACHE_KEY = cacheClass.getSimpleName();
		CACHE_SIZE = capacity;
	}

	@Override
	public final V get(K key) throws AppException {
		try {

			if (REDIS_CONNECTOR.hexists(CACHE_KEY.getBytes(), key.toString().getBytes())) {
				return deserialize(REDIS_CONNECTOR.hget(CACHE_KEY.getBytes(), key.toString().getBytes()));
			} else {
				V value = fetchData(key);
				put(key, value);
				return value;
			}
		} finally {
		}
	}

	private final byte[] serialize(Object object) throws AppException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out)) {
			os.writeObject(object);
			return out.toByteArray();
		} catch (IOException e) {
			throw new AppException();
		}
	}

	@SuppressWarnings("unchecked")
	private final V deserialize(byte[] bytes) throws AppException {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				ObjectInputStream os = new ObjectInputStream(in)) {
			return (V) os.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new AppException();
		}
	}

	@Override
	protected void put(K key, V value) {
//		if (REDIS_CONNECTOR.hlen(CACHE_KEY.getBytes()) > CACHE_SIZE) {
//			K keyRemoved = cacheKeyOrder.removeLast();
//			cacheData.remove(keyRemoved);
//		}
//		cacheKeyOrder.addFirst(key);
//		cacheData.put(key, val);
	}

	@Override
	protected V fetchData(K key) throws AppException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		try {

			Jedis jedis = new Jedis("localhost", 6379);
			System.out.println("Connection Successful");
			System.out.println("Server Ping : " + jedis.ping());
//			System.out.println(
//					jedis.hset("Person".getBytes(), "Age".getBytes(), serializeObject(System.getProperties())));

			System.out.println(deserializeObject(jedis.hget("Person".getBytes(), "Age".getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	private static byte[] serializeObject(Object object) throws AppException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out)) {
			os.writeObject(object);
			return out.toByteArray();
		} catch (IOException e) {
			throw new AppException();
		}
	}

	private static Object deserializeObject(byte[] bytes) throws AppException {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				ObjectInputStream os = new ObjectInputStream(in)) {
			return os.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new AppException();
		}
	}
}
