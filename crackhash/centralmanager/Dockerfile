FROM openjdk:11-jdk-slim AS build
WORKDIR /centralmanager
COPY . /centralmanager
RUN ./gradlew bootJar

WORKDIR /
ENTRYPOINT ["java","-jar","/centralmanager/build/libs/centralmanager.jar"]
