package com.jason.mriya.provider.spring;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import com.jason.mriya.client.conn.config.ConnConfig;
import com.jason.mriya.client.proxy.MriyaProxyFacotry;

/**
 * 
 * <pre>
 * <p>文件名称: MriyaSpringProxyFacotry.java</p>
 * 
 * <p>文件功能: mriya服务代理工厂</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年10月8日 上午11:03:27</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class MriyaSpringConsumer extends MriyaProxyFacotry implements
		FactoryBean<Object> {

	private static final int DEFAULT_IDEL_TIME_OUT = 60 * 10 * 1000;
	private static final int DEFAULT_POOL_SIZE = 8;
	private String groupId;
	private String name;

	private String url;
	private String api;
	// 连接超时时间
	private int connTimeout;
	// 读超时时间
	private int readTimeout;
	// 连接池大小
	private int poolSize = DEFAULT_POOL_SIZE;
	// 连接闲置回收时间
	private int idleTimeout = DEFAULT_IDEL_TIME_OUT;

	private Object obj;

	public MriyaSpringConsumer() {
		super();
		super.getNetworkConfig().setConnectTimeout(connTimeout);
		super.getNetworkConfig().setReadTimeout(readTimeout);
		super.getNetworkConfig().setPoolSize(poolSize);
		super.getNetworkConfig().setIdleTimeout(idleTimeout);
	}

	public MriyaSpringConsumer(ConnConfig networkConfig) {
		super(networkConfig);
	}

	public MriyaSpringConsumer(ConnConfig networkConfig, ClassLoader loader) {
		super(networkConfig, loader);
	}

	@SuppressWarnings("static-access")
	@Override
	public Object getObject() throws Exception {
		if (obj == null) {
			if (StringUtils.isBlank(api)) {
				throw new Exception("api :" + api + " not found.");
			}
			obj = super
					.create(this.getClass().forName(api), groupId, name, url);
		}
		return obj;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getUrl() {
		return url;
	}

	public Object getObj() {
		return obj;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
	public Class<?> getObjectType() {
		return obj != null ? obj.getClass() : null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public int getConnTimeout() {
		return connTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setConnTimeout(int connTimeout) {
		this.connTimeout = connTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
