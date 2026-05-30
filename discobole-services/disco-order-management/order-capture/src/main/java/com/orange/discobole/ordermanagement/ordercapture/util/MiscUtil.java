// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.processflow.dto.generated.ChannelRef;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class MiscUtil {
    public static final ObjectMapper mapper = new ObjectMapper();

    private MiscUtil() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }

    public static String getFirstChannelId(List<ChannelRef> channelRefList) {
        return CollectionUtils.isEmpty(channelRefList) ? null : channelRefList.get(0).getId();
    }

    public static <T> T convertInstanceOfObject(Object object, Class<T> clazz) {
        try {
            String objectValueStr = mapper.writeValueAsString(object);
            return mapper.readValue(objectValueStr, clazz);
        } catch (Exception e) {
            log.error("Unable to get Object Value [{}]:", e.getMessage(), e);
            throw new InvalidParameterException(ExceptionMessage.INVALID_INPUT);
        }
    }

    public static <V> List<V> getCompletableFutureList(List<CompletableFuture<V>> futures) throws ExecutionException, InterruptedException {
        @SuppressWarnings("unchecked")
        CompletableFuture<Collection<V>>[] arrayFuture = futures.toArray(new CompletableFuture[0]);
        return CompletableFuture
                .allOf(arrayFuture)
                .thenApply(v -> futures
                        .stream()
                        .map(CompletableFuture::join)
                        .toList())
                .exceptionally(ex -> {
                    throw new DiscoException("Error getting result", ex);
                }).get();
    }

    public static <T> boolean isMapEmptyOrContainsNull(Map<String, List<T>> map) {
        if (map == null || map.isEmpty()) {
            return true;
        }
        for (List<T> items : map.values()) {
            if (items == null || items.isEmpty()) {
                return true;
            }
            for (T item : items) {
                if (item == null) {
                    return true;
                }
            }
        }
        return false;
    }
}