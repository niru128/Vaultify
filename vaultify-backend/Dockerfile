FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

COPY . .

# build the jar
RUN mvn clean package -DskipTests

# run the app
CMD ["java", "-jar", "target/*.jar"]
