// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.config;

import com.orange.discobole.ordermanagement.orderinventory.interceptor.EmptyQueryParamValidationInterceptor;
import com.orange.discobole.ordermanagement.orderinventory.interceptor.LogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final LogInterceptor logInterceptor;
    private final EmptyQueryParamValidationInterceptor emptyQueryParamValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor).addPathPatterns("/**").excludePathPatterns("/actuator", "/actuator/**");
        registry.addInterceptor(emptyQueryParamValidationInterceptor)
                .addPathPatterns(Constants.PRODUCT_ORDERING_MANAGEMENT_BASE_URL + "/**");
    }
}