package com.bccriskadvisory.link.connector;

public class EdgescanConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	public EdgescanConnectionException() {
		super();
	}

	public EdgescanConnectionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EdgescanConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public EdgescanConnectionException(String message) {
		super(message);
	}

	public EdgescanConnectionException(Throwable cause) {
		super(cause);
	}
}
