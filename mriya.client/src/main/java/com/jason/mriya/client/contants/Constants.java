package com.jason.mriya.client.contants;

public class Constants {
	public static final String NAME_CHARSET = "ASCII";
	public static final String EXCEPTION_METHOD = "mriya_inner_error";
	public static final String HEART_BEAT = "heart_beat";

	public static final byte HEART_BEAT_REQ = (byte) (2 << 7);

	public static final byte HEART_BEAT_RSP = (byte) (2 << 7 - 1);
}
