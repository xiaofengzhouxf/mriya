package com.jason.mriya.start;

import java.io.IOException;

import junit.framework.TestCase;

import com.jason.mriya.client.HelloWorld;
import com.jason.mriya.provider.export.MriyaRpcExporter;
import com.jason.mriya.provider.server.StandaloneServer;

public class RemoteServerTest extends TestCase {

	public void testStart() throws IOException, InterruptedException {
		// 远程服务初始化

		HelloWorldImpl hello = new HelloWorldImpl();

		MriyaRpcExporter exporter = new MriyaRpcExporter("hello", hello,
				HelloWorld.class);

		new StandaloneServer().add(exporter).start(9090);

		// 启动3分钟
		Thread.sleep(1000 * 60 * 30);

		assertTrue(true);
	}

}
