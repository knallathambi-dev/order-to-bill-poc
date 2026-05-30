// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.config;

import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Autowired
    private transient MeterRegistry registry;

    @Bean
    public MeterBinder processMemoryMetrics() {
        MeterBinder processMemoryMetrics =  new ProcessMemoryMetrics();
        processMemoryMetrics.bindTo(registry);
        setRegistryConfig();
        return processMemoryMetrics;
    }

    @Bean
    public MeterBinder processThreadMetrics() {
        MeterBinder processThreadMetrics =  new ProcessThreadMetrics();
        processThreadMetrics.bindTo(registry);
        setRegistryConfig();
        return processThreadMetrics;
    }

    private void setRegistryConfig() {
        registry.config()
                .commonTags("application", "cood");

    }
}


