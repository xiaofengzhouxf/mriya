package com.jason.mriya.provider.tran.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.jason.mriya.client.conn.io.IoBufferInputStream;
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
	public MriyaCodec codec = new MriyaCodec();

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		RpcRequest request = (RpcRequest) codec.decode(new IoBufferInputStream(
				in));

		out.write(request);
		return true;
	}
}
