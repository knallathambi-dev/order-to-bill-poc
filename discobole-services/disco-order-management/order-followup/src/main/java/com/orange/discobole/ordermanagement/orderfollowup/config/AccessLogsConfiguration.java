// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.config;

import ch.qos.logback.access.tomcat.LogbackValve;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.valves.AccessLogValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Set;


@Configuration
@Slf4j
public class AccessLogsConfiguration {
    private final Environment environment;

    public AccessLogsConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> accessLogsCustomizer() {
        return factory -> {
            LogbackValve defaultAccessValve = getLogbackAccessValve("logback-access.xml");
            factory.addContextValves(defaultAccessValve);
            factory.addContextCustomizers(this::customizeContext);
        };
    }

    private LogbackValve getLogbackAccessValve(String fileName) {
        LogbackValve logbackValve = new LogbackValve();
        logbackValve.setFilename(fileName);
        logbackValve.setAsyncSupported(true);
        return logbackValve;
    }

    private void customizeContext(Context context) {
        if (context.getPath().startsWith("/actuator")) {
            // Disable access logs for Actuator endpoints
            AccessLogValve accessLogValve = new AccessLogValve();
            accessLogValve.setPattern("dummy");
            accessLogValve.setDirectory("/dev/null");
            context.getParent().getPipeline().addValve(accessLogValve);
        }
        List<String> activeAppenders = getActiveAppenders();
        log.debug("Configuring access log appenders: {}", activeAppenders);
        activeAppenders.forEach(appender -> context.getParent().getPipeline().addValve(getLogbackAccessValve(String.format("logback-access-appender-%s.xml", appender))));
    }

    private List<String> getActiveAppenders() {
        List<String> logbackAccessAppender = List.of("jsonfile", "fluentd", "jsonconsole");
        Set<String> activeProfiles = Set.of(environment.getActiveProfiles());
        return logbackAccessAppender.stream()
                .filter(activeProfiles::contains)
                .toList();
    }
}