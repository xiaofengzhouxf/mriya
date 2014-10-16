package com.jason.mriya.client.proxy.hessian;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.AbstractHessianInput;

/**
 * 
 * <pre>
 * 文件功能:
 * 编程者: xiaofeng.zhou
 * 初作时间: 2014年10月14日 下午2:28:28
 * 版本: version 1.0
 * </pre>
 */
public class ResultInputStream extends InputStream {
	private static final Logger log = LoggerFactory
			.getLogger(ResultInputStream.class);
	private Socket _conn;
	private InputStream _connIs;
	private AbstractHessianInput _in;
	private InputStream _hessianIs;

	ResultInputStream(Socket conn, InputStream is, AbstractHessianInput in,
			InputStream hessianIs) {
		_conn = conn;
		_connIs = is;
		_in = in;
		_hessianIs = hessianIs;
	}

	public int read() throws IOException {
		if (_hessianIs != null) {
			int value = _hessianIs.read();

			if (value < 0)
				close();

			return value;
		} else
			return -1;
	}

	public int read(byte[] buffer, int offset, int length) throws IOException {
		if (_hessianIs != null) {
			int value = _hessianIs.read(buffer, offset, length);

			if (value < 0)
				close();

			return value;
		} else
			return -1;
	}

	public void close() throws IOException {
		Socket conn = _conn;
		_conn = null;

		InputStream connIs = _connIs;
		_connIs = null;

		AbstractHessianInput in = _in;
		_in = null;

		InputStream hessianIs = _hessianIs;
		_hessianIs = null;

		try {
			if (hessianIs != null)
				hessianIs.close();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		try {
			if (in != null) {
				in.completeReply();
				in.close();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		try {
			if (connIs != null) {
				connIs.close();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
}