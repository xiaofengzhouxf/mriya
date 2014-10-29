package com.jason.mriya.provider.tran.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jason.mriya.client.conn.io.IoBufferInputStream;
import com.jason.mriya.client.conn.io.IoBufferOutputStream;
import com.jason.mriya.client.contants.Constants;
import com.jason.mriya.client.contants.TranProtocol;
import com.jason.mriya.client.exception.MriyaRuntimeException;
import com.jason.mriya.provider.tran.RpcRequest;
import com.jason.mriya.provider.tran.RpcResponse;

public class MriyaCodec implements Codec<RpcRequest, RpcResponse> {
	private static final int STEP_7_FIN = 7;
	private static final int STEP_6_CONTENT = 6;
	private static final int STEP_5_CONTENT_LENGTH = 5;
	private static final int STEP_4_SEQ = 4;
	private static final int STEP_3_BEANNAME = 3;
	private static final int STEP_2_BEANNAME_LEN = 2;
	private static final int STEP_1_PROTOCOL = 1;
	private static final int STEP_0_VERSION = 0;
	private static final Logger log = LoggerFactory.getLogger(MriyaCodec.class);
	private static final int _1024 = 1024;


	private int decodeStep = 0;
	private int contentLength;
	private byte beanNameLenth;

	public IoBufferOutputStream encode(RpcResponse resp) {

		try {
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
	public boolean decode(RpcRequest request, IoBufferInputStream input) {

		IoBuffer in = input.getBuffer();

		switch (decodeStep) {

		case STEP_0_VERSION:
			if (in.remaining() == 0) {
				return false;
			}
			byte version = in.get();
			decodeStep = STEP_1_PROTOCOL;
			if (version == Constants.HEART_BEAT_REQ) {
				log.debug("heart beat..");
				request.setBean(Constants.HEART_BEAT);
			} else if (version != 1) {
				throw new MriyaRuntimeException(
						"version not support protocol. " + version);
			}

		case STEP_1_PROTOCOL:
			if (in.remaining() == 0) {
				return false;
			}
			byte protocol = in.get();
			request.setSerializeProtocol(TranProtocol.convert(protocol));
			decodeStep = STEP_2_BEANNAME_LEN;

			if (protocol != 100) {
				throw new MriyaRuntimeException(
						"version not support protocol. ");
			}

		case STEP_2_BEANNAME_LEN:
			// beanname
			if (in.remaining() == 0) {
				return false;
			}
			beanNameLenth = in.get();

			decodeStep = STEP_3_BEANNAME;

		case STEP_3_BEANNAME:
			if (in.remaining() < beanNameLenth) {
				return false;
			}
			
			byte[] beanname = new byte[beanNameLenth];
			for (byte i = 0; i < beanNameLenth; i++) {
				beanname[i] = in.get();
			}
			try {
				request.setBean(new String(beanname, Constants.NAME_CHARSET));
			} catch (Throwable e) {
				throw new MriyaRuntimeException("encode exception.", e);
			}

			decodeStep = STEP_4_SEQ;

		case STEP_4_SEQ:
			// seq
			if (in.remaining() == 0) {
				return false;
			}
			int seq = in.getInt();
			request.setSeq(seq);
			decodeStep = STEP_5_CONTENT_LENGTH;

		case STEP_5_CONTENT_LENGTH:
			// content
			if (in.remaining() == 0) {
				return false;
			}
			contentLength = in.getInt();

			decodeStep = STEP_6_CONTENT;
		case STEP_6_CONTENT:

			if (in.remaining() < contentLength) {
				return false;
			}
			
			byte[] dst = new byte[contentLength];
			in.get(dst);
			IoBuffer newIoBuffer = IoBuffer.allocate(contentLength);
			newIoBuffer.put(dst);
			newIoBuffer.flip();
			IoBufferInputStream bufferInputStream = new IoBufferInputStream(
					newIoBuffer);
			request.setInputStream(bufferInputStream);
			decodeStep = STEP_7_FIN;
		}
		// if (in.hasRemaining()) {
		// System.out.println(" log remain::: "+ in.remaining() );
		// in.get(new byte[in.remaining()]);
		// }

		return true;
	}

	public void clearStep() {
		decodeStep = STEP_0_VERSION;
	}
	
	
	
}
