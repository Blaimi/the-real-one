FROM docker.io/library/maven:3.9.5-eclipse-temurin-21-alpine as builder
COPY ./ ./
RUN mvn package -DskipTests=true

FROM docker.io/library/eclipse-temurin:21-jre-alpine
RUN mkdir -p /backend
COPY --from=builder target/therealone.jar /backend/therealone.jar
ENTRYPOINT ["java","-jar","/backend/therealone.jar"]
