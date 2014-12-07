package com.jason.mriya.client.proxy;

import com.jason.mriya.client.conn.config.ConnConfig;

/**
 * 
 * <pre>
 * <p>文件名称: BaseProxyFactory.java</p>
 * 
 * <p>文件功能:远程服务代理工厂基类 </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月18日 下午1:54:14</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public abstract class BaseProxyFactory {
	public ConnConfig getNetworkConfig() {
		return networkConfig;
	}

	private ClassLoader loader;
	private boolean overloadEnabled = true;
	private ConnConfig networkConfig = null;

	public abstract Object create(Class<?> api, String groupId, String name,
			String url, ClassLoader loader);

	public abstract Object create(Class<?> api, String groupId, String name,
			String url);

	public int getConnectTimeout() {
		return networkConfig.getConnectTimeout();
	}

	public int getReadTimeout() {
		return networkConfig.getReadTimeout();
	}

	public int getReceiveBufferSize() {
		return networkConfig.getReceiveBufferSize();
	}

	public int getSendBufferSize() {
		return networkConfig.getSendBufferSize();
	}

	public int getSoLinger() {
		return networkConfig.getSoLinger();
	}

	public boolean isReuseAddress() {
		return networkConfig.isReuseAddress();
	}

	public boolean isTcpNoDelay() {
		return networkConfig.isTcpNoDelay();
	}

	public void setOverloadEnabled(boolean overloadEnabled) {
		this.overloadEnabled = overloadEnabled;
	}

	public BaseProxyFactory(ConnConfig networkConfig) {
		this(networkConfig, Thread.currentThread().getContextClassLoader());
	}

	public BaseProxyFactory(ConnConfig networkConfig, ClassLoader loader) {
		this.loader = loader;
		this.networkConfig = networkConfig;
		this.loader = loader;
	}

	public ClassLoader getLoader() {
		return loader;
	}

	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}

	public boolean isOverloadEnabled() {
		return overloadEnabled;
	}
}
