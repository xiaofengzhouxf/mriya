package com.jason.mriya.provider.tran;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 
 * <pre>
 * <p>文件名称: RpcResponse.java</p>
 * 
 * <p>文件功能: 请求响应信息</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月12日 上午10:06:53</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class RpcResponse extends BaseMessage {
	private static final long serialVersionUID = -857913694968228926L;
	private IoBuffer outputBuffer;
	private String method;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public IoBuffer getOutputBuffer() {
		return outputBuffer;
	}

	public void setOutputBuffer(IoBuffer outputBuffer) {
		this.outputBuffer = outputBuffer;
	}
}
