FROM openjdk:8
ADD target/kafka-stream-0.0.1-SNAPSHOT.jar kafka-stream-0.0.1-SNAPSHOT.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "kafka-stream-0.0.1-SNAPSHOT.jar"]
