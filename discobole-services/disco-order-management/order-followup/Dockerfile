# SPDX-FileCopyrightText: 2025 - 2026 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

FROM eclipse-temurin:17-jdk-alpine as builder

ARG MAVEN_REPOSITORY_URL="https://repo1.maven.org/maven2"

LABEL name="discobole-ordermanagement-followup" \
      version="1.12.0" \
      description="ODA - OM - Order Follow-up Service" \
      maintainer="aelhoseiny.ext@orange.com"

HEALTHCHECK --interval=15s --timeout=3s --retries=3 CMD curl --silent --fail http://localhost:9000/actuator/health | grep "UP" || exit 1

WORKDIR /app
COPY  target/discobole-ordermanagement-followup*-exec.jar ./
RUN mv -T ./discobole-ordermanagement-followup*-exec.jar app.jar \
      && wget -q ${MAVEN_REPOSITORY_URL}/io/opentelemetry/javaagent/opentelemetry-javaagent/1.24.0/opentelemetry-javaagent-1.24.0.jar \
      && mv -T ./opentelemetry-javaagent-*.jar opentelemetry-javaagent.jar

RUN addgroup java && adduser -D javauser java
USER javauser
EXPOSE 8080 9000

ENTRYPOINT ["java","-jar","app.jar"]