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


======================================
    STEP TO HOW I TEST ABOVE TASK
======================================

Step 1: Start System with Docker Compose
Run this from your project root (where docker-compose.yml is):

docker-compose up -d --build
✅ This will:

Build your Spring Boot app

Start Kafka (confluentinc/cp-kafka)

Start Zookeeper

Start your SignupValidatorService

🧾 Step 2: Check Running Containers

docker ps
✅ You should see:

signupvalidatorserviceapplication-signup-validator-service-1

signupvalidatorserviceapplication-kafka-1

signupvalidatorserviceapplication-zookeeper-1

📜 Step 3: Confirm Kafka Topics
Enter the Kafka container:


docker exec -it signupvalidatorserviceapplication-kafka-1 bash
Then list topics:

kafka-topics --bootstrap-server localhost:9092 --list
✅ You should see:

user.signup

user.signup.dlq

✉️ Step 4: Produce Messages to Kafka
Use Kafka CLI to send messages to user.signup:


kafka-console-producer --broker-list localhost:9092 --topic user.signup
✅ Test Case: Valid Data
Paste this:


{"email": "valid@example.com", "username": "validUser"}
❌ Test Case: Permanent Error (Missing Email)
Paste this:


{"username": "noEmailUser"}
🧭 Step 5: Observe Retry/DLQ Behavior
In a new terminal, view Spring Boot logs:


docker logs -f signupvalidatorserviceapplication-signup-validator-service-1
✅ What to check:

For valid message: Processed successfully

For missing email: ValidationException → sending to DLQ

For transient error: Retrying..., then DLQ if still fails

📦 Step 6: Consume Messages from DLQ
In Kafka container:

kafka-console-consumer --bootstrap-server localhost:9092 --topic user.signup.dlq --from-beginning
