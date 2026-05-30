// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productcatalog.catalog.service.RedisService;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.util.ArrayList;
import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {
    private static final Logger LOGGER = LogManager.getLogger(RedisServiceImpl.class);
    public static final String COMPUTED_ITEMS = "computed-items-";
    public static final String PRODUCT_OFFERING = "product-offering-";

    public static final Integer BATCH_SIZE = 1000;
    @Resource
    private RedisTemplate<String, String> redisTemplate; // Store data as JSON strings

    /**
     * Deletes all Redis keys matching the given prefix (e.g., "computedItems:")
     * Uses SCAN + batch deletion for performance and safety.
     *
     * @param prefix the key prefix to match (e.g., "computedItems:")
     */
    @Override
    public void deleteProductOffering(String productOfferingId) {
        if (ObjectUtils.isEmpty(productOfferingId))
            return;
        String offeringkey = PRODUCT_OFFERING + productOfferingId;
        String prefix = COMPUTED_ITEMS;
        redisTemplate.delete(offeringkey);
        ScanOptions options = ScanOptions.scanOptions()
                .match(prefix + "*")
                .count(BATCH_SIZE)
                .build();

        List<String> batch = new ArrayList<>(BATCH_SIZE);
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            LOGGER.info("Starting deletion of keys with prefix: {}", prefix);

            cursor.forEachRemaining(key -> {
                if (key != null) {
                    batch.add(key);
                    // Flush batch when full
                    if (batch.size() == BATCH_SIZE) {
                        deleteBatch(batch, prefix);
                        batch.clear();
                    }
                }
            });
            // Delete remaining keys
            if (!batch.isEmpty()) {
                deleteBatch(batch, prefix);
            }

            LOGGER.info("Completed deletion of keys with prefix: {}", prefix);

        } catch (Exception e) {
            LOGGER.error("Failed to scan or delete keys with prefix '{}': {}", prefix, e.getMessage(), e);
            // Optional: rethrow or trigger alert
        }
    }

    @Override
    public void deleteProductOfferingPrice(String productOfferingPriceId) {
        if (ObjectUtils.isEmpty(productOfferingPriceId))
            return;
        String offeringPricekey = PRODUCT_OFFERING + productOfferingPriceId;
        try {
            redisTemplate.delete(offeringPricekey);
        }catch (Exception e){
            LOGGER.error("Failed to delete key '{}': {}", offeringPricekey, e.getMessage(), e);
        }
    }

    /**
     * Deletes a batch of keys in a single Redis DEL command.
     */
    private void deleteBatch(List<String> keys, String prefix) {
        try {
            Long deletedCount = redisTemplate.delete(keys);
            LOGGER.debug("Deleted {} keys in batch (prefix: {})", deletedCount, prefix);
        } catch (Exception ex) {
            LOGGER.warn("Failed to delete batch of {} keys (prefix: {}): {}",
                    keys.size(), prefix, ex.getMessage());
            // Optionally retry or log individual keys
        }
    }
}
