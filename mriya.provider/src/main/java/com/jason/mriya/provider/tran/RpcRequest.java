package com.jason.mriya.provider.tran;

import java.io.InputStream;

/**
 * 
 * <pre>
 * <p>文件名称: RpcRequest.java</p>
 * 
 * <p>文件功能: RPC请求参数</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月12日 上午10:04:46</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class RpcRequest extends BaseMessage {

	private static final long serialVersionUID = 1008702087453594305L;

	private InputStream inputStream;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

}
