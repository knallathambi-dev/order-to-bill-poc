FROM eclipse-temurin:17-jre
WORKDIR /app
RUN groupadd java && useradd -r -g java javauser
COPY target/billing-service-*.jar /app/app.jar
USER javauser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
