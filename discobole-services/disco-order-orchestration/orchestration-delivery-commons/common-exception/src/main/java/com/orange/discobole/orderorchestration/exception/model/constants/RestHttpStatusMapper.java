// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model.constants;

import java.util.Map;

import static com.orange.discobole.orderorchestration.exception.model.constants.RestErrorCode.*;

public class RestHttpStatusMapper {

    private static final Map<ExceptionCode, RestErrorCode> EXCEPTION_CODE_REST_ERROR_CODE_MAP = Map.of(
            ExceptionCode.COOD_DB_EXCEPTION, COOD_INTERNAL_SERVER_EXCEPTION,
            ExceptionCode.COOD_HTTP_FAILED_EXCEPTION, COOD_INTERNAL_SERVER_EXCEPTION,
            ExceptionCode.COOD_MAPPING_EXCEPTION, COOD_INTERNAL_SERVER_EXCEPTION,
            ExceptionCode.COOD_NOT_FOUND_EXCEPTION, COOD_NOT_FOUND_REST_EXCEPTION,
            ExceptionCode.COOD_SECURITY_EXCEPTION, COOD_SECURITY_EXCEPTION_INVALID_CREDENTIALS,
            ExceptionCode.COOD_TECHNICAL_EXCEPTION, COOD_INTERNAL_SERVER_EXCEPTION,
            ExceptionCode.COOD_UNEXPECTED_STATE_EXCEPTION, COOD_INTERNAL_SERVER_EXCEPTION,
            ExceptionCode.COOD_VALIDATION_EXCEPTION, COOD_VALIDATION_INVALID_QUERY_EXCEPTION
    );

    private RestHttpStatusMapper() {
    }


    public static RestErrorCode getRestErrorCode(ExceptionCode exceptionCode) {
        return EXCEPTION_CODE_REST_ERROR_CODE_MAP.getOrDefault(exceptionCode, COOD_INTERNAL_SERVER_EXCEPTION);
    }

}
