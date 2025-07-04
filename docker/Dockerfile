FROM openjdk:21
MAINTAINER DVO
RUN javac EventBookingApplication.java
COPY target/EventBooking-0.0.1-SNAPSHOT-plain.jar /app/app.jar
WORKDIR /app
CMD ["java", "EventBookingApplication", "-jar", "app.jar"]