FROM openjdk:8-alpine

COPY target/uberjar/todoapp.jar /todoapp/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/todoapp/app.jar"]
