package com.jason.mriya.provider.tran.codec;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jason.mriya.client.conn.io.IoBufferInputStream;
import com.jason.mriya.client.contants.Constants;
import com.jason.mriya.provider.tran.RpcRequest;

/**
 * 
 * <pre>
 * <p>文件名称: RpcDecoder.java</p>
 * 
 * <p>文件功能: </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月17日 下午2:45:01</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class RpcDecoder extends CumulativeProtocolDecoder {
	private static final Logger log = LoggerFactory.getLogger(RpcDecoder.class);

	private MriyaCodec codec = new MriyaCodec();
	private RpcRequest request = new RpcRequest();


	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {

		log.debug("total::{}", in.remaining());

		try {
			if (!codec.decode(request, new IoBufferInputStream(in))) {
				return true;
			}
		} catch (Throwable e) {
			log.error("Decode exception.", e);
			request = new RpcRequest();
			request.setBean(Constants.EXCEPTION_METHOD);

			InputStream errorIn = new ByteArrayInputStream(
					"Input content error, decode exception.".getBytes());

			request.setInputStream(errorIn);
		}

		if (in.hasRemaining()) {
			log.debug("total remain::{}", in.remaining());
			// in.get(new byte[in.remaining()]);
			return true;
		}

		codec.clearStep();
		out.write(request);
		
		request = new RpcRequest();
		return false;
	}
}