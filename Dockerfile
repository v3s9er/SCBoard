FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon

EXPOSE 8080
CMD ["java", "-jar", "build/libs/scboard-0.0.1-SNAPSHOT.jar"]
