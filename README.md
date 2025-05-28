DLQ & Retry — User Signup Validator
Project Name: SignupValidatorService
Goal:
Validate signup data. On failure, retry once then route to user.signup.dlq.

Tech Stack:
Java 17
Spring Boot
Spring Kafka
Kafka Retry Template or Manual Retry Logic

How It Works:
Message fails (e.g., email missing) → retry → DLQ if still failing
Rules to Follow:
Kafka-Specific:

Use separate DLQ topic
Backoff retry config
Code Quality:

Centralized error classification
Retry only transient errors
Testing:

Unit: Retry logic
Negative: Permanent error → assert DLQ push
Integration: Test both retry paths
