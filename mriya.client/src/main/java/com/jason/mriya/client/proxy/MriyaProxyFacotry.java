package com.jason.mriya.client.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jason.mriya.client.conn.config.ConnConfig;
import com.jason.mriya.client.conn.config.ConnURL;
import com.jason.mriya.client.conn.socket.SimpleSocketPool;
import com.jason.mriya.client.contants.TranProtocol;
import com.jason.mriya.client.exception.MriyaRuntimeException;
import com.jason.mriya.client.proxy.hessian.MriyaHessianProxyFactory;

/**
 * 
 * <pre>
 * 文件功能: 服务代理工厂
 * 
 * 使用方式：
 * {@code
 * NetworkConfig config = new NetworkConfig();
 * 		config.setConnectTimeout(20);
 * 
 * 		MriyaProxyFacotry factory = new MriyaProxyFacotry(config);
 * 
 * 		HelloWorld hello = (HelloWorld) factory.create(HelloWorld.class,
 * 				"hessian://localhost:8080/hell1o");
 * }
 * 
 * socket 连接配置见 NetworkConfig
 * 
 * 编程者: xiaofeng.zhou
 * 初作时间: 2014年9月19日 上午9:27:56
 * 版本: version 1.0
 * </pre>
 */
public class MriyaProxyFacotry extends BaseProxyFactory {

	public MriyaProxyFacotry() {
		super(new ConnConfig());
	}

	public MriyaProxyFacotry(ConnConfig networkConfig) {
		super(networkConfig);
	}

	public MriyaProxyFacotry(ConnConfig networkConfig, ClassLoader loader) {
		super(networkConfig, loader);
	}

	@Override
	public Object create(Class<?> api, String url, ClassLoader loader) {

		ConnURL u = new ConnURL(url);

		if (u.getProtocol() == TranProtocol.HESSIAN
				|| u.getProtocol() == TranProtocol.HTTP) {

			MriyaHessianProxyFactory hf = new MriyaHessianProxyFactory(
					super.getNetworkConfig(), new SimpleSocketPool(
							super.getNetworkConfig()));

			return hf.create(api, url, loader);

		} else {
			throw new MriyaRuntimeException("Hessian not suppor for: " + url);
		}
	}

	@Override
	public Object create(Class<?> api, String url) {

		return create(api, url, Thread.currentThread().getContextClassLoader());
	}

}
