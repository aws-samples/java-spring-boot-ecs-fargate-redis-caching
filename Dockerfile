FROM maven:3.6.3-amazoncorretto-15 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM amazoncorretto:15-alpine
COPY --from=build /usr/src/app/target/Spring-Boot-With-Redis-Caching*.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]
