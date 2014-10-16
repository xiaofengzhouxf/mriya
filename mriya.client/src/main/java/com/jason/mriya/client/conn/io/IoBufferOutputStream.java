package com.jason.mriya.client.conn.io;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 
 * <pre>
 * <p>文件名称: IoBufferOutputStream.java</p>
 * 
 * <p>文件功能: </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月12日 上午10:01:25</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class IoBufferOutputStream extends OutputStream {
	private static final int DEFAULT_SIZE = 1024;
	private IoBuffer outputBuffer;

	public IoBufferOutputStream() {
		this(DEFAULT_SIZE);
	}

	public IoBufferOutputStream(int initBufferSize) {
		outputBuffer = IoBuffer.allocate(initBufferSize);
		outputBuffer.setAutoExpand(true);
	}

	public IoBufferOutputStream(IoBuffer outputBuffer) {
		this.outputBuffer = outputBuffer;
	}

	@Override
	public void write(int b) throws IOException {
		outputBuffer.put((byte) b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		outputBuffer.put(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		outputBuffer.put(b);
	}

	public IoBuffer flip() {
		outputBuffer.flip();
		return outputBuffer;
	}
}