package com.validator.exception;

public class PermanentException extends RuntimeException {

	private static final long serialVersionUID = 2007906292650389788L;

	public PermanentException(String message) {
		super(message);
	}
}