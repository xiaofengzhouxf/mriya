package com.jason.mriya.client.conn.config;

import com.jason.mriya.client.contants.TranProtocol;

/**
 * <pre>
 * hessian://localhost:8080/hello
 *    protocol = hessian
 *    address = localhost
 *    host = 8080
 *    beanName = hello
 * </pre>
 * 
 */
public class ConnURL {
	private String url;
	private TranProtocol protocol;
	private String host;
	private int port;
	private String query;

	public ConnURL(String url) {
		super();
		this.url = url;
		int idx1 = url.indexOf("://");
		if (idx1 <= 0) {
			throw new IllegalArgumentException("Illegal url:" + url);
		}
		int idx2 = url.indexOf('/', idx1 + 4);
		if (idx2 <= 0) {
			throw new IllegalArgumentException("Illegal url:" + url);
		}

		String upperCase = url.substring(0, idx1).toUpperCase();

		protocol = TranProtocol.valueOf(upperCase);

		query = url.substring(idx2 + 1);
		String hostPort = url.substring(idx1 + 3, idx2);
		int idx3 = hostPort.indexOf(':');
		if (idx3 < 0) {
			host = hostPort;
			port = protocol.getPort();
		} else {
			host = hostPort.substring(0, idx3);
			port = Integer.parseInt(hostPort.substring(idx3 + 1));
		}
	}

	public String getUrl() {
		return url;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getQuery() {
		return query;
	}

	public TranProtocol getProtocol() {
		return protocol;
	}

}
