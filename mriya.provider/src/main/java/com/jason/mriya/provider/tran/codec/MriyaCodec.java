package com.jason.mriya.provider.tran.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jason.mriya.client.conn.io.IoBufferInputStream;
import com.jason.mriya.client.conn.io.IoBufferOutputStream;
import com.jason.mriya.client.conn.socket.SocketWrapper;
import com.jason.mriya.client.contants.Constants;
import com.jason.mriya.client.contants.TranProtocol;
import com.jason.mriya.client.exception.MriyaRuntimeException;
import com.jason.mriya.provider.tran.RpcRequest;
import com.jason.mriya.provider.tran.RpcResponse;

public class MriyaCodec implements Codec {
	private static final Logger log = LoggerFactory.getLogger(MriyaCodec.class);
	private static final int _1024 = 1024;

	public IoBufferOutputStream encode(Object obj) {

		try {
			RpcResponse resp = (RpcResponse) obj;
			IoBuffer buf = IoBuffer.allocate(_1024);
			buf.setAutoExpand(true);
			buf.put(resp.getVersion());// version
			buf.put(resp.getSerializeProtocol() != null ? resp
					.getSerializeProtocol().getCode() : TranProtocol.HESSIAN
					.getCode());// protocol
			byte[] beanNameBytes = resp.getBean().getBytes(
					Constants.NAME_CHARSET);// byte[]
											// bean
											// name
											// (all
											// in
											// us-ascii)
			buf.put((byte) beanNameBytes.length);// byte beanname length
			buf.put(beanNameBytes); // byte[] beanName
			byte[] methodNameBytes = resp.getMethod().getBytes(
					Constants.NAME_CHARSET);// byte[] method name (all in
			// us-ascii)
			buf.put((byte) methodNameBytes.length); // byte methodname length
			buf.put(methodNameBytes); // byte[] methodname
			buf.putInt(resp.getSeq()); // int seq
			buf.putInt(resp.getOutputBuffer().limit());// total content length
			buf.put(resp.getOutputBuffer());// content

			return new IoBufferOutputStream(buf);
		} catch (Throwable e) {
			throw new MriyaRuntimeException("encode exception.", e);
		}
	}

	@Override
	public Object decode(IoBufferInputStream input) {

		IoBuffer in = input.getBuffer();

		RpcRequest request = new RpcRequest();
		byte version = in.get();
		switch (version) {

		case 1:

			byte protocol = in.get();
			request.setSerializeProtocol(TranProtocol.convert(protocol));
			if (protocol == 100) {

				// beanname
				byte beanNameLenth = in.get();
				byte[] beanname = new byte[beanNameLenth];
				for (byte i = 0; i < beanNameLenth; i++) {
					beanname[i] = in.get();
				}
				try {
					request.setBean(new String(beanname, Constants.NAME_CHARSET));
				} catch (Throwable e) {
					throw new MriyaRuntimeException("encode exception.", e);
				}
				// seq
				in.getInt();

				// content
				int length = in.getInt();

				byte[] dst = new byte[length];
				in.get(dst);
				IoBuffer newIoBuffer = IoBuffer.allocate(length);
				newIoBuffer.put(dst);
				newIoBuffer.flip();
				IoBufferInputStream bufferInputStream = new IoBufferInputStream(
						newIoBuffer);
				request.setInputStream(bufferInputStream);
			}
			break;

		case Constants.HEART_BEAT_REQ:
			log.debug("heart beat..");
			request.setBean(Constants.HEART_BEAT);
		}

		if (in.hasRemaining()) {

			in.get(new byte[in.remaining()]);
		}

		return request;
	}
}
