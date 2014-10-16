package com.jason.mriya.client.conn.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 
 * <pre>
 * <p>文件名称: IoBufferInputStream.java</p>
 * 
 * <p>文件功能: </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月12日 上午10:01:03</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class IoBufferInputStream extends InputStream {
	private IoBuffer buffer;

	public IoBufferInputStream(IoBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public int read() throws IOException {
		return buffer.get();
	}

	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	};

	public int read(byte[] b, int off, int len) throws IOException {
		len = Math.min(len, buffer.remaining());
		len = Math.min(len, b.length);
		buffer.get(b, off, len);
		return len;
	};

	public IoBuffer flip() {
		buffer.flip();
		return buffer;
	}

	public IoBuffer getBuffer() {
		return buffer;
	}
}