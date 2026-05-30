// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service.impl;

import com.orange.discobole.productcatalog.policyrule.service.RedisService;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {
    private static final Logger LOGGER = LogManager.getLogger(RedisServiceImpl.class);
    @Resource
    private RedisTemplate<String, String> redisTemplate; // Store data as JSON strings

    /**
     * Deletes all Redis keys matching the given prefix (e.g., "computedItems:")
     * Uses SCAN + batch deletion for performance and safety.
     *
     * @param prefix the key prefix to match (e.g., "computedItems:")
     */
    @Override
    public void deletePolicyRule(List<String> keys) {
        try{
            if (!keys.isEmpty()) {
               // Single bulk delete call for all keys
                redisTemplate.delete(keys);
            }
        }catch (Exception e){
            LOGGER.error("Error deleting keys from Redis: {}", e.getMessage(), e);
        }
    }
}
