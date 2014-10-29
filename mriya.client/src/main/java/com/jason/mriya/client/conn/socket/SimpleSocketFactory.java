package com.jason.mriya.client.conn.socket;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jason.mriya.client.conn.config.ConnConfig;

/**
 * 
 * <pre>
 * <p>文件名称: SimpleSocketFactory.java</p>
 * 
 * <p>文件功能: </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月18日 下午1:36:47</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class SimpleSocketFactory implements KeyedPoolableObjectFactory {
	private static final Logger log = LoggerFactory
			.getLogger(SimpleSocketFactory.class);
	private static final int MI_SEC = 1000;
	private ConnConfig connConfig;
	private ExecutorService threadPool;

	public SimpleSocketFactory(ConnConfig connConfig, ExecutorService threadPool) {
		super();
		this.connConfig = connConfig;
		this.threadPool = threadPool;
	}

	public void activateObject(Object key, Object obj) throws Exception {
	}

	public void destroyObject(Object key, Object obj) throws Exception {

		SocketWrapper socket = null;
		socket = (SocketWrapper) obj;
		socket.close();

		log.debug(" [pool close]  {}", socket.isActive());
	}

	public Object makeObject(Object key) throws Exception {

		ServerAddress address = (ServerAddress) key;
		SocketWrapper conn = new SocketWrapper(threadPool);
		conn.setKeepAlive(true);
		conn.setKeepAliveTime(connConfig.getKeepAliveTime());
		conn.setSoTimeout(connConfig.getReadTimeout() * MI_SEC);
		conn.setTcpNoDelay(connConfig.isTcpNoDelay());
		conn.setReuseAddress(connConfig.isReuseAddress());
		conn.setSoLinger(connConfig.getSoLinger() > 0, connConfig.getSoLinger());
		conn.setOOBInline(true);
		conn.setSendBufferSize(connConfig.getSendBufferSize());
		conn.setReceiveBufferSize(connConfig.getReceiveBufferSize());
		conn.connect(
				new InetSocketAddress(address.getHost(), address.getPort()),
				connConfig.getConnectTimeout() * MI_SEC);

		log.debug("[pool create]  {}", conn.isActive());

		return conn;
	}

	public void passivateObject(Object key, Object obj) throws Exception {

	}

	public boolean validateObject(Object key, Object obj) {

		SocketWrapper socket = (SocketWrapper) obj;

		log.debug("[pool validate] {}", socket.isActive());

		return socket.isActive() && !socket.isFailed();
	}

}
