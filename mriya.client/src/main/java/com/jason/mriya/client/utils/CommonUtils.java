package com.jason.mriya.client.utils;

import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.SerializationUtils;

/**
 * 
 * <pre>
 * 
 * <p>文件功能: </p>
 * 
 * <p>编程者: xiaofeng.zhou</p>
 * 
 * <p>初作时间: 2014年9月15日 上午11:36:19</p>
 * 
 * <p>版本: version 1.0 </p>
 * </pre>
 */
public class CommonUtils {

	public static Object deserial(InputStream in) {

		Object deserialize = SerializationUtils.deserialize(in);

		return deserialize;
	}

	public static byte[] serialize(Serializable obj) {

		return SerializationUtils.serialize(obj);
	}

	public static byte[] intToBytesLitterend(int value) {
		byte[] src = new byte[4];
		src[3] = (byte) ((value >> 24) & 0xFF);
		src[2] = (byte) ((value >> 16) & 0xFF);
		src[1] = (byte) ((value >> 8) & 0xFF);
		src[0] = (byte) (value & 0xFF);
		return src;
	}

	public static byte[] intToBytesBigend(int value) {
		byte[] src = new byte[4];
		src[0] = (byte) ((value >> 24) & 0xFF);
		src[1] = (byte) ((value >> 16) & 0xFF);
		src[2] = (byte) ((value >> 8) & 0xFF);
		src[3] = (byte) (value & 0xFF);
		return src;
	}

	public static int byteToIntLitterend(byte[] bytes) {
		int length = 4;
		int intValue = 0;
		for (int i = length - 1; i >= 0; i--) {
			int offset = i * 8;
			intValue |= (bytes[i] & 0xFF) << offset;
		}
		return intValue;
	}

	public static int byteToIntBigend(byte[] bytes) {
		int length = 4;
		int intValue = 0;
		for (int i = 0; i < length; i++) {
			int offset = (length - i - 1) * 8;
			intValue |= (bytes[i] & 0xFF) << offset;
		}
		return intValue;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		byte[] ss = new byte[] { 0, 0, 0, 78 };

		System.out.println(CommonUtils.byteToIntBigend(ss));

	}
}
