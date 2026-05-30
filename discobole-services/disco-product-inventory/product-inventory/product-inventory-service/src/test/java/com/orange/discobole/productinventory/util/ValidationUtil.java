// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import static com.orange.discobole.productinventory.exception.model.BusinessErrors.PATCH_METHOD_NOT_SUPPORTED_BY_THAT_RESOURCE;

public class ValidationUtil {

    public static String getNotNullMessage(String field) {
        return getMessageWithField(field, "must not be null");
    }

    public static String getNotEmptyMessage(String field) {
        return getMessageWithField(field, "must not be empty");
    }

    private static String getMessageWithField(String field, String message) {
        return "%s %s".formatted(field, message);
    }

    public static String getUnpatchableErrorMessageForField(String field) {
        return String.format(PATCH_METHOD_NOT_SUPPORTED_BY_THAT_RESOURCE, field);
    }
}
