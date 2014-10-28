package com.jason.mriya.provider.config;

import org.apache.commons.lang.StringUtils;

public class Env {

	private static final int DEFAULT_PORT = 9090;

	public static int getServerPort() {

		String portStr = System.getProperty("server.port");

		if (StringUtils.isNotBlank(portStr)) {
			return Integer.parseInt(portStr);
		}

		return DEFAULT_PORT;
	}
}
