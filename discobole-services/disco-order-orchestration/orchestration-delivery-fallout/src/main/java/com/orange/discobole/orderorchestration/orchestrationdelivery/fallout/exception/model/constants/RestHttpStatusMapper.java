// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants;

import java.util.Map;

import static com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.constants.ExceptionCode.*;


public class RestHttpStatusMapper {

    private static final Map<ExceptionCode, RestErrorCode> EXCEPTION_CODE_REST_ERROR_CODE_MAP = Map.of(
            FALLOUT_DB_EXCEPTION, RestErrorCode.FALLOUT_INTERNAL_SERVER_EXCEPTION,
            FALLOUT_HTTP_FAILED_EXCEPTION, RestErrorCode.FALLOUT_INTERNAL_SERVER_EXCEPTION,
            FALLOUT_MAPPING_EXCEPTION, RestErrorCode.FALLOUT_INTERNAL_SERVER_EXCEPTION,
            FALLOUT_NOT_FOUND_EXCEPTION, RestErrorCode.FALLOUT_NOT_FOUND_REST_EXCEPTION,
            FALLOUT_SECURITY_EXCEPTION, RestErrorCode.FALLOUT_SECURITY_EXCEPTION_INVALID_CREDENTIALS,
            FALLOUT_TECHNICAL_EXCEPTION, RestErrorCode.FALLOUT_INTERNAL_SERVER_EXCEPTION,
            FALLOUT_UNEXPECTED_STATE_EXCEPTION, RestErrorCode.FALLOUT_INTERNAL_SERVER_EXCEPTION,
            FALLOUT_VALIDATION_EXCEPTION, RestErrorCode.FALLOUT_VALIDATION_INVALID_QUERY_EXCEPTION
    );

    private RestHttpStatusMapper() {
    }


    public static RestErrorCode getRestErrorCode(ExceptionCode exceptionCode) {
        return EXCEPTION_CODE_REST_ERROR_CODE_MAP.getOrDefault(exceptionCode, RestErrorCode.FALLOUT_INTERNAL_SERVER_EXCEPTION);
    }

}
