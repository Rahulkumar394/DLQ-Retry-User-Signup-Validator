package com.validator.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.validator.model.SignupRequest;

@Service
@Validated
public class SignupValidatorService {

	public void validate(SignupRequest request) {
		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}
	}
}
