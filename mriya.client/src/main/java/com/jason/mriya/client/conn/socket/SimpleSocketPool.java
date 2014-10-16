package com.jason.mriya.client.conn.socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.jason.mriya.client.conn.config.ConnConfig;

/**
 * 
 * <pre>
 * <p>文件名称: SimpleSocketPool.java</p>
 * 
 * <p>文件功能: </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月18日 下午1:40:16</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class SimpleSocketPool implements SocketPool {
	private static GenericKeyedObjectPool pool;

	private static ExecutorService threadPool;

	private volatile Object lock = new Object();

	public SimpleSocketPool() {
		this(new ConnConfig());
	}

	public SimpleSocketPool(ConnConfig connConfig) {

		if (pool == null) {
			synchronized (lock) {
				if (pool == null) {

					threadPool = Executors.newFixedThreadPool(connConfig
							.getPoolSize());

					pool = new GenericKeyedObjectPool(new SimpleSocketFactory(
							connConfig, threadPool));

					// must check
					pool.setTestOnBorrow(true);
					pool.setTestOnReturn(true);
					pool.setTestWhileIdle(false);

					pool.setNumTestsPerEvictionRun(_numTestsPerEvictionRun);
					pool.setTimeBetweenEvictionRunsMillis(connConfig
							.getIdleTimeout());
					pool.setMinEvictableIdleTimeMillis(connConfig
							.getIdleTimeout());

					// max size
					pool.setMaxActive(connConfig.getPoolSize());
					// max
					pool.setMaxIdle(connConfig.getPoolSize());
					// get time out int milliseconds
					pool.setMaxWait(connConfig.getConnectTimeout() * 1000);
					// exhausted do.
					pool.setWhenExhaustedAction(whenExhaustedAction(
							connConfig.getPoolSize(),
							connConfig.getConnectTimeout() * 1000));

				}
			}
		}
	}

	public SocketWrapper getSocket(String host, int port) throws Exception {
		return (SocketWrapper) pool.borrowObject(new ServerAddress(host, port));
	}

	public void returnSocket(String host, int port, SocketWrapper socket)
			throws Exception {
		pool.returnObject(new ServerAddress(host, port), socket);
	}

	private byte whenExhaustedAction(int maxActive, int maxWait) {
		byte whenExhausted = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
		if (maxActive <= 0) {
			whenExhausted = GenericObjectPool.WHEN_EXHAUSTED_GROW;
		} else if (maxWait == 0) {
			whenExhausted = GenericObjectPool.WHEN_EXHAUSTED_FAIL;
		}
		return whenExhausted;
	}

	private int _numTestsPerEvictionRun = GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		this._numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	@Override
	public ExecutorService getExecutorService() {
		return threadPool;
	}

}
