package com.validator.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import com.validator.model.SignupRequest;

@Configuration
public class KafkaConsumerConfig {
	
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public ConsumerFactory<String, SignupRequest> consumerFactory() {
		JsonDeserializer<SignupRequest> deserializer = new JsonDeserializer<>(SignupRequest.class);
		deserializer.addTrustedPackages("*"); // Or your package like "com.validator.model"

		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "signup-validation-group");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, deserializer);
		props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);

		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
	}
	
//	@Bean
//	public ConcurrentKafkaListenerContainerFactory<String, SignupRequest> kafkaListenerContainerFactory() {
//	    ConcurrentKafkaListenerContainerFactory<String, SignupRequest> factory =
//	            new ConcurrentKafkaListenerContainerFactory<>();
//	    factory.setConsumerFactory(consumerFactory());
//
//	    // Set manual acknowledgment mode
//	    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
//	    
//	    return factory;
//	}
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, SignupRequest> kafkaListenerContainerFactory(
	        ConsumerFactory<String, SignupRequest> consumerFactory,
	        KafkaTemplate<String, SignupRequest> kafkaTemplate) {

	    ConcurrentKafkaListenerContainerFactory<String, SignupRequest> factory =
	            new ConcurrentKafkaListenerContainerFactory<>();

	    factory.setConsumerFactory(consumerFactory);

	    // DLQ handler setup
	    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
	            kafkaTemplate,
	            (record, ex) -> new TopicPartition("user.signup.dlq", record.partition())
	    );

	    // Retry once with 2-second interval
	    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 1));
	    factory.setCommonErrorHandler(errorHandler);

	    return factory;
	}



}
