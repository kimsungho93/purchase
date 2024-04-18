FROM openjdk:17-alpine

CMD ["./gradlew", "clean", "build"]

VOLUME /app

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} purchase.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "purchase.jar"]

