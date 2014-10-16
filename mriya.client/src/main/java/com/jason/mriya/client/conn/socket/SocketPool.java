package com.jason.mriya.client.conn.socket;

import java.util.concurrent.ExecutorService;

public interface SocketPool {

	SocketWrapper getSocket(String host, int port) throws Exception;

	void returnSocket(String host, int port, SocketWrapper socket)
			throws Exception;

	ExecutorService getExecutorService();
}
