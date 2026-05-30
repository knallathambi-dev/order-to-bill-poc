// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.config.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.orange.discobole.productinventory.config.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class CacheConfig {

    private final AppConfig appConfig;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("catalogCache", "resourceInventoryCache", "userAuthorizationCache");
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(appConfig.getExpireAfterWrite()).maximumSize(appConfig.getMaximumSize()));
        return cacheManager;
    }
}

