FROM openjdk:11-jdk
WORKDIR /app/stock
COPY src/main/resources/app.properties /app/stock/src/main/resources/app.properties
COPY target/jdbc*.jar /app/stock/lib/jdbc.jar
ARG SECRET_API_KEY
ENV SECRET_API_KEY=${SECRET_API_KEY}
ENV SERVER=stock-psql
CMD ["java", "-jar", "/app/stock/lib/jdbc.jar"]