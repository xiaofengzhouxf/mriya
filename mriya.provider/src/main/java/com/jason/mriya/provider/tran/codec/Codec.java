package com.jason.mriya.provider.tran.codec;

import com.jason.mriya.client.conn.io.IoBufferInputStream;
import com.jason.mriya.client.conn.io.IoBufferOutputStream;

public interface Codec<R, P> {

	public IoBufferOutputStream encode(P resp);

	public boolean decode(R req, IoBufferInputStream in);
}
