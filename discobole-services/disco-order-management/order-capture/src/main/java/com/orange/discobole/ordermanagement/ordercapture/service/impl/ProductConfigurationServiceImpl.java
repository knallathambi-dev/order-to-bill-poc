// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.configuration.QueryProductConfiguration;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductConfigurationService;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Slf4j
public class ProductConfigurationServiceImpl implements ProductConfigurationService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;
    private final Pattern configurationIdPattern = Pattern.compile("^(ACK|MOD|Delete|Migration.*)");

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductConfigurationServiceImpl(DiscoServiceUrl serviceUrl, WebClient webClient) {
        this.discoServiceUrl = serviceUrl;
        this.webClient = webClient;
    }

    @Override
    public QueryProductConfiguration getProductConfigurationById(String configurationId) throws DiscoException {
        log.info("Start retrieving product configuration items");
        if (isBlank(configurationId)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_PRODUCT_CONFIGURATION_ID);
        }
        return retrieveProductConfigurationItems(configurationId);
    }

    public QueryProductConfiguration retrieveProductConfigurationItems(String configurationId) {
        String parameterizedId = getParameterizedConfigurationId(configurationId);
        String productConfigurationUri = determineProductConfigurationUrl(parameterizedId);
        return webClient.get()
                .uri(productConfigurationUri)
                .retrieve()
                .toEntity(QueryProductConfiguration.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                })
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientException) {
                        HttpStatusCode statusCode = webClientException.getStatusCode();
                        if (statusCode == HttpStatus.NOT_FOUND) {
                            return Mono.error(new DiscoException(DescriptionConstants.ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS));
                        }
                    }
                    return Mono.error(new DiscoException(DescriptionConstants.PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE));
                })
                .block();
    }

    private String determineProductConfigurationUrl(String id) {
        if (startsWithConfigurationIdPattern(id)) {
            return discoServiceUrl.getProductConfigurationItemsByIdMockedUrl(id);
        }
        return discoServiceUrl.getProductConfigurationItemsByIdUrl(id);
    }

    private boolean startsWithConfigurationIdPattern(String input) {
        return configurationIdPattern.matcher(input).matches();
    }


    private String getParameterizedConfigurationId(String configurationId) {
        List<String> parts = List.of(configurationId.split("_"));
        String baseId = parts.get(0);
        if (parts.size() > 1) {
            String queryParams = String.join("&", parts.subList(1, parts.size()).stream().map(part -> "id=" + part).toList());
            return baseId + "?" + queryParams;
        }
        return baseId;
    }


}