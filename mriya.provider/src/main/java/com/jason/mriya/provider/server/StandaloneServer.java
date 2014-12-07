package com.jason.mriya.provider.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.jason.mriya.client.contants.TranProtocol;
import com.jason.mriya.client.exception.MriyaRuntimeException;
import com.jason.mriya.provider.export.RemoteExporter;
import com.jason.mriya.provider.tran.codec.RpcCodecFactory;
import com.jason.mriya.register.RegisterInfo;
import com.jason.mriya.register.RegsiterContainer;

/**
 * 
 * <pre>
 * <p>文件名称: RemoteServer.java</p>
 * 
 * <p>文件功能: 远程服务</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月16日 下午3:56:00</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class StandaloneServer {

	// private static final Logger log = LoggerFactory
	// .getLogger(HessianRemoteProxy.class);

	// private static final int IDLE_TIME = 30;
	private static final long THREAD_POOL_IDLE_TIME_OUT = 120;
	private static final int THREAD_POOL_SIZE = 20;

	private static final String THREAD_POOL = "threadPool";
	private static final String LOG = "log";
	private static final String CODEC = "codec";
	private volatile AtomicBoolean started = new AtomicBoolean();
	// =============

	private int workSize = THREAD_POOL_SIZE;

	private final ServerHandler handler = new ServerHandler();

	public synchronized void start(int port) throws IOException {

		if (!started.get()) {
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			acceptor.setDefaultLocalAddress(new InetSocketAddress(port));
			acceptor.setHandler(handler);
			acceptor.getSessionConfig().setReuseAddress(true);
			// use obb silently discarded, for heart beat.
			// acceptor.getSessionConfig().setOobInline(true);
			// acceptor.getSessionConfig()
			// .setIdleTime(IdleStatus.BOTH_IDLE, IDLE_TIME);
			acceptor.getFilterChain().addLast(CODEC,
					new ProtocolCodecFilter(new RpcCodecFactory()));
			acceptor.getFilterChain().addLast(LOG, new LoggingFilter());
			acceptor.getFilterChain().addLast(
					THREAD_POOL,
					new ExecutorFilter(workSize, workSize,
							THREAD_POOL_IDLE_TIME_OUT, TimeUnit.SECONDS));

			acceptor.bind();
			started.compareAndSet(false, true);

			// register service
			if (RegsiterContainer.getInstance().hasCenter()) {
				Collection<RemoteExporter> listExporters = handler
						.listExporters();

				for (RemoteExporter export : listExporters) {
					RegisterInfo info = new RegisterInfo();
					info.setGroupId(export.getGroupId());
					info.setIp("");
					info.setPort(port);
					info.setProtocol(TranProtocol.valueOf(export.getProtocol())
							.getCode());
					info.setService(export.getName());
					RegsiterContainer.getInstance().registerService(info);
				}
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {

				public void run() {
					// remove service
					if (RegsiterContainer.getInstance().hasCenter()) {
						Collection<RemoteExporter> listExporters = handler
								.listExporters();

						for (RemoteExporter export : listExporters) {
							RegsiterContainer.getInstance()
									.removeRegisterService(export.getGroupId(),
											export.getName());
						}
					}
				}
			});

		}
	}

	/**
	 * 
	 * <pre>
	 * Description：添加暴露器
	 * @param export
	 * @return void
	 * @author name：xiaofeng.zhou
	 * </pre>
	 *
	 */
	public StandaloneServer add(RemoteExporter exporter) {

		if (exporter == null || exporter.getService() == null) {
			throw new MriyaRuntimeException("Exporter info error.");
		}

		handler.addExporter(exporter);
		return this;
	}

	/**
	 * 
	 * <pre>
	 * Description：设置处理线程数
	 * @param workSize
	 * @return
	 * @return StandaloneServer
	 * @author name：xiaofeng.zhou
	 * </pre>
	 *
	 */
	public StandaloneServer setWorkSize(int workSize) {
		this.workSize = workSize;
		return this;
	}

	public boolean isStarted() {
		return started.get();
	}

}
