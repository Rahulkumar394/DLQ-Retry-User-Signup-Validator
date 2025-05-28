package com.validator.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.validator.model.SignupRequest;
import com.validator.service.SignupValidatorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SignupListener {

	private final SignupValidatorService validatorService;

	public SignupListener(SignupValidatorService validatorService) {
		this.validatorService = validatorService;
	}

//	@KafkaListener(topics = "user.signup", groupId = "signup-validation-group", containerFactory = "kafkaListenerContainerFactory")
//	public void listen(SignupRequest request, Acknowledgment ack) {
//		try {
//			log.info("Received signup request: {}", request);
//			validatorService.validate(request);
//			ack.acknowledge(); // Commit offset manually after successful processing
//			log.info("Signup request processed and acknowledged.");
//		} catch (Exception e) {
//			log.error("Error processing signup request: {}", request, e);
//			throw e; // This will trigger retry or DLQ based on config
//		}
//	}
	
	@KafkaListener(topics = "user.signup", groupId = "signup-validation-group")
	public void listen(SignupRequest request, Acknowledgment acknowledgment) {
	    try {
	        System.out.println("Received: " + request);
	        log.info("Received signup request: {}", request);
	        validatorService.validate(request);
	        acknowledgment.acknowledge(); // Acknowledge manually
	        log.info("Signup request processed and acknowledged.");
	    } catch (Exception e) {
	        // Retry / DLQ logic here
	    	log.error("Error processing signup request: {}", request, e);
	        System.err.println("Validation failed: " + e.getMessage());
	    }
	}

}
