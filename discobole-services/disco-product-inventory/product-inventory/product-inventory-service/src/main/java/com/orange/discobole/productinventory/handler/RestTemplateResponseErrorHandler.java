// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.handler;


import com.orange.discobole.productinventory.exception.ProductInventoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.MISSING_CREDENTIALS;

@Component
@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        HttpStatusCode res = httpResponse.getStatusCode();
        log.info("http status code for the response - {}", res);
        if (res.isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            throw new ProductInventoryException(HttpStatus.UNAUTHORIZED, MISSING_CREDENTIALS.getCode(), MISSING_CREDENTIALS.getStatus());
        }
    }

    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        HttpStatusCode httpStatusCode = httpResponse.getStatusCode();
        log.info("http status code for the response - {}", httpResponse.getStatusCode());
        return httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError();
    }
}