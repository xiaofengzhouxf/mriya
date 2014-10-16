package com.jason.mriya.client;

import java.io.IOException;

import com.jason.mriya.client.conn.config.ConnConfig;
import com.jason.mriya.client.proxy.MriyaProxyFacotry;

public class RemoteClientExample {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		ConnConfig config = new ConnConfig();
		config.setConnectTimeout(20);
		config.setPoolSize(20);
		config.setKeepAliveTime(30);
		final MriyaProxyFacotry factory = new MriyaProxyFacotry(config);

		for (int i = 0; i < 1; i++) {

			new Thread() {

				public void run() {

					while (true) {
						try {
							long currentTimeMillis = System.currentTimeMillis();

							final HelloWorld hello = (HelloWorld) factory
									.create(HelloWorld.class,
											"hessian://localhost:9090/hello");

							hello.hello("hello");

							// System.out.println("result::" + result);

							long during = System.currentTimeMillis()
									- currentTimeMillis;

							// System.out.println(result + "====> " + during);
							try {
								Thread.sleep(100000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}

		Thread.sleep(100000);
	}
}