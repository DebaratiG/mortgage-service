# mortgage-service
mortgage-service is a Java-based backend REST service that will help customers to check the interest rates and also validate if they are eligible to get a mortgage against their dream house.

## You can:
- Access Swagger UI at `http://localhost:8081/swagger-ui.html`
- Access Actuator endpoints at `http://localhost:8081/actuator`
- Access H2-Console at `http://localhost:8081/h2-console`
- Generate JaCoCo coverage reports by running `mvn clean test` and `mvn test jacoco:report` (reports will be in `target/site/jacoco/`)

## Example: GET lists of interest rates
    curl -X GET "http://localhost:8081/api/v1/interest-rates" -H "Accept: application/json"

## Example: POST mortgage check
    curl -X POST "http://localhost:8081/api/v1/mortgage-check" -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"income\":1000,\"maturityPeriod\":84,\"loanValue\":4000,\"homeValue\":4000}"

# Version: 1.0.0
- Added H2-Console to access the database
- Added GET API to get a list of current interest rates
- Added POST API to post the parameters to calculate for a mortgage check
- Added Logging
- Added Unit Testing
- Added Jacoco for a code coverage report
- Added DockerFile to create a docker image
- Added OpenApi Documentation for both GET and POST APIs 
- Added Actuator for monitoring features like health checks and metrics
- Added Swagger UI for documentation
- Added Spring Boot Actuator

