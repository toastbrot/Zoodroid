package com.unicorntoast.android.zoodroid.exception;

public class ZoodroidException extends RuntimeException {
	private static final long serialVersionUID = -4453470978407759884L;
	
	private int msgResourceId;
	private Exception originalException;
	private String more;
	
	public ZoodroidException(int msgResourceId, Exception originalException, String more) {
		this.msgResourceId = msgResourceId;
		this.originalException = originalException;
		this.more = more;
	}
	
	public ZoodroidException(int msgResourceId, Exception originalException) {
		this(msgResourceId, originalException, null);
	}

	public int getMsgResourceId() {
		return msgResourceId;
	}

	public Exception getOriginalException() {
		return originalException;
	}

	public String getMore() {
		return more;
	}
	
}
