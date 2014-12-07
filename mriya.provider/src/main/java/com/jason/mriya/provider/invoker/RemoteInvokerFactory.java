package com.jason.mriya.provider.invoker;

import java.util.Collection;
import java.util.HashMap;

import com.jason.mriya.client.contants.TranProtocol;
import com.jason.mriya.client.exception.MriyaRuntimeException;
import com.jason.mriya.provider.export.RemoteExporter;
import com.jason.mriya.provider.invoker.hessian.HessianInvoker;

/**
 * 
 * <pre>
 * <p>文件名称: RemoteInvokerFactory.java</p>
 * 
 * <p>文件功能: RemoteInvoker 工厂</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月22日 上午8:41:12</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class RemoteInvokerFactory {

	private final static HashMap<String, RemoteInvoker> invokerCache = new HashMap<String, RemoteInvoker>(
			10);
	private final static HashMap<String, RemoteExporter> exportCache = new HashMap<String, RemoteExporter>(
			10);

	public static RemoteInvoker create(RemoteExporter exporter) {

		if (exporter == null) {
			throw new MriyaRuntimeException("exporter can't be null.");
		}

		if (invokerCache.containsKey(exporter.getName())) {
			return invokerCache.get(exporter.getName());
		}

		RemoteInvoker invoker = null;
		// FIXME 后续来考虑扩展，serviceloader之类
		if (TranProtocol.valueOf(exporter.getProtocol().toUpperCase()) == TranProtocol.HESSIAN) {

			invoker = new HessianInvoker(exporter.getName(),
					exporter.getGroupId(), exporter.getService(),
					exporter.getServiceInterface() != null ? exporter
							.getServiceInterface() : exporter.getService()
							.getClass());
			invokerCache.put(exporter.getName(), invoker);

			exportCache.put(exporter.getGroupId() + exporter.getName(),
					exporter);
		} else {
			throw new MriyaRuntimeException("Not support this protocol: "
					+ exporter.getProtocol());
		}

		return invoker;

	}

	public static RemoteInvoker lookup(String bean) {
		return invokerCache.get(bean);
	}

	public static Collection<RemoteExporter> listService() {
		return exportCache.values();
	}
}
