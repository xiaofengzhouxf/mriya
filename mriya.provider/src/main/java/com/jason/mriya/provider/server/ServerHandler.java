package com.jason.mriya.provider.server;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jason.mriya.client.conn.io.IoBufferOutputStream;
import com.jason.mriya.client.contants.Constants;
import com.jason.mriya.client.exception.MriyaRuntimeException;
import com.jason.mriya.client.utils.CommonUtils;
import com.jason.mriya.provider.export.RemoteExporter;
import com.jason.mriya.provider.invoker.RemoteInvoker;
import com.jason.mriya.provider.invoker.RemoteInvokerFactory;
import com.jason.mriya.provider.tran.RpcRequest;
import com.jason.mriya.provider.tran.RpcResponse;

/**
 * 
 * <pre>
 * <p>文件名称: ServerHandler.java</p>
 * 
 * <p>文件功能: </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月18日 下午12:50:38</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
class ServerHandler extends IoHandlerAdapter {
	private static final Logger log = LoggerFactory
			.getLogger(ServerHandler.class);

	public ServerHandler() {
		super();
	}

	/**
	 * 
	 * <pre>
	 * Description：添加服务，一个服务名称只能对应一个协议
	 * @param exporter
	 * @return void
	 * @author name：xiaofeng.zhou
	 * </pre>
	 *
	 */
	public void addExporter(RemoteExporter exporter) {
		RemoteInvokerFactory.create(exporter);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		RpcRequest request = (RpcRequest) message;

		if (Constants.HEART_BEAT.equals(request.getBean())) {
			log.info("heat beat....");
			return;
		}

		RpcResponse resp = new RpcResponse();
		resp.setSerializeProtocol(request.getSerializeProtocol());
		RemoteInvoker remoteInvoker = RemoteInvokerFactory.lookup(request
				.getBean());
		try {
			if (remoteInvoker == null) {
				throw new MriyaRuntimeException("bean [" + request.getBean()
						+ "] not found.");
			}

			IoBufferOutputStream os = new IoBufferOutputStream();

			if (remoteInvoker.supportSerializeProtocol() == request
					.getSerializeProtocol()) {
				String methodName = remoteInvoker.handlerRequest(
						request.getInputStream(), os);

				resp.setOutputBuffer(os.flip());
				resp.setBean(request.getBean());
				resp.setMethod(methodName);
				resp.setSeq(request.getSeq());
				session.write(resp);
			} else {
				byte[] serialize = CommonUtils
						.serialize(new MriyaRuntimeException(
								"protocol not support."));
				IoBuffer iobuffer = IoBuffer.wrap(serialize);
				resp.setOutputBuffer(iobuffer);
				resp.setBean(request.getBean());
				resp.setMethod(Constants.EXCEPTION_METHOD);
				resp.setSeq(request.getSeq());
				session.write(resp);
			}

		} catch (Throwable e) {
			// 抛出异常
			byte[] serialize = CommonUtils.serialize(new MriyaRuntimeException(
					"Remote exception.", e));
			IoBuffer iobuffer = IoBuffer.wrap(serialize);
			resp.setOutputBuffer(iobuffer);
			resp.setBean(request != null
					&& StringUtils.isNotBlank(request.getBean()) ? request
					.getBean() : "error");
			resp.setMethod(Constants.EXCEPTION_METHOD);
			resp.setSeq(request != null ? request.getSeq() : 0);
			session.write(resp);
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// session.close(false);
	}

	public Collection<RemoteExporter> listExporters() {

		return RemoteInvokerFactory.listService();
	}
}
