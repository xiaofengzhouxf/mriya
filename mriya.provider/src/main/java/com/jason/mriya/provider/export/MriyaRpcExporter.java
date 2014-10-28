package com.jason.mriya.provider.export;

import com.jason.mriya.client.contants.TranProtocol;

/**
 * 
 * <pre>
 * <p>文件名称: MriyaRpcExport.java</p>
 * 
 * <p>文件功能: 远程服务暴露</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月18日 上午11:28:11</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class MriyaRpcExporter implements RemoteExporter {

	private String protocol = TranProtocol.HESSIAN.name();

	private String name;
	private Object service;
	private Class<?> inter;

	public MriyaRpcExporter(String protocol, String name, Object service,
			Class<?> inter) {
		super();
		this.protocol = protocol;
		this.name = name;
		this.service = service;
		this.inter = inter;
	}

	public MriyaRpcExporter(String name, Object service, Class<?> inter) {
		super();
		this.name = name;
		this.service = service;
		this.inter = inter;
	}

	public MriyaRpcExporter() {
		super();
	}

	@Override
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public void setService(Object service) {
		this.service = service;
	}

	@Override
	public void setServiceInterface(Class<?> inter) {
		this.inter = inter;
	}

	@Override
	public Object getService() {
		return service;
	}

	@Override
	public Class<?> getServiceInterface() {
		return inter;
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}