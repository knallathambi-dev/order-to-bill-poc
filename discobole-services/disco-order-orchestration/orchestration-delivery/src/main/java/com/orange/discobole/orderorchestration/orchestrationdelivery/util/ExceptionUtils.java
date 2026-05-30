// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class ExceptionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtils.class);

    private ExceptionUtils() {
    }

    /**
     * An exception wrapper for functions that throw checked exceptions, designed to catch, log the exception, and handle failures gracefully.
     * @param throwingFunction
     * @param <T>
     * @param <R>
     */
    public static <T, R> Function<T, R> throwingFunctionWrapper(
            ThrowingFunction<T, R, Exception> throwingFunction) {
        return t -> {
            try {
                return throwingFunction.accept(t);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        };
    }
}
