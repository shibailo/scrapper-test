FROM maven:3.9.6-eclipse-temurin-17 AS tests

WORKDIR /workspace

# Copy the full source tree.
COPY . .
RUN mvn -pl domain,api,jsonwikiclient,htmlwikiclient,persistence -am install -DskipTests

ENV SPRING_PROFILES_ACTIVE=html

# CMD ["mvn", "-B", "test"]
CMD ["mvn", "-pl", "app", "spring-boot:run"]
