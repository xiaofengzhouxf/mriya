package com.jason.mriya.provider.tran.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.jason.mriya.client.conn.io.IoBufferOutputStream;
import com.jason.mriya.provider.tran.RpcResponse;

/**
 * 
 * <pre>
 * <p>文件名称: RpcEncoder.java</p>
 * 
 * <p>文件功能: </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月17日 下午2:45:17</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class RpcEncoder implements ProtocolEncoder {
	public MriyaCodec codec = new MriyaCodec();

	public void dispose(IoSession session) throws Exception {

	}

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {

		if (message instanceof RpcResponse) {
			RpcResponse resp = (RpcResponse) message;
			out.write(codec.encode(resp).flip());
		} else {
			IoBuffer outputBuffer = IoBuffer.allocate(1024);
			outputBuffer.setAutoExpand(true);
			outputBuffer.put(message.toString().getBytes());
			out.write(outputBuffer.flip());
		}
	}
}