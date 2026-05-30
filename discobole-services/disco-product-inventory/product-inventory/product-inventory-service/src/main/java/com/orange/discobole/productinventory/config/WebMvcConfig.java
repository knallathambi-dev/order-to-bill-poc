// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config;

import com.orange.discobole.productinventory.interceptor.EmptyQueryParamValidationInterceptor;
import com.orange.discobole.productinventory.interceptor.LogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import static com.orange.discobole.productinventory.constant.Constant.PRODUCT_INVENTORY_MANAGEMENT_BASE_URL;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;
    private final EmptyQueryParamValidationInterceptor emptyQueryParamValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator", "/actuator/**");
        registry.addInterceptor(emptyQueryParamValidationInterceptor)
                .addPathPatterns(PRODUCT_INVENTORY_MANAGEMENT_BASE_URL + "/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/async-api/**")
                .addResourceLocations("classpath:/public/async-api/");
        registry.addResourceHandler("/api-docs/**")
                .addResourceLocations("classpath:/static/api-docs/");
    }

   }
