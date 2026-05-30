// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.orderorchestration.exception.model.CoodMappingException;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.client.ProductManagementService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.httpfailed.CPIBHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.httpfailed.POIHttpFailedException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.exception.model.validations.ProductOrderValidationException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.CharacteristicMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.DiscoServiceUrl;
import com.orange.discobole.orderorchestration.util.WebClientUtil;
import com.orange.discobole.orderorchestration.webclient.WebClientRetryStrategy;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.dto.v1.ServiceRef;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.orange.discobole.orderorchestration.exception.handler.ExceptionThrower.doThrow;
import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.COOD_TECHNICAL_EXCEPTION;
import static com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode.INVALID_PRODUCTS_PARAMETERS;
import static com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ProductOrderItemActionType.*;
import static org.apache.commons.lang3.StringUtils.isBlank;


@Component
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
public class ProductManagementServiceImpl implements ProductManagementService {
    public static final String FIELDS_QUERY_PARAM = "fields";

    public static final String IDS_QUERY_PARAM = "id";

    private static final Map<ProductOperationalStatusType, ProductStatusType> CFS_OPERATIONAL_STATUS_TO_MAIN_STATUS_MAP = Map.of(
            ProductOperationalStatusType.PENDINGACTIVE, ProductStatusType.CREATED,
            ProductOperationalStatusType.LOCKED, ProductStatusType.CREATED,
            ProductOperationalStatusType.PENDINGMODIFICATION, ProductStatusType.ACTIVE,
            ProductOperationalStatusType.PENDINGTERMINATE, ProductStatusType.ACTIVE,
            ProductOperationalStatusType.ACTIVE, ProductStatusType.ACTIVE,
            ProductOperationalStatusType.LOCKEDACTIVE, ProductStatusType.ACTIVE,
            ProductOperationalStatusType.TERMINATED, ProductStatusType.TERMINATED,
            ProductOperationalStatusType.ABORTED, ProductStatusType.ABORTED
    );

    private static final Map<ProductOperationalStatusType, ProductStatusType> TANGIBLE_OPERATIONAL_STATUS_TO_MAIN_STATUS_MAP = Map.of(
            ProductOperationalStatusType.PENDINGDELIVERY, ProductStatusType.CREATED,
            ProductOperationalStatusType.LOCKED, ProductStatusType.CREATED,
            ProductOperationalStatusType.SOLD, ProductStatusType.SOLD,
            ProductOperationalStatusType.ABORTED, ProductStatusType.ABORTED
    );

    private final DiscoServiceUrl discoServiceUrl;

    private final WebClientUtil webClientUtil;

    private final ObjectMapper objectMapper;

    @Value("${config.enableUpdateCpibProduct}")
    private boolean updateCpibProduct;

    private final CharacteristicMapper characteristicMapper;

    @Override
    public List<Product> getProductsByOrderIdAndItemIds(List<String> orderItemIds, String productOrderId) throws ProductOrderValidationException {
        log.info("Inside getting product id: {}", productOrderId);
        if (isBlank(productOrderId) && Objects.nonNull(orderItemIds)) {
            doThrow(() -> new ProductOrderValidationException(INVALID_PRODUCTS_PARAMETERS));
        }
        return getProductByOrderIdAndOrderItemId(orderItemIds, productOrderId);
    }

