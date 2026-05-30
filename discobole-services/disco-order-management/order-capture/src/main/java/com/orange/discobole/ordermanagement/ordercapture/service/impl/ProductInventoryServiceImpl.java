// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;


import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTOList;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.productinventory.dto.v1.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.util.*;
import java.util.function.Predicate;


import static com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage.INVALID_PRODUCT_LIST;
import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.BUNDLES;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Slf4j
public class ProductInventoryServiceImpl implements ProductInventoryService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductInventoryServiceImpl(DiscoServiceUrl discoServiceUrl, WebClient webClient) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClient = webClient;
    }

    @Override
    public List<Product> getProductsByRelationship(String productId) {
        String productManagementUrl = discoServiceUrl.getProductInventoryUrl();
        return webClient.get()
                .uri(productManagementUrl, uri -> uri
                        .queryParam("productRelationship.product.id", productId)
                        .queryParam("status", "Active", "Sold")
                        .build())
                .retrieve()
                .toEntityList(Product.class)
                .flatMap(response -> {
                    log.debug(RESPONSE_STATUS_CODE, response.getStatusCode());
                    log.debug(RESPONSE_BODY, response.getBody());
                    return Mono.just(Objects.requireNonNull(response.getBody()).stream()
                            .toList());
                })
                .onErrorResume(error ->
                        Mono.error(new DiscoException(ExceptionMessage.PRODUCT_INVENTORY_SERVICE_UNREACHABLE))
                )
                .block();
    }

    @Override
    public Product createProducts(Product product) {
        log.info("Start creating product in product inventory");
        if (Objects.isNull(product)) {
            throw new InvalidParameterException(INVALID_PRODUCT_LIST);
        }
        String productManagementUri = discoServiceUrl.getProductInventoryUrl();
        return webClient.post()
                .uri(productManagementUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .toEntity(Product.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                })
                .onErrorResume(error ->
                        Mono.error(new DiscoException(ExceptionMessage.PRODUCT_INVENTORY_SERVICE_UNREACHABLE))
                )
                .block();
    }

    @Override
    public List<Product> updateProducts(String jsonPatch) {
        String productManagementUri = discoServiceUrl.getProductInventoryUrl();
        return webClient.patch()
                .uri(productManagementUri)
                .header("Content-Type", APPLICATION_JSON_PATCH_JSON)
                .bodyValue(jsonPatch)
                .retrieve()
                .toEntityList(Product.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));

                })
                .onErrorResume(error ->
                        Mono.error(new DiscoException(ExceptionMessage.PRODUCT_INVENTORY_SERVICE_UNREACHABLE))
                )
                .block();
    }

    @Override
    public void confirmProducts(List<String> productIds) {
        log.info("Inside confirm products");
        try {
            PatchDTOList productPatch = createStatusesPatchRequest(productIds, null, ProductOperationalStatusType.CONFIRMED);
            updateProducts(productPatch.toJsonString());
        } catch (Exception e) {
            throw new DiscoException(ExceptionMessage.ERROR_CONFIRMING_PRODUCTS, e);
        }
    }

    @Override
    public void terminateProducts(List<Product> terminatedProducts, List<Product> products, String productOrderId) {
        log.info("Inside terminate products");
        List<String> productIdsList = new ArrayList<>();
        List<Product> bundledProducts = getBundledProduct(terminatedProducts);
        bundledProducts.forEach(product -> {
            List<Product> leafProducts = getLeafProducts(products, product);
            if (CollectionUtils.isEmpty(leafProducts) || allHaveSoldStatus(leafProducts, productOrderId)) {
                productIdsList.add(product.getId());
            }
        });
        if (!CollectionUtils.isEmpty(productIdsList)) {
            try {
                PatchDTOList productPatch = createStatusesPatchRequest(productIdsList, ProductStatusType.TERMINATED, ProductOperationalStatusType.TERMINATED);
                updateProducts(productPatch.toJsonString());
            } catch (Exception e) {
                throw new DiscoException(ExceptionMessage.ERROR_TERMINATING_PRODUCTS, e);
            }
        }
    }

    private List<Product> getLeafProducts(List<Product> products, Product product) {
        List<Product> childProducts = new ArrayList<>();
        getChildProducts(products, product, childProducts);
        return childProducts.stream()
                .filter(this::isALeafProduct)
                .toList();
    }


    List<String> getProductIdsWithBundlesRelationType(Product productDTO) {
        return productDTO.getProductRelationship().stream()
                .filter(productRelationship -> BUNDLES.equals(productRelationship.getRelationshipType())
                        && productRelationship.getProduct() != null
                        && productRelationship.getProduct().getAtType().equals(PRODUCT_REF))
                .map(productRelationship -> ((ProductRef) productRelationship.getProduct()).getId())
                .toList();
    }

    private void getChildProducts(List<Product> products, Product product, List<Product> childProducts) {
        if (!isALeafProduct(product)) {
            List<String> childProductIds = getProductIdsWithBundlesRelationType(product);
            List<Product> foundProducts = getProductsByIds(childProductIds, products);
            foundProducts.forEach(foundProduct ->
                    getChildProducts(products, foundProduct, childProducts));
        }
        childProducts.add(product);
    }


    private boolean isALeafProduct(Product product) {
        if (Objects.isNull(product.getProductRelationship())) {
            return true;
        } else {
            return product.getProductRelationship().stream()
                    .noneMatch(productRelationship -> BUNDLES.equals(productRelationship.getRelationshipType()));
        }
    }

    List<Product> getProductsByIds(List<String> childIds, List<Product> products) {
        return products.stream()
                .filter(product -> childIds.contains(product.getId()))
                .toList();
    }

    @Override
    public void cancelProducts(List<String> productIds) {
        log.info("Inside cancel products");
        try {
            PatchDTOList productPatch = createStatusesPatchRequest(productIds, ProductStatusType.CANCELLED, ProductOperationalStatusType.CANCELLED);
            updateProducts(productPatch.toJsonString());
        } catch (Exception e) {
            throw new DiscoException(ExceptionMessage.ERROR_CANCELLING_PRODUCTS, e);
        }
    }

    @Override
    public List<String> getProductsIdsByProductOrder(ProductOrder productOrder, Predicate<Product> filter) {
        String productManagementUrl = discoServiceUrl.getProductInventoryUrl();
        return webClient.get()
                .uri(productManagementUrl, uri -> uri
                        .queryParam("fields", "id")
                        .queryParam("productOrderItem.productOrderId", productOrder.getId())
                        .build())
                .retrieve()
                .toEntityList(Product.class)
                .flatMap(response -> {
                    log.debug(RESPONSE_STATUS_CODE, response.getStatusCode());
                    log.debug(RESPONSE_BODY, response.getBody());
                    return Mono.just(Objects.requireNonNull(response.getBody()).stream()
                            .filter(filter)
                            .map(Product::getId)
                            .toList());
                })
                .onErrorResume(error ->
                        Mono.error(new DiscoException(ExceptionMessage.PRODUCT_INVENTORY_SERVICE_UNREACHABLE))
                )
                .block();
    }

    @Override
    public Product getProductById(String productId) {
        if (isBlank(productId)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_PRODUCT_ID);
        }
        String productManagementUri = discoServiceUrl.getProductManagementByIdUrl(productId);
        return webClient.get()
                .uri(productManagementUri)
                .retrieve()
                .toEntity(Product.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                })
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientException) {
                        HttpStatusCode statusCode = webClientException.getStatusCode();
                        if (statusCode == HttpStatus.NOT_FOUND) {
                            return Mono.error(new DiscoException(DescriptionConstants.SELECTED_PRODUCT_DOES_NOT_EXIST));
                        }
                    }
                    return Mono.error(new DiscoException(ExceptionMessage.PRODUCT_INVENTORY_SERVICE_UNREACHABLE));
                })
                .block();
    }

    @Override
    public List<Product> getProductByIds(List<String> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_PRODUCT_ID_PARAMETERS);
        }
        String productManagementUrl = discoServiceUrl.getProductInventoryUrl();
        return webClient.get()
                .uri(productManagementUrl, uri -> uri
                        .queryParam("id", String.join(",", productIds))
                        .build())
                .retrieve()
                .toEntityList(Product.class)
                .flatMap(response -> {
                    log.debug(RESPONSE_STATUS_CODE, response.getStatusCode());
                    log.debug(RESPONSE_BODY, response.getBody());
                    return Mono.just(Objects.requireNonNull(response.getBody()));
                })
                .onErrorResume(error -> Mono.error(new DiscoException(ExceptionMessage.PRODUCT_INVENTORY_SERVICE_UNREACHABLE)))
                .block();
    }

    private PatchDTOList createStatusesPatchRequest(List<String> productIds, ProductStatusType
            status, ProductOperationalStatusType operationalStatus) {
        List<PatchDTO> patchDTOList = productIds.stream()
                .map(productId -> buildProductPatchRequest(productId, status, operationalStatus))
                .flatMap(List::stream)
                .toList();

        return PatchDTOList.builder()
                .list(patchDTOList)
                .build();
    }

    private List<PatchDTO> buildProductPatchRequest(String productId, ProductStatusType status, ProductOperationalStatusType operationalStatus) {
        String productUri = PRODUCT_INVENTORY_URI + productId;
        List<PatchDTO> patches = new ArrayList<>();

        if (status != null) {
            PatchDTO patchStatusDTO = createPatchRequest(productUri + STATUS_URI, status.getValue());
            patches.add(patchStatusDTO);
        }
        PatchDTO patchOperationalStatusDTO = createPatchRequest(productUri + OPERATIONAL_STATUS_URI, operationalStatus.getValue());
        patches.add(patchOperationalStatusDTO);
        return patches;
    }

    @Override
    public List<Product> getProductsByProductOrderId(String productOrderId) {
        String productManagementUrl = discoServiceUrl.getProductInventoryUrl();
        return webClient.get()
                .uri(productManagementUrl, uri -> uri
                        .queryParam("productOrderItem.productOrderId", productOrderId)
                        .build())
                .retrieve()
                .toEntityList(Product.class)
                .flatMap(response -> {
                    log.debug(RESPONSE_STATUS_CODE, response.getStatusCode());
                    log.debug(RESPONSE_BODY, response.getBody());
                    return Mono.just(Objects.requireNonNull(response.getBody()));
                })
                .onErrorResume(error -> Mono.error(new DiscoException(ExceptionMessage.PRODUCT_INVENTORY_SERVICE_UNREACHABLE)))
                .block();
    }

    @Override
    public void abortProducts(List<String> productIds) {
        log.info("Inside abort products");
        try {
            PatchDTOList productPatch = createStatusesPatchRequest(productIds, ProductStatusType.ABORTED, ProductOperationalStatusType.ABORTED);
            updateProducts(productPatch.toJsonString());
        } catch (Exception e) {
            throw new DiscoException(ExceptionMessage.ERROR_ABORTING_PRODUCTS, e);
        }
    }

    private PatchDTO createPatchRequest(String path, Object value) {
        return PatchDTO.builder()
                .op(PatchOperationType.REPLACE)
                .path(path)
                .value(value)
                .build();
    }

    List<Product> getBundledProduct(List<Product> products) {
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyList();
        }
        return products.stream()
                .filter(product -> product.getProductRelationship() != null)
                .filter(product -> product.getProductRelationship().stream()
                        .anyMatch(productRelationship -> productRelationship.getRelationshipType() != null && BUNDLES.equals(productRelationship.getRelationshipType())))
                .toList();
    }

    boolean allHaveSoldStatus(List<Product> products, String productOrderId) {
        return !CollectionUtils.isEmpty(products) && products.stream()
                .allMatch(product -> product.getStatus() == ProductStatusType.SOLD && product.getProductOrderItem()
                        .stream().anyMatch(relatedProductOrderItem -> productOrderId.equals(relatedProductOrderItem.getProductOrderId())
                                && OrderCaptureConstants.DELETE.equals(relatedProductOrderItem.getOrderItemAction())
                        ));

    }
}