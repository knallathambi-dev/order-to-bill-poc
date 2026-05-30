// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Slf4j
public class MiscUtil {

    private MiscUtil() {
        throw new IllegalStateException();
    }

    public static <V> CompletableFuture<List<V>> getCompletableFutureList(List<CompletableFuture<V>> futures) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Collection<V>>[] arrayFuture = futures.toArray(new CompletableFuture[0]);
        return CompletableFuture
                .allOf(arrayFuture)
                .thenApply(v -> futures
                        .stream()
                        .map(CompletableFuture::join)
                        .toList());
    }
    public static String getStackTraceAsString(Throwable throwable, int limit) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (int i = 0; i < Math.min(limit, stackTrace.length); i++) {
            sb.append(stackTrace[i].toString()).append("\n");
        }
        if (stackTrace.length > limit) {
            sb.append("... (").append(stackTrace.length - limit).append(" more)");
        }
        return sb.toString();
    }


}