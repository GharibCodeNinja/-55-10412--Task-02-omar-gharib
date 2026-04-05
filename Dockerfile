FROM eclipse-temurin:25.0.2_10-jdk
WORKDIR /app
COPY target/*.jar app.jar
ENV USER_NAME=Omar_Ghareeb
ENV ID=55-10412
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
