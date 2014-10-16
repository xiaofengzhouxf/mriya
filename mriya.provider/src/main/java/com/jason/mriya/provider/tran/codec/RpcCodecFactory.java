package com.jason.mriya.provider.tran.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 
 * <pre>
 * <p>文件名称: RpcCodecFactory.java</p>
 * 
 * <p>文件功能:</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月17日 下午2:44:42</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class RpcCodecFactory implements ProtocolCodecFactory {

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new RpcDecoder();
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new RpcEncoder();
	}

}
