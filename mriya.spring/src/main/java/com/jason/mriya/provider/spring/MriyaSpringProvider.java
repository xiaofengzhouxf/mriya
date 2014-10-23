package com.jason.mriya.provider.spring;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.jason.mriya.provider.config.Env;
import com.jason.mriya.provider.export.MriyaRpcExporter;
import com.jason.mriya.provider.server.StandaloneServer;

/**
 * 
 * <pre>
 * @author zhouxiaofeng
 * 
 * 2014年10月20日下午2:46:50
 * </pre>
 */
public class MriyaSpringProvider extends MriyaRpcExporter {

	private static final String MRIYA_SERVER_PORT = "mriya_server_port";
	private volatile StandaloneServer server = null;
	private boolean directStart = true;
	private int workSize = -1;
	private int port = -1;

	public void init() throws IOException {

		if (server == null) {
			synchronized (MRIYA_SERVER_PORT) {

				if (server == null) {
					server = new StandaloneServer();

					if (workSize > 0) {
						server.setWorkSize(workSize);
					}

					if (directStart) {
						start();
					}
				}
			}
		}

		server.add(this);
	}

	@SuppressWarnings("static-access")
	public void start() throws IOException {

		if (server.isStarted()) {
			return;
		}

		String strPort = System.getProperty(MRIYA_SERVER_PORT);
		if (StringUtils.isNotBlank(strPort)) {
			port = Integer.parseInt(strPort);
		}

		server.start(port < 0 ? Env.getServerPort() : port);
	}

	public void setDirectStart(boolean directStart) {
		this.directStart = directStart;
	}

	public void setWorkSize(int workSize) {
		this.workSize = workSize;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
