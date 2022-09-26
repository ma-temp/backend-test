# backend-test

### Requirements
* Java 17
* Maven 3.8
* Docker Desktop

### How to Run
* `./mvnw spring-boot:build-image`
* `docker run -p 8080:8080 -t ma-temp/backend-test`
* Access API with base path: `localhost:8080/api/v1/neo/`

### Considerations
* There are multiple ToDo comments for refactoring and adding more fields and tests.
* Integration tests.
* In this version Horizontal scalability was not a goal.
* There are rooms for improvement in areas like: Documentation, Logging, Error handling, etc.
* regarding IAC there wasn't enough time but its possible using AWS CloudFormation to create An ECS Cluster, TaskDefinition and a Service to deploy our docker image.