// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.SETTING_CACHE;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.USER_ROLES_CACHE;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${app.cache.caffeine.spec.maximumSize}")
    private int maximumSize;

    @Value("${app.cache.caffeine.spec.expireAfterWrite}")
    private Duration expireAfterWrite;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(USER_ROLES_CACHE, SETTING_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(expireAfterWrite).maximumSize(maximumSize));
        return cacheManager;
    }

}

