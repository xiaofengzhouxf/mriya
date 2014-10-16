package com.jason.mriya.client.conn.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jason.mriya.client.contants.Constants;

/**
 * 
 * <pre>
 * 文件功能:
 * 编程者: xiaofeng.zhou
 * 初作时间: 2014年10月13日 下午5:13:18
 * 版本: version 1.0
 * </pre>
 */
public class SocketWrapper extends Socket {
	private static final Logger log = LoggerFactory
			.getLogger(SocketWrapper.class);
	private static final int DEFAULT_IDLE_TIME_OUT = 30 * 1000;

	private ExecutorService threadPool;

	private boolean failed;
	private int keepAliveTime = DEFAULT_IDLE_TIME_OUT;

	public SocketWrapper(ExecutorService threadPool) {
		super();
		this.threadPool = threadPool;
	}

	public SocketWrapper(InetAddress address, int port, InetAddress localAddr,
			int localPort, ExecutorService threadPool) throws IOException {
		super(address, port, localAddr, localPort);
	}

	public SocketWrapper(InetAddress address, int port,
			ExecutorService threadPool) throws IOException {
		super(address, port);
		this.threadPool = threadPool;
	}

	public SocketWrapper(Proxy proxy, ExecutorService threadPool) {
		super(proxy);
		this.threadPool = threadPool;
	}

	public SocketWrapper(SocketImpl impl, ExecutorService threadPool)
			throws SocketException {
		super(impl);
		this.threadPool = threadPool;
	}

	public SocketWrapper(String host, int port, InetAddress localAddr,
			int localPort, ExecutorService threadPool) throws IOException {
		super(host, port, localAddr, localPort);
		this.threadPool = threadPool;
	}

	public SocketWrapper(String host, int port, ExecutorService threadPool)
			throws UnknownHostException, IOException {
		super(host, port);
		this.threadPool = threadPool;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public int getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(int keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public boolean isActive() {
		return !failed && super.isConnected() && !super.isClosed()
				&& !super.isInputShutdown() && !super.isOutputShutdown();
	}

	@Override
	public void connect(SocketAddress endpoint) throws IOException {
		super.connect(endpoint);

		if (super.getKeepAlive() && threadPool != null) {
			threadPool.execute(new CheckIdle(this));
		}
	}

	@Override
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		super.connect(endpoint, timeout);

		if (super.getKeepAlive() && threadPool != null) {
			threadPool.execute(new CheckIdle(this));
		}
	}

	@Override
	public synchronized void close() throws IOException {
		super.close();

	}

	private static class CheckIdle implements Runnable {

		private SocketWrapper socket;

		public CheckIdle(SocketWrapper socket) {
			super();
			this.socket = socket;
		}

		@Override
		public void run() {
			while (socket != null && !socket.isClosed()) {
				try {
					Thread.sleep(socket.getKeepAliveTime());
				} catch (InterruptedException e) {
				}

				try {
					socket.sendUrgentData(Constants.HEART_BEAT_REQ);

					log.debug("send heart beat");
				} catch (IOException e) {
					log.error("socket exception.", e);
					try {
						socket.close();
					} catch (IOException e1) {
					}
				}
			}
		}
	}

}
