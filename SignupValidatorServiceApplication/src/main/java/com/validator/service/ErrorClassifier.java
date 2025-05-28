package com.validator.service;

import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;

@Component
public class ErrorClassifier {
    public boolean isRetryable(Throwable ex) {
        return ex instanceof TransientDataAccessException;
    }
}