    @Override
    public List<Product> getProductsByFields(List<String> productOrderId, List<String> fields) throws CPIBHttpFailedException {
        log.info("ProductManagementServiceImpl | getProductsByFields | Retrieving CPIB to get ProductOrderId {}, info: {}", productOrderId, fields);
        if (productOrderId.isEmpty()) {
            doThrow(() -> new ProductOrderValidationException(INVALID_PRODUCTS_PARAMETERS));
        }

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(discoServiceUrl.getProductManagementUrl())
                .queryParam(FIELDS_QUERY_PARAM, fields)
                .queryParam(IDS_QUERY_PARAM, productOrderId)
                .buildAndExpand(productOrderId);

        return webClientUtil.send(HttpMethod.GET, uriComponents.toUriString(), new ParameterizedTypeReference<>() {
        }, (retryBackoffSpec, retrySignal) -> {
            WebClientResponseException webClientResponseException = (WebClientResponseException) retrySignal.failure();
            return new CPIBHttpFailedException(webClientResponseException.getResponseBodyAsString(),
                    uriComponents.toUriString(),
                    webClientResponseException.getStatusCode(),
                    WebClientRetryStrategy.getExceptionCode(webClientResponseException),
                    webClientResponseException);
        }, response -> {
            log.info("ProductManagementServiceImpl | getProductsByFields | Finished retrieving CPIB to get ProductOrderId {}, fields: {}", productOrderId, fields);
            log.debug("ProductManagementServiceImpl | getProductsByFields | Finished retrieving CPIB to get ProductOrderId {}, fields: {} with response {}", productOrderId, fields, response);
        }, new POIHttpFailedException());
    }

    @Override
    public void updateCFSOperationalStatus(Product product, ProductOperationalStatusType operationalStatusType) {
        product.setOperationalStatus(operationalStatusType);
        product.setStatus(CFS_OPERATIONAL_STATUS_TO_MAIN_STATUS_MAP.get(operationalStatusType));

        updateProduct(product);
    }

    @Override
    public void updateTangibleOperationalStatus(Product product, ProductOperationalStatusType operationalStatusType) {
        product.setOperationalStatus(operationalStatusType);
        product.setStatus(TANGIBLE_OPERATIONAL_STATUS_TO_MAIN_STATUS_MAP.get(operationalStatusType));

        updateProduct(product);
    }

    private void updateProduct(Product product) throws CPIBHttpFailedException, CoodMappingException {
        if (Boolean.FALSE.equals(updateCpibProduct)) {
            return;
        }
        log.info("ProductManagementServiceImpl | updateProduct | Updating Product in CPIB, id: {}, dto: {}", product.getId(), product);
        String productManagementUri = discoServiceUrl.getProductManagementByIDUrl(product.getId());
        webClientUtil.send(HttpMethod.PATCH, productManagementUri, Product.class, getBody(product), (retryBackoffSpec, retrySignal) -> {
                    WebClientResponseException webClientResponseException = (WebClientResponseException) retrySignal.failure();
                    return new CPIBHttpFailedException(webClientResponseException.getResponseBodyAsString(),
                            productManagementUri,
                            webClientResponseException.getStatusCode(),
                            WebClientRetryStrategy.getExceptionCode(webClientResponseException),
                            webClientResponseException);
                }, productResponse -> log.info("ProductManagementServiceImpl | updateProduct | Patch method successfully done of product : " + productResponse.getId()),
                new POIHttpFailedException());
    }

    private String getBody(Product product) throws CoodMappingException {
        try {
            return objectMapper.writeValueAsString(product);
        } catch (JsonProcessingException exception) {
            throw new CoodMappingException(COOD_TECHNICAL_EXCEPTION, Object.class, Product.class, "Cannot send the Product to CPIB, error while serializing");
        }
    }

    public List<Product> getProductByOrderIdAndOrderItemId(List<String> orderItemIds, String productOrderId) throws CPIBHttpFailedException {
        log.info("ProductManagementServiceImpl | getProductByOrderIdAndOrderItemId | Retrieving Product from CPIB, filtered by ProductOrderId: {} and OrderItemIds: {}", productOrderId, orderItemIds);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(discoServiceUrl.getProductManagementUrl())
                .queryParam("productOrderItem.productOrderId", productOrderId)
                .queryParam("productOrderItem.orderItemId", orderItemIds)
                .buildAndExpand(productOrderId);

        return webClientUtil.send(HttpMethod.GET, uriComponents.toUriString(), new ParameterizedTypeReference<>() {
        }, (retryBackoffSpec, retrySignal) -> {
            WebClientResponseException webClientResponseException = (WebClientResponseException) retrySignal.failure();
            return new CPIBHttpFailedException(webClientResponseException.getResponseBodyAsString(),
                    uriComponents.toUriString(),
                    webClientResponseException.getStatusCode(),
                    WebClientRetryStrategy.getExceptionCode(webClientResponseException),
                    webClientResponseException);
        }, products -> {
            if (!CollectionUtils.isEmpty(products)) {
                log.info("Finished retrieving Product from CPIB, filtered by ProductOrderId: {} and OrderItemIt: {}", productOrderId, orderItemIds);
            }
        }, new POIHttpFailedException());
    }

