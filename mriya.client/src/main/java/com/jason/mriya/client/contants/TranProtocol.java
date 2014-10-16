package com.jason.mriya.client.contants;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <pre>
 * <p>文件名称: TransportProtocol.java</p>
 * 
 * <p>文件功能: 协议</p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月12日 上午9:52:36</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public enum TranProtocol {
	HESSIAN((byte) 100, 80), HTTP((byte) 200, 80), NULL((byte) 0, 0);

	private byte code;
	private int port;

	private TranProtocol(byte code, int port) {
		this.code = code;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public byte getCode() {
		return code;
	}

	static Map<Byte, TranProtocol> map = new HashMap<Byte, TranProtocol>(1);
	static {
		TranProtocol[] values = TranProtocol.values();

		for (TranProtocol tran : values) {
			map.put(tran.getCode(), tran);
		}
	}

	public static TranProtocol convert(byte code) {
		if (map.containsKey(code)) {
			return map.get(code);
		} else {
			return NULL;
		}
	}

}
