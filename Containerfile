FROM docker.io/library/maven:3.9.6-eclipse-temurin-21-alpine as builder

COPY pom.xml lockfile.json ./
RUN mvn io.github.chains-project:maven-lockfile:validate
RUN mvn io.github.chains-project:maven-lockfile:freeze
RUN mvn dependency:go-offline -B

COPY ./ ./
RUN mvn package -DskipTests=true

FROM docker.io/library/eclipse-temurin:21-jre-alpine

WORKDIR /backend
COPY --from=builder target/therealone.jar /backend/therealone.jar
ENTRYPOINT ["java","-jar","/backend/therealone.jar"]
