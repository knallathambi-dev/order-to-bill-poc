// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.config;


import jakarta.annotation.Resource;
import org.apache.catalina.Context;
import org.apache.catalina.valves.AccessLogValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import ch.qos.logback.access.tomcat.LogbackValve;

@Configuration
public class AccessLogsConfiguration {

    @Resource
    private Environment environment;

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
        if (isJsonFileProfile()) {
            context.getParent().getPipeline().addValve(getLogbackAccessValve("logback-access-json-file.xml"));
        }
    }

    private boolean isJsonFileProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("jsonfile".equals(profile)) {
                return true;
            }
        }
        return false;
    }

}