// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.utils;

import com.orange.discobole.orderorchestration.exception.model.CoodHttpFailedException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;

import java.time.Instant;

public class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static <T extends CoodHttpFailedException> CoodRecoverableAndNonRetryableException createCoodDLTAndNonRetryableException(int statusCode, String url,
                                                                                                                                    String message, T throwableException) {
        CoodRecoverableAndNonRetryableException exception;
        switch (statusCode) {
            case 204 -> {
                throwableException.setCoodError(new CoodError(ExceptionCode.HTTP_NO_CONTENT.getCode(), ExceptionCode.HTTP_NO_CONTENT.getMessagePattern().formatted(url, message), ExceptionCode.HTTP_NO_CONTENT.getReason(), Instant.now()));
                exception = new CoodRecoverableAndNonRetryableException(throwableException);
            }
            case 400 -> {
                throwableException.setCoodError(new CoodError(ExceptionCode.HTTP_BAD_REQUEST.getCode(), ExceptionCode.HTTP_BAD_REQUEST.getMessagePattern().formatted(url, message), ExceptionCode.HTTP_BAD_REQUEST.getReason(), Instant.now()));
                exception = new CoodRecoverableAndNonRetryableException(throwableException);
            }
            case 404 -> {
                throwableException.setCoodError(new CoodError(ExceptionCode.HTTP_NOT_FOUND.getCode(), ExceptionCode.HTTP_NOT_FOUND.getMessagePattern().formatted(url, message), ExceptionCode.HTTP_NOT_FOUND.getReason(), Instant.now()));
                exception = new CoodRecoverableAndNonRetryableException(throwableException);
            }
            case 500 -> {
                throwableException.setCoodError(new CoodError(ExceptionCode.HTTP_INTERNAL_SERVER_ERROR.getCode(), ExceptionCode.HTTP_INTERNAL_SERVER_ERROR.getMessagePattern().formatted(url, message), ExceptionCode.HTTP_INTERNAL_SERVER_ERROR.getReason(), Instant.now()));
                exception = new CoodRecoverableAndNonRetryableException(throwableException);
            }
            default -> {
                exception = new CoodRecoverableAndNonRetryableException(throwableException);
            }
        }
        return exception;
    }

    public static Throwable getRootCause(Throwable ex) {
        Throwable result = ex;
        Throwable cause;

        while ((cause = result.getCause()) != null && cause != result) {
            result = cause;
        }

        return result;
    }
}
