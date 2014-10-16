package com.jason.mriya.start;

import org.apache.mina.core.buffer.IoBuffer;

import com.jason.mriya.client.HelloWorld;
import com.jason.mriya.client.exception.MriyaRuntimeException;
import com.jason.mriya.client.utils.CommonUtils;

public class HelloWorldImpl implements HelloWorld {

	@Override
	public void hello(String msg) {

		// return "hello world." + msg;

//		throw new MriyaRuntimeException("test");
		
		throw new  NullPointerException("asdfad");
	}

	public static void main(String[] args) throws ClassNotFoundException {

		byte[] serialize = CommonUtils.serialize(new MriyaRuntimeException(
				"Remote exception."));
		IoBuffer iobuffer = IoBuffer.wrap(serialize);
		iobuffer.flip();
		System.out.println(iobuffer.array().length);

		Class<?> forName = Class.forName("com.cmcc.mriya.client.HelloWorld");

		System.out.println(forName);

	}
}
