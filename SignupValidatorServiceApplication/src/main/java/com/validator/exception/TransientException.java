package com.validator.exception;

public class TransientException extends RuntimeException {
	private static final long serialVersionUID = 4911369768874109312L;

	public TransientException(String message) {
		super(message);
	}
}
