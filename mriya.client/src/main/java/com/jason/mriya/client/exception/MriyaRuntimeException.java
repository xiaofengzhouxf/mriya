package com.jason.mriya.client.exception;

public class MriyaRuntimeException extends RuntimeException {

	public MriyaRuntimeException() {
		super();
	}

	public MriyaRuntimeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MriyaRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MriyaRuntimeException(String message) {
		super(message);
	}

	public MriyaRuntimeException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 1231231231231211L;

}
