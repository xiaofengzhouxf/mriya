package com.jason.mriya.client.conn.config;

/**
 * 
 * <pre>
 * 文件功能: 网络配置
 * 编程者: xiaofeng.zhou
 * 初作时间: 2014年9月19日 上午9:32:24
 * 版本: version 1.0
 * </pre>
 */
public class ConnConfig {
	private int connectTimeout = 20;// in seconds;
	private boolean tcpNoDelay = true;
	private boolean reuseAddress = true;
	private int soLinger = -1;
	private int sendBufferSize = 256;
	private int receiveBufferSize = 1024;
	private int readTimeout = 20;// in seconds;

	// connect pool size
	private int poolSize = 20;
	// connect idle time out Millis.
	private int idleTimeout = 60 * 10 * 1000;
	// heart beat interval millis
	private int keepAliveTime = 30 * 1000;

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public boolean isReuseAddress() {
		return reuseAddress;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}

	public int getSoLinger() {
		return soLinger;
	}

	public void setSoLinger(int soLinger) {
		this.soLinger = soLinger;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public int getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(int keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

}
