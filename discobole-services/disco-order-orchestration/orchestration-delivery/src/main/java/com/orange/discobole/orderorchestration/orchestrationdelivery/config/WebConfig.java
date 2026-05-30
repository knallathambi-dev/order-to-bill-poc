// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.config;

import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.converts.StringToOrchestrationPlanNodeStateEnumConverter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.converts.StringToOrchestrationPlanStateEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToOrchestrationPlanNodeStateEnumConverter());
        registry.addConverter(new StringToOrchestrationPlanStateEnumConverter());
    }
}