    public void updateActualCPIBProductCharacteristicBasedOnServiceOrderItemCompletionStatue(Product product, OrchestrationPlanNode orchestrationPlanNode) {
        String orchestrationPlanNodeAction = orchestrationPlanNode.getActualRelatedOrderItem().getAction();
        if (!List.of(MODIFY.getValue(), ADD.getValue(), MIGRATE.getValue()).contains(orchestrationPlanNodeAction)) {
            throw CoodNonRecoverableAndNonRetryableException.of(new ProductOrderValidationException(ExceptionCode.INVALID_PRODUCT_ACTION, orchestrationPlanNodeAction, product.getId()));
        }

        if (MODIFY.getValue().equals(orchestrationPlanNodeAction)) {
            Set<Characteristic> nodeProductCharacteristics = orchestrationPlanNode.getActualRelatedProductOptional()
                    .map(RelatedProduct::getProductCharacteristic)
                    .orElse(Collections.emptySet());

            List<com.orange.discobole.productinventory.dto.v1.Characteristic> productCharacteristics = product.getProductCharacteristic();

            if (!CollectionUtils.isEmpty(productCharacteristics) && !CollectionUtils.isEmpty(nodeProductCharacteristics)) {

                // Map for fast lookup: name -> Characteristic
                Map<String, Characteristic> nodeCharacteristicByName = nodeProductCharacteristics.stream()
                        .collect(Collectors.toMap(Characteristic::getName, Function.identity()));

                // Find names that are COMMON between productCharacteristics & nodeProductCharacteristics
                Set<String> intersectionNames = productCharacteristics.stream()
                        .map(com.orange.discobole.productinventory.dto.v1.Characteristic::getName)
                        .filter(nodeCharacteristicByName::containsKey)
                        .collect(Collectors.toSet());

                // Remove ALL product characteristics that are in the intersection
                productCharacteristics.removeIf(productCharacteristic -> intersectionNames.contains(productCharacteristic.getName()));

                // Re-add the node characteristics mapped back
                intersectionNames.forEach(name ->
                        productCharacteristics.add(characteristicMapper.from(nodeCharacteristicByName.get(name)))
                );
            }
        }

        orchestrationPlanNode.getActualRelatedProductOptional().flatMap(relatedProduct -> Optional.ofNullable(relatedProduct.getRealisingService()))
                .ifPresent(realisingServices -> {
                    List<ServiceRef> serviceRefs = realisingServices.stream()
                            .map(realisingService -> (ServiceRef) ServiceRef.builder().id(realisingService.getId()).href(realisingService.getHref()).build())
                            .toList();
                    product.setRealizingService(serviceRefs);
                });

        // it is a workaround to allow external resolution till a business discussion taken
        if (ProductOperationalStatusType.LOCKED.equals(product.getOperationalStatus())) {
            log.info("ProductManagementServiceImpl | update cpib operational state in external resolution case | for cpib product id: {}", product.getId());
            product.setOperationalStatus(ProductOperationalStatusType.PENDINGACTIVE);
            product.setStatus(CFS_OPERATIONAL_STATUS_TO_MAIN_STATUS_MAP.get(product.getOperationalStatus()));
            updateProduct(product);
        }

        Product updatedProduct = Product.builder()
                .id(product.getId())
                .operationalStatus(ProductOperationalStatusType.ACTIVE)
                .status(CFS_OPERATIONAL_STATUS_TO_MAIN_STATUS_MAP.get(ProductOperationalStatusType.ACTIVE))
                .atType(product.getAtType())
                .productOrderItem(product.getProductOrderItem())
                .realizingService(product.getRealizingService())
                .productCharacteristic(product.getProductCharacteristic())
                .build();

        updateProduct(updatedProduct);
    }
}
