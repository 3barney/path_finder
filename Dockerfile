FROM adoptopenjdk/openjdk11:alpine-slim
EXPOSE 9090
ADD target/routing_test.jar routing_test.jar
ENTRYPOINT ["java", "-jar", "routing_test.jar"]
