# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

# Normal startup: 20/25sec, optimized startup: 11 sec on 4CPU
FROM eclipse-temurin:17-jdk-alpine as builder

ARG JAR_FILE

WORKDIR /app
COPY ${JAR_FILE} ./
RUN mv -T ./orchestration-delivery-commons-*.jar app.jar

# make our image running with non root user, files are read-only
RUN addgroup java && adduser -D javauser java
USER javauser
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker,console
ENV APP_CLASS="com.orange.orchestration_delivery_commons.OrchestrationDelieveryCommonsApplication"

ENTRYPOINT ["java","-jar","app.jar"]


