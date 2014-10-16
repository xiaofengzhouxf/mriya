package com.jason.mriya.provider.invoker.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.HessianFactory;
import com.caucho.hessian.io.HessianInputFactory;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.services.server.AbstractSkeleton;
import com.caucho.services.server.ServiceContext;

/**
 * Proxy class for Hessian services.
 */
public class HessianSkeletonProxy extends AbstractSkeleton {
	private static final Logger log = LoggerFactory
			.getLogger(HessianSkeletonProxy.class);

	private boolean _isDebug;

	private HessianInputFactory _inputFactory = new HessianInputFactory();
	private HessianFactory _hessianFactory = new HessianFactory();

	private Object _service;

	/**
	 * Create a new hessian skeleton.
	 * 
	 * @param service
	 *            the underlying service object.
	 * @param apiClass
	 *            the API interface
	 */
	public HessianSkeletonProxy(Object service, Class<?> apiClass) {
		super(apiClass);

		if (service == null)
			service = this;

		_service = service;

		if (!apiClass.isAssignableFrom(service.getClass()))
			throw new IllegalArgumentException("Service " + service
					+ " must be an instance of " + apiClass.getName());
	}

	/**
	 * Create a new hessian skeleton.
	 * 
	 * @param service
	 *            the underlying service object.
	 * @param apiClass
	 *            the API interface
	 */
	public HessianSkeletonProxy(Class<?> apiClass) {
		super(apiClass);
	}

	public void setDebug(boolean isDebug) {
		_isDebug = isDebug;
	}

	public boolean isDebug() {
		return _isDebug;
	}

	public void setHessianFactory(HessianFactory factory) {
		_hessianFactory = factory;
	}

	/**
	 * Invoke the object with the request from the input stream.
	 * 
	 * @param in
	 *            the Hessian input stream
	 * @param out
	 *            the Hessian output stream
	 */
	public String invoke(InputStream is, OutputStream os) throws Exception {
		return invoke(is, os, null);
	}

	/**
	 * Invoke the object with the request from the input stream.
	 * 
	 * @param in
	 *            the Hessian input stream
	 * @param out
	 *            the Hessian output stream
	 */
	@SuppressWarnings({ "resource" })
	public String invoke(InputStream is, OutputStream os,
			SerializerFactory serializerFactory) throws Exception {
		boolean isDebug = false;

		if (!isDebugInvoke()) {
			isDebug = true;

			PrintWriter dbg = createDebugPrintWriter();
			HessianDebugInputStream dIs = new HessianDebugInputStream(is, dbg);
			dIs.startTop2();
			is = dIs;
			HessianDebugOutputStream dOs = new HessianDebugOutputStream(os, dbg);
			dOs.startTop2();
			os = dOs;
		}

		HessianInputFactory.HeaderType header = _inputFactory.readHeader(is);

		AbstractHessianInput in;
		AbstractHessianOutput out;

		switch (header) {
		case CALL_1_REPLY_1:
			in = _hessianFactory.createHessianInput(is);
			out = _hessianFactory.createHessianOutput(os);
			break;

		case CALL_1_REPLY_2:
			in = _hessianFactory.createHessianInput(is);
			out = _hessianFactory.createHessian2Output(os);
			break;

		case HESSIAN_2:
			in = _hessianFactory.createHessian2Input(is);
			in.readCall();
			out = _hessianFactory.createHessian2Output(os);
			break;

		default:
			throw new IllegalStateException(header
					+ " is an unknown Hessian call");
		}

		if (serializerFactory != null) {
			in.setSerializerFactory(serializerFactory);
			out.setSerializerFactory(serializerFactory);
		}

		try {
			return invoke(_service, in, out);
		} finally {
			in.close();
			out.close();

			if (isDebug)
				os.close();
		}
	}

	/**
	 * Invoke the object with the request from the input stream.
	 * 
	 * @param in
	 *            the Hessian input stream
	 * @param out
	 *            the Hessian output stream
	 */
	public String invoke(AbstractHessianInput in, AbstractHessianOutput out)
			throws Exception {
		return invoke(_service, in, out);
	}

	/**
	 * Invoke the object with the request from the input stream.
	 * 
	 * @param in
	 *            the Hessian input stream
	 * @param out
	 *            the Hessian output stream
	 */
	public String invoke(Object service, AbstractHessianInput in,
			AbstractHessianOutput out) throws Exception {
		ServiceContext context = ServiceContext.getContext();

		// backward compatibility for some frameworks that don't read
		// the call type first
		in.skipOptionalCall();

		// Hessian 1.0 backward compatibility
		String header;
		while ((header = in.readHeader()) != null) {
			Object value = in.readObject();

			context.addHeader(header, value);
		}

		String methodName = in.readMethod();
		int argLength = in.readMethodArgLength();

		Method method;

		method = getMethod(methodName + "__" + argLength);

		if (method == null)
			method = getMethod(methodName);

		if (method != null) {
		} else if ("_hessian_getAttribute".equals(methodName)) {
			String attrName = in.readString();
			in.completeCall();

			String value = null;

			if ("java.api.class".equals(attrName))
				value = getAPIClassName();
			else if ("java.home.class".equals(attrName))
				value = getHomeClassName();
			else if ("java.object.class".equals(attrName))
				value = getObjectClassName();

			out.writeReply(value);
			out.close();
			return methodName;
		} else if (method == null) {
			out.writeFault("NoSuchMethodException",
					"The service has no method named: " + in.getMethod(), null);
			out.close();
			return methodName;
		}

		Class<?>[] args = method.getParameterTypes();

		if (argLength != args.length && argLength >= 0) {
			out.writeFault("NoSuchMethod",
					"method " + method
							+ " argument length mismatch, received length="
							+ argLength, null);
			out.close();
			return methodName;
		}

		Object[] values = new Object[args.length];

		for (int i = 0; i < args.length; i++) {
			// XXX: needs Marshal object
			values[i] = in.readObject(args[i]);
		}

		Object result = null;

		try {
			result = method.invoke(service, values);
		} catch (Exception e) {
			Throwable e1 = e;
			if (e1 instanceof InvocationTargetException)
				e1 = ((InvocationTargetException) e).getTargetException();

			log.error(this + " " + e1.toString(), e1);

			out.writeFault("ServiceException", e1.getMessage(), null);
			out.close();
			return methodName;
		}

		// The complete call needs to be after the invoke to handle a
		// trailing InputStream
		in.completeCall();

		out.writeReply(result);

		out.close();
		return methodName;
	}

	protected boolean isDebugInvoke() {
		return (log.isDebugEnabled() || isDebug());
	}

	/**
	 * Creates the PrintWriter for debug output. The default is to write to
	 * java.util.Logging.
	 */
	protected PrintWriter createDebugPrintWriter() throws IOException {
		return new PrintWriter(new LogWriter(log));
	}

	static class LogWriter extends Writer {
		private Logger _log;
		private StringBuilder _sb = new StringBuilder();

		LogWriter(Logger log) {
			_log = log;
		}

		public void write(char ch) {
			if (ch == '\n' && _sb.length() > 0) {
				_log.debug(_sb.toString());
				_sb.setLength(0);
			} else
				_sb.append((char) ch);
		}

		public void write(char[] buffer, int offset, int length) {
			for (int i = 0; i < length; i++) {
				char ch = buffer[offset + i];

				if (ch == '\n' && _sb.length() > 0) {
					_log.debug(_sb.toString());
					_sb.setLength(0);
				} else
					_sb.append((char) ch);
			}
		}

		public void flush() {
		}

		public void close() {
		}
	}
}
