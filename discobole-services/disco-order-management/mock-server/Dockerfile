# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

FROM eclipse-temurin:17-jdk-alpine as builder

ARG MAVEN_REPOSITORY_URL="https://repo1.maven.org/maven2"

LABEL name="mock-server" \
      version="1.15.0" \
      description="order management mock-server Spring Boot application" \
      maintainer="aelhoseiny.ext@orange.com"

HEALTHCHECK --interval=15s --timeout=3s --retries=3 \
    CMD curl --silent --fail http://localhost:8080/actuator/health | grep "UP" || exit 1

WORKDIR /app
COPY target/om-mock-server*.jar ./app.jar
RUN wget -q ${MAVEN_REPOSITORY_URL}/io/opentelemetry/javaagent/opentelemetry-javaagent/1.24.0/opentelemetry-javaagent-1.24.0.jar \
    && mv -T ./opentelemetry-javaagent-*.jar opentelemetry-javaagent.jar

RUN addgroup java && adduser -D javauser java
USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]