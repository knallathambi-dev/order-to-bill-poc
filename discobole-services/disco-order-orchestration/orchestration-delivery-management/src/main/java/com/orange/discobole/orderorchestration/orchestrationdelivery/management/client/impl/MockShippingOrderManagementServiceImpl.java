// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.httpfailed.ShippingOrderHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrder;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.shom.ShippingOrderCreate;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.client.ShippingOrderManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.management.util.DiscoServiceUrl;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import com.orange.discobole.orderorchestration.webclient.WebClientRetryStrategy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class MockShippingOrderManagementServiceImpl implements ShippingOrderManagementService {

    private final DiscoServiceUrl discoServiceUrl;

    private final WebClientUtil webClientUtil;

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public ShippingOrder createShippingOrder(ShippingOrderCreate shippingOrderCreate) {
        String shippingOrderUrl = discoServiceUrl.getShippingOrderUrl();
        return webClientUtil.send(HttpMethod.POST, shippingOrderUrl, ShippingOrder.class, objectMapper.writeValueAsString(shippingOrderCreate), (retryBackoffSpec, retrySignal) -> {
                    WebClientResponseException exception = (WebClientResponseException) retrySignal.failure();
                    return CoodNonRecoverableAndNonRetryableException.of(new ShippingOrderHttpFailedException(exception.getResponseBodyAsString(),
                            shippingOrderUrl,
                            exception.getStatusCode(),
                            WebClientRetryStrategy.getExceptionCode(exception),
                            exception));
                },
                shippingOrder -> log.info("Post method successfully done | shipping order id {} ", shippingOrder.getId()),
                new ShippingOrderHttpFailedException());
    }
}
