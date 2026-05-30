// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.orderfollowup.constant.ExceptionMessage;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MiscUtil {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    private MiscUtil() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }

    public static <T> T convertInstanceOfObject(Object object, Class<T> clazz) {
        try {
            String objectValueStr = objectMapper.writeValueAsString(object);
            return objectMapper.readValue(objectValueStr, clazz);
        } catch (Exception e) {
            log.error("Unable to get Object Value [{}]:", e.getMessage(), e);
            throw new InvalidParameterException(ExceptionMessage.INVALID_INPUT);
        }
    }
}