# mortgage-service
mortgage-service is a Java-based backend REST service that will help customers to check the interest rates and also validate if they are eligible to get a mortgage against their dream house.

## You can:
- Access Swagger UI at `http://localhost:8080/swagger-ui.html`
- Access Actuator endpoints at `http://localhost:8080/actuator`
- Generate JaCoCo coverage reports by running `mvn test` (reports will be in `target/site/jacoco/`)

## Example: GET lists of interest rates
    curl -X GET http://localhost:8081/v1/api/interest-rates \  -H "Accept: application/json"



# Version: 0.0.1-SNAPSHOT
- Added GET API to get a list of current interest rates
- Added POST API to post the parameters to calculate for a mortgage check
- Added Authentication with JWT and OAUTH when calculation mortgage eligibility
- Added Logging
- Added Unit Testing
- Added Jacoco for a code coverage report
- Added DockerFile to create a docker image
- Added OpenApi Documentation for both GET and POST APIs 
- Added Actuator for monitoring features like health checks and metrics

