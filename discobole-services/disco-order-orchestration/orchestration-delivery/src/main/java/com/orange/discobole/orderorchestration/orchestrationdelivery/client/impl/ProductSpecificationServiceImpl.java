// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.client.impl;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRefOrValue;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductSpecificationService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.common.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductSpecificationBaseType;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.httpfailed.CPIBHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.httpfailed.PCHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductSpecificationValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.DiscoServiceUrl;
 import com.orange.discobole.orderorchestration.webclient.WebClientRetryStrategy;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.COULD_NOT_GET_PRODUCT_SPECIFICATION_IDS;
import static io.micrometer.core.instrument.util.StringUtils.isBlank;

@Component
@Slf4j
public class ProductSpecificationServiceImpl implements ProductSpecificationService {

    private final DiscoServiceUrl discoServiceUrl;

    private final WebClientUtil webClientUtil;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductSpecificationServiceImpl(DiscoServiceUrl discoServiceUrl, WebClientUtil webClientUtil) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClientUtil = webClientUtil;
    }

    @Override
    public ProductSpecification retrieveProductSpecById(String id) throws ProductSpecificationValidationException {
        log.info("ProductSpecificationServiceImpl | retrieveProductSpecById | Inside getting product specifications by id {}", id);
        if (isBlank(id)) {
            throw new ProductSpecificationValidationException(ExceptionCode.COOD_TECHNICAL_EXCEPTION, "Invalid parameter, required id");
        }
        return getProductSpecification(id);
    }

    public ProductSpecification getProductSpecification(String id) throws PCHttpFailedException {
        String catalogProductSpecUrl = discoServiceUrl.getProductSpecByIdUrl(id);
        return webClientUtil.send(HttpMethod.GET, catalogProductSpecUrl, ProductSpecification.class, (retryBackoffSpec, retrySignal) -> {
            WebClientResponseException webClientResponseException = (WebClientResponseException) retrySignal.failure();
            return new CPIBHttpFailedException(webClientResponseException.getResponseBodyAsString(),
                    catalogProductSpecUrl,
                    webClientResponseException.getStatusCode(),
                    WebClientRetryStrategy.getExceptionCode(webClientResponseException),
                    webClientResponseException);
        }, response -> {
            log.info("ProductSpecificationServiceImpl | retrieveProductSpecById | retrieved product specification {} successfully", id);
            log.debug("ProductSpecificationServiceImpl | retrieveProductSpecById | retrieved product specification {} with response {}", id, response);
        }, new PCHttpFailedException());

    }

    private Map<String, ProductSpecification> retrieveProductSpecificationsByIds(List<String> ids, String orchestrationPlanId) throws ProductSpecificationValidationException, PCHttpFailedException {
        log.info("ProductSpecificationServiceImpl | retrieveProductSpecificationsByIds | retrieving product specifications for IDs: {} for orchestrationPlanId {}", String.join(", ", ids), orchestrationPlanId);

        if (CollectionUtils.isEmpty(ids)) {
            throw new CoodRecoverableAndNonRetryableException(new ProductSpecificationValidationException(COULD_NOT_GET_PRODUCT_SPECIFICATION_IDS, orchestrationPlanId));
        }

        String catalogProductSpecUrl = discoServiceUrl.getProductSpecificationsByIdsUrl(ids);
        List<ProductSpecification> productSpecifications = webClientUtil.send(HttpMethod.GET, catalogProductSpecUrl, new ParameterizedTypeReference<>() {
        }, (retryBackoffSpec, retrySignal) -> {
            WebClientResponseException exception = (WebClientResponseException) retrySignal.failure();
            String message = "Error retrieving product specification with ProductOrderId %s and status %s".formatted(ids, exception.getStatusCode());
            return new CoodRecoverableAndNonRetryableException(new PCHttpFailedException(message, catalogProductSpecUrl, exception.getStatusCode(), WebClientRetryStrategy.getExceptionCode(exception), exception));
        }, resultBody -> {
            log.info("Returned product specifications response for IDs: {} successfully", String.join(", ", ids));
            log.debug("Returned product specifications response for IDs: {} successfully response {}", String.join(", ", ids), resultBody);
        }, new PCHttpFailedException());

        log.info("ProductSpecificationServiceImpl | retrieveProductSpecificationsByIds | result {} retrieving product specifications for IDs: {} for orchestrationPlanId {}", productSpecifications, String.join(", ", ids), orchestrationPlanId);
        if (productSpecifications != null) {
            return productSpecifications.stream()
                    .collect(Collectors.toMap(ProductSpecification::getId, Function.identity(), (existing, replacement) -> existing));
        }
        return Map.of();
    }

    @Override
    public Map<String, ProductSpecification> retrieveProductSpecificationsFromProductOrderItem(List<ProductOrderItem> productOrderItems, String orchestrationPlanId) {
        log.info("ProductSpecificationServiceImpl | retrieveProductSpecificationsFromProductOrderItem | OrchestrationPlanId: {} productOrderItems: {}", orchestrationPlanId, productOrderItems);
        List<String> productSpecificationIds = getProductSpecificationIdsExcept(productOrderItems, ProductSpecificationBaseType.SHIPPING_PRODUCT_SPECIFICATION.getValue());
        return retrieveProductSpecificationsByIds(productSpecificationIds, orchestrationPlanId);
    }

    private static List<String> getProductSpecificationIdsExcept(List<ProductOrderItem> productOrderItems, String baseType) {
        return productOrderItems.stream()
                .filter(productOrderItem -> {
                    ProductRefOrValue product = productOrderItem.getProduct();
                    // Check if product is an instance of Product and has a non-null ProductSpecification
                    return product instanceof Product &&
                            Objects.nonNull(((Product) product).getProductSpecification()) &&
                            !baseType.equals(((Product) product).getProductSpecification().getAtBaseType());
                })
                .map(productOrderItem -> ((Product) productOrderItem.getProduct()).getProductSpecification().getId())
                .toList();
    }
}
