# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

FROM eclipse-temurin:17-jdk-alpine
# hadolint ignore=DL3018
RUN apk add --no-cache curl
ARG MAVEN_REPOSITORY_URL="https://repo1.maven.org/maven2"


LABEL name="event-service" \
      description="Disco event service" \
      maintainer="dhruv.verma@orange.com"

WORKDIR /app

COPY ./target/event-service.jar /app/
RUN mv -T ./event-service.jar app.jar

RUN wget -q ${MAVEN_REPOSITORY_URL}/io/opentelemetry/javaagent/opentelemetry-javaagent/1.24.0/opentelemetry-javaagent-1.24.0.jar \
    && mv -T ./opentelemetry-javaagent-*.jar opentelemetry-javaagent.jar \
    && ls -lh && pwd

RUN addgroup java && adduser -D javauser java
EXPOSE 8080 9000

USER javauser
CMD ["java", "-jar", "app.jar"]