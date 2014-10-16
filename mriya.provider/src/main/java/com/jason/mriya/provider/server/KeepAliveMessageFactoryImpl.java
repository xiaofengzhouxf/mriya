package com.jason.mriya.provider.server;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jason.mriya.client.contants.Constants;
import com.jason.mriya.client.contants.TranProtocol;
import com.jason.mriya.provider.tran.RpcRequest;
import com.jason.mriya.provider.tran.RpcResponse;

public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
	private static final Logger log = LoggerFactory
			.getLogger(KeepAliveMessageFactoryImpl.class);
	private static final int SIZE = 128;

	private static final String HEARTBEAT_REQUEST = "0x911";
	private static final String HEARTBEAT_RESPONSE = "0x119";

	@Override
	public boolean isRequest(IoSession session, Object message) {
		log.info("heart beat: " + message);
		if (message instanceof RpcRequest
				&& (Constants.HEART_BEAT.equals(((RpcRequest) message)
						.getBean()))) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isResponse(IoSession session, Object message) {
		return false;
	}

	@Override
	public Object getRequest(IoSession session) {
		log.info("heart beat: " + HEARTBEAT_REQUEST);
		RpcResponse req = new RpcResponse();
		req.setBean(Constants.HEART_BEAT);
		IoBuffer newIoBuffer = IoBuffer.allocate(SIZE);
		newIoBuffer.put(HEARTBEAT_REQUEST.getBytes());
		req.setOutputBuffer(newIoBuffer.flip());
		req.setSerializeProtocol(TranProtocol.HESSIAN);
		req.setMethod(Constants.HEART_BEAT);
		req.setSeq(1);
		return req;
	}

	@Override
	public Object getResponse(IoSession session, Object request) {
		log.info("heart beat: " + HEARTBEAT_RESPONSE);

		return HEARTBEAT_RESPONSE;
	}

}