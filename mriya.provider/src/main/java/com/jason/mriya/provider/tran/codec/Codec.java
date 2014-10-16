package com.jason.mriya.provider.tran.codec;

import com.jason.mriya.client.conn.io.IoBufferInputStream;
import com.jason.mriya.client.conn.io.IoBufferOutputStream;

public interface Codec {

	public IoBufferOutputStream encode(Object resp);

	public Object decode(IoBufferInputStream in);
}
