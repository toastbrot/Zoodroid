package com.unicorntoast.android.zoodroid.exception;

public class ZoodroidException extends RuntimeException {
	private static final long serialVersionUID = -4453470978407759884L;
	
	private int msgResourceId;
	private Exception originalException;
	
	public ZoodroidException(int msgResourceId, Exception originalException) {
		this.msgResourceId = msgResourceId;
		this.originalException = originalException;
	}

	public int getMsgResourceId() {
		return msgResourceId;
	}

	public Exception getOriginalException() {
		return originalException;
	}
	
}
