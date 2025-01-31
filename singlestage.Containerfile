FROM docker.io/library/eclipse-temurin:23-jre-alpine

WORKDIR /backend
COPY target/therealone.jar /backend/therealone.jar
ENTRYPOINT ["java","-jar","/backend/therealone.jar"]
