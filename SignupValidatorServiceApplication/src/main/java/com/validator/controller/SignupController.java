package com.validator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.validator.model.SignupRequest;

@RestController
@RequestMapping("/signup")
public class SignupController {

	 private final KafkaTemplate<String, SignupRequest> kafkaTemplate;

	    public SignupController(KafkaTemplate<String, SignupRequest> kafkaTemplate) {
	        this.kafkaTemplate = kafkaTemplate;
	    }

	    @PostMapping("/publish")
	    public ResponseEntity<String> sendMessage(@RequestBody SignupRequest signupRequest) {
	        kafkaTemplate.send("user.signup", signupRequest);
	        return ResponseEntity.ok("Message sent to Kafka topic user.signup");
	    }
}
