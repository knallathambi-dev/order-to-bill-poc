# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

# Normal startup: 20/25sec, optimized startup: 11 sec on 4CPU
FROM eclipse-temurin:17-jdk-ubi9-minimal AS builder

ARG MAVEN_REPOSITORY_URL="https://repo1.maven.org/maven2"

HEALTHCHECK --interval=15s --timeout=3s --retries=3 CMD curl --silent --fail http://localhost:8080/actuator/health | grep "UP" || exit 1

WORKDIR /app
COPY target/discobole-orderorchestration-orchestration-delivery-fallout*-exec.jar ./
RUN mv -T ./discobole-orderorchestration-orchestration-delivery-fallout-*-exec.jar app.jar \
      && wget -q ${MAVEN_REPOSITORY_URL}/io/opentelemetry/javaagent/opentelemetry-javaagent/1.24.0/opentelemetry-javaagent-1.24.0.jar \
      && mv -T ./opentelemetry-javaagent-*.jar opentelemetry-javaagent.jar

# Information sur l'image Docker
LABEL name="orchestration-delivery-fallout" \
      version="2.0.3" \
      description="orchestration-delivery-fallout Spring Boot application" \
      maintainer="aelhoseiny.ext@orange.com"

# make our image running with non root user, files are read-only
RUN groupadd java && useradd -r -g java javauser
# hadolint ignore=DL3002
USER root
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker,console
ENV APP_CLASS="com.orange.discobole.orderorchestration.orchestrationdelivery.OrchestrationFalloutApplication"

ENTRYPOINT ["java","-jar","app.jar"]

# Démarrage de l'application fait dans l'image de base.

