FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

COPY . .

# build the jar
RUN mvn clean package -DskipTests

# copy jar to a fixed name
RUN cp target/*.jar app.jar

# run the app
CMD ["java", "-jar", "app.jar","--spring.config.location=classpath:/application.properties"]