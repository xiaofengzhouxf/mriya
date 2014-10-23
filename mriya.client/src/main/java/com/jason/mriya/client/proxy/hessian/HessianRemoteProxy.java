package com.jason.mriya.client.proxy.hessian;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.WeakHashMap;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.services.server.AbstractSkeleton;
import com.jason.mriya.client.conn.config.ConnURL;
import com.jason.mriya.client.conn.io.IoBufferOutputStream;
import com.jason.mriya.client.conn.socket.SocketWrapper;
import com.jason.mriya.client.contants.Constants;
import com.jason.mriya.client.contants.TranProtocol;
import com.jason.mriya.client.exception.MriyaRuntimeException;
import com.jason.mriya.client.utils.CommonUtils;

/**
 * 
 * <pre>
 * <p>文件名称: RemoteProxy.java</p>
 * 
 * <p>文件功能: </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月18日 下午1:37:30</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class HessianRemoteProxy implements InvocationHandler, Serializable {
	private static final Logger log = LoggerFactory
			.getLogger(HessianRemoteProxy.class);
	private String host;
	private int port;
	private String beanName;
	private MriyaHessianProxyFactory _factory;
	private WeakHashMap<Method, String> _mangleMap = new WeakHashMap<Method, String>();
	private CharsetDecoder charsetDecoder = Charset.forName(
			Constants.NAME_CHARSET).newDecoder();

	private TranProtocol protocol;

	public HessianRemoteProxy(ConnURL url,
			MriyaHessianProxyFactory syncHessianProxyFactory) {
		super();
		this.host = url.getHost();
		this.port = url.getPort();
		this.beanName = url.getQuery();
		this.protocol = url.getProtocol();
		this._factory = syncHessianProxyFactory;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1380891452634342681L;

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String mangleName;

		synchronized (_mangleMap) {
			mangleName = _mangleMap.get(method);
		}

		if (mangleName == null) {
			String methodName = method.getName();
			Class<?>[] params = method.getParameterTypes();

			// equals and hashCode are special cased
			if (methodName.equals("equals") && params.length == 1
					&& params[0].equals(Object.class)) {
				Object value = args[0];
				if (value == null || !Proxy.isProxyClass(value.getClass()))
					return Boolean.FALSE;

				Object proxyHandler = Proxy.getInvocationHandler(value);

				if (!(proxyHandler instanceof HessianRemoteProxy))
					return Boolean.FALSE;

				HessianRemoteProxy handler = (HessianRemoteProxy) proxyHandler;

				return equals(handler);
			} else if (methodName.equals("hashCode") && params.length == 0)
				return hashCode();
			else if (methodName.equals("getHessianType"))
				return proxy.getClass().getInterfaces()[0].getName();
			else if (methodName.equals("getHessianURL"))
				return "tcp://" + host + ":" + port + "/" + beanName;
			else if (methodName.equals("toString") && params.length == 0)
				return toString();

			if (!_factory.isOverloadEnabled())
				mangleName = method.getName();
			else
				mangleName = mangleName(method);

			synchronized (_mangleMap) {
				_mangleMap.put(method, mangleName);
			}
		}

		SocketWrapper conn = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug("Mriay Hessian proxy [{} ] calling {}", toString(),
						mangleName);
			}

			conn = _factory.getSocket(host, port);

			conn = sendRequest(conn, mangleName, args);

			return dealResponse(conn, method);

		} catch (java.net.SocketException e) {

			if (conn != null) {
				conn.setFailed(true);
			}
			log.error("socket error", e);
			throw new MriyaRuntimeException(e);
		} catch (Exception e) {
			throw new MriyaRuntimeException(e);
		} finally {
			try {
				if (conn != null)
					_factory.destroySocket(host, port, conn);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
	}

	private Object dealResponse(SocketWrapper conn, Method method)
			throws Throwable {

		InputStream is = null;

		is = conn.getInputStream();
		BufferedInputStream bufferIs = new BufferedInputStream(is);

		boolean chunked = false;
		if (protocol == TranProtocol.HTTP) {
			// to read http headers
			String line = null;
			while (!(line = readLine(bufferIs)).isEmpty()) {
				if (line.startsWith("Transfer-Encoding:")
						&& line.substring(line.indexOf(':') + 1).trim()
								.equals("chunked")) {
					chunked = true;
				}
			}
		}
		if (chunked) {
			throw new MriyaRuntimeException("Not support trunk transfer.");
		}

		byte version = (byte) bufferIs.read();// version

		if (version != 1) {
			throw new MriyaRuntimeException(
					"version not support or serialize failed.");
		}

		byte protocol = (byte) bufferIs.read();
		byte beanLength = (byte) bufferIs.read();
		byte[] beanname = new byte[beanLength];
		bufferIs.read(beanname);
		@SuppressWarnings("unused")
		String beanNameStr = new String(beanname, Constants.NAME_CHARSET);

		// System.out.println("beanname: " + beanNameStr);

		byte methodLength = (byte) bufferIs.read();
		byte[] methodname = new byte[methodLength];
		bufferIs.read(methodname);
		String methodNameStr = new String(methodname, Constants.NAME_CHARSET);

		// System.out.println("methodNameStr: " + methodNameStr);

		byte[] seq = new byte[4];
		bufferIs.read(seq);

		byte[] totalLength = new byte[4];
		bufferIs.read(totalLength);

		int total = CommonUtils.byteToIntBigend(totalLength);
		byte[] contentBytes = new byte[total];
		bufferIs.read(contentBytes);

		ByteArrayInputStream bio = null;

		try {
			bio = new ByteArrayInputStream(contentBytes);

			// 异常
			if (Constants.EXCEPTION_METHOD.equals(methodNameStr)) {
				Object deserial = CommonUtils.deserial(bio);
				if (deserial != null
						&& deserial instanceof MriyaRuntimeException) {
					throw (MriyaRuntimeException) deserial;
				} else {
					throw new MriyaRuntimeException("server unknow exception.");
				}
			} else if (Constants.HEART_BEAT.equals(methodNameStr)) {
				log.error(" heart beat...... ");
				return null;
			}

			if (protocol == TranProtocol.HESSIAN.getCode()) {
				AbstractHessianInput in;

				int code = bio.read();

				if (code == 'H') {
					bio.read();// int major
					bio.read();// int minor

					in = _factory.getHessian2Input(bio);

					Object value = in.readReply(method.getReturnType());

					return value;
				} else if (code == 'r') {
					bio.read();// int major
					bio.read();// int minor

					in = _factory.getHessianInput(bio);

					in.startReplyBody();

					Object value = in.readObject(method.getReturnType());

					if (value instanceof InputStream) {
						value = new ResultInputStream(conn, is, in,
								(InputStream) value);
						is = null;
						conn = null;
						bio = null;
					} else
						in.completeReply();

					return value;
				} else
					throw new HessianProtocolException("'" + (char) code
							+ "' is an unknown code");
			} else {
				throw new MriyaRuntimeException("[" + protocol
						+ "] protocol not support.");
			}
		} finally {
			try {
				if (bio != null)
					bio.close();
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}

	}

	/**
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String readLine(InputStream is) throws IOException {
		IoBuffer buffer = IoBuffer.allocate(30);
		buffer.setAutoExpand(true);
		while (true) {
			byte b = (byte) is.read();
			if (b == 13) {
				byte b2 = (byte) is.read();
				if (b2 != 10) {
					throw new IOException(
							"Serialize Decode exception. CR must be with a LR");
				} else {
					break;
				}
			} else {
				buffer.put(b);
			}
		}

		buffer.flip();
		if (!buffer.hasRemaining()) {
			return "";
		}
		String line = buffer.getString(charsetDecoder);
		charsetDecoder.reset();
		return line;
	}

	private SocketWrapper sendRequest(SocketWrapper conn, String mangleName,
			Object[] args) throws Exception {

		OutputStream os = null;

		try {
			os = conn.getOutputStream();
		} catch (Exception e) {
			throw new HessianRuntimeException(e);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				_factory.getSendBufferSize());

		AbstractHessianOutput out = _factory.getHessianOutput(baos);
		out.call(mangleName, args);
		out.flush();
		if (protocol == TranProtocol.HESSIAN) {
			IoBuffer write = write(os, baos);
			byte[] dst = new byte[write.limit()];
			write.get(dst);
			os.write(dst);
		} else if (protocol == TranProtocol.HTTP) {
			IoBuffer write = write(os, baos);
			byte[] dst = new byte[write.limit()];
			write.get(dst);

			StringBuilder builder = new StringBuilder(256);
			builder.append("POST /").append(beanName).append(" HTTP/1.1")
					.append("\r\n");
			builder.append("Content-Type: application/x-mriya").append("\r\n");
			builder.append("Host: ").append(host).append(':').append(port)
					.append("\r\n");
			builder.append("Connection: keep-alive").append("\r\n");
			builder.append("Content-Length: ").append(dst.length)
					.append("\r\n");
			builder.append("\r\n");
			os.write(builder.toString().getBytes(Constants.NAME_CHARSET));
			os.write(dst);
		} else {
			throw new RuntimeException("[" + protocol
					+ "] portocal not support.");
		}
		return conn;
	}

	private IoBuffer write(OutputStream os, ByteArrayOutputStream baos)
			throws IOException, UnsupportedEncodingException {
		@SuppressWarnings("resource")
		IoBufferOutputStream outBuffer = new IoBufferOutputStream();
		// version
		outBuffer.write(1);
		// protocal
		outBuffer.write(TranProtocol.HESSIAN.getCode());
		// beanname length
		byte[] beanNameBytes = beanName.getBytes(Constants.NAME_CHARSET);
		outBuffer.write(beanNameBytes.length);
		// beanname
		outBuffer.write(beanNameBytes);
		// seq int
		byte[] seqArray = CommonUtils.intToBytesBigend(0);
		outBuffer.write(seqArray);
		// content length
		byte[] totalLengthArray = CommonUtils.intToBytesBigend(baos.size());
		outBuffer.write(totalLengthArray);
		// content
		outBuffer.write(baos.toByteArray());
		// head
		return outBuffer.flip();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((beanName == null) ? 0 : beanName.hashCode());
		result = prime * result + port;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HessianRemoteProxy other = (HessianRemoteProxy) obj;
		if (beanName == null) {
			if (other.beanName != null)
				return false;
		} else if (!beanName.equals(other.beanName))
			return false;
		if (port != other.port)
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Mriay hessian Proxy [beanName=" + beanName + ", port=" + port
				+ ", host=" + host + "]";
	}

	protected String mangleName(Method method) {
		Class<?>[] param = method.getParameterTypes();

		if (param == null || param.length == 0)
			return method.getName();
		else
			return AbstractSkeleton.mangleName(method, false);
	}

	public static final byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	public static final int byteArrayToInt(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

}
