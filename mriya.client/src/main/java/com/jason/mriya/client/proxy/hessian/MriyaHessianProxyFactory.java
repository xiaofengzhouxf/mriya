package com.jason.mriya.client.proxy.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.HessianRemoteObject;
import com.caucho.hessian.io.SerializerFactory;
import com.jason.mriya.client.conn.config.ConnConfig;
import com.jason.mriya.client.conn.config.ConnURL;
import com.jason.mriya.client.conn.socket.SocketPool;
import com.jason.mriya.client.conn.socket.SocketWrapper;
import com.jason.mriya.client.proxy.BaseProxyFactory;

/**
 * 
 * <pre>
 * <p>文件名称: MriyaHessianProxyFactory.java</p>
 * 
 * <p>文件功能: hessian 序列化包装</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月18日 下午2:29:32</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class MriyaHessianProxyFactory extends BaseProxyFactory {
	private static final int _1000 = 1000;
	private ClassLoader loader;
	private SerializerFactory serializerFactory;
	private boolean hessian2Request = true;
	private boolean hessian2Response = true;
	private boolean connectionKeepAlive = false;
	private SocketPool socketPool;

	public MriyaHessianProxyFactory() {
		this(new ConnConfig());
	}

	public MriyaHessianProxyFactory(ConnConfig networkConfig) {
		super(networkConfig);
		this.connectionKeepAlive = false;
	}

	public MriyaHessianProxyFactory(ConnConfig networkConfig,
			SocketPool socketPool) {
		super(networkConfig);
		this.socketPool = socketPool;
		this.connectionKeepAlive = true;
	}

	public Object create(Class<?> api, String url, String groupId, String name,
			ClassLoader loader) {
		if (api == null)
			throw new NullPointerException(
					"api must not be null for HessianProxyFactory.create()");
		InvocationHandler handler = null;
		ConnURL u = new ConnURL(url);
		handler = new HessianRemoteProxy(u, this);

		this.loader = loader;

		return Proxy.newProxyInstance(loader, new Class[] { api,
				HessianRemoteObject.class }, handler);

	}

	public Object create(Class<?> api, String groupId, String name, String url) {
		return create(api, url, null, null, Thread.currentThread()
				.getContextClassLoader());
	}

	public SocketWrapper getSocket(String host, int port) throws Exception {
		if (connectionKeepAlive) {
			return socketPool.getSocket(host, port);
		}
		return createSocket(host, port);
	}

	public SocketWrapper createSocket(String host, int port) throws IOException {
		SocketWrapper conn = new SocketWrapper(null);
		conn.setSoTimeout(getReadTimeout() * _1000);
		conn.setTcpNoDelay(isTcpNoDelay());
		conn.setReuseAddress(isReuseAddress());
		conn.setSoLinger(getSoLinger() > 0, getSoLinger());
		conn.setSendBufferSize(getSendBufferSize());
		conn.setReceiveBufferSize(getReceiveBufferSize());
		conn.connect(new InetSocketAddress(host, port), getConnectTimeout()
				* _1000);
		return conn;
	}

	public void destroySocket(String host, int port, SocketWrapper socket)
			throws Exception {
		if (connectionKeepAlive) {
			socketPool.returnSocket(host, port, socket);
		} else {
			socket.close();
		}
	}

	/**
	 * Gets the serializer factory.
	 */
	public SerializerFactory getSerializerFactory() {
		if (serializerFactory == null)
			serializerFactory = new SerializerFactory(loader);

		return serializerFactory;
	}

	public AbstractHessianOutput getHessianOutput(OutputStream os) {
		AbstractHessianOutput out;

		if (hessian2Request)
			out = new Hessian2Output(os);
		else {
			HessianOutput out1 = new HessianOutput(os);
			out = out1;

			if (hessian2Response)
				out1.setVersion(2);
		}

		out.setSerializerFactory(getSerializerFactory());

		return out;
	}

	public AbstractHessianInput getHessianInput(InputStream is) {
		return getHessian2Input(is);
	}

	public AbstractHessianInput getHessian1Input(InputStream is) {
		AbstractHessianInput in;

		in = new HessianInput(is);

		in.setSerializerFactory(getSerializerFactory());

		return in;
	}

	public AbstractHessianInput getHessian2Input(InputStream is) {
		AbstractHessianInput in;

		in = new Hessian2Input(is);

		in.setSerializerFactory(getSerializerFactory());

		return in;
	}

	public boolean isHessian2Request() {
		return hessian2Request;
	}

	public void setHessian2Request(boolean hessian2Request) {
		this.hessian2Request = hessian2Request;
	}

	public boolean isHessian2Response() {
		return hessian2Response;
	}

	public void setHessian2Response(boolean hessian2Response) {
		this.hessian2Response = hessian2Response;
	}

}
