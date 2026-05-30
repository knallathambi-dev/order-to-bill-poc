// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.product.stock.ProductStock;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductStockManagementService;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.ordercapture.util.MiscUtil;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage.INVALID_PRODUCT_STOCK;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_BODY;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.RESPONSE_STATUS_CODE;

@Component
@Slf4j
public class ProductStockManagementServiceImpl implements ProductStockManagementService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductStockManagementServiceImpl(DiscoServiceUrl discoServiceUrl, WebClient webClient) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClient = webClient;

    }

    @Override
    public ProductStock reserveProductStock(ProductStock productStock) {
        log.debug("Start reserving product stock");
        if (Objects.isNull(productStock)) {
            throw new InvalidParameterException(INVALID_PRODUCT_STOCK);
        }
        String productStockManagementUrl = discoServiceUrl.getProductStockManagementUrl();
        return webClient.post()
                .uri(productStockManagementUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productStock)
                .retrieve()
                .toEntity(ProductStock.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                })
                .onErrorResume(error ->
                        Mono.error(new DiscoException(DescriptionConstants.PRODUCT_STOCK_SERVICE_UNREACHABLE)))
                .block();
    }

    @Override
    public Map<String, ProductStock> reserveProductStocks(Map<String, ProductStock> productOrderItemProductStockMap) {
        Map<String, ProductStock> reservedProductStocksList = new HashMap<>();
        try {
            List<CompletableFuture<Map<String, ProductStock>>> futures = reserveProductStocksAsyncList(productOrderItemProductStockMap);
            reservedProductStocksList = MiscUtil.getCompletableFutureList(futures)
                    .stream()
                    .flatMap(m -> m.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (InterruptedException e) {
            log.error("Error getting available resources: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            if (Thread.interrupted()) {
                throw new DiscoException("The current thread was interrupted");
            }
        } catch (Exception e) {
            throw new DiscoException("Error getting available resources");
        }
        return reservedProductStocksList;
    }

    @Override
    public Map<String, ProductStock> getReservedProductStocks(Map<String, String> productOrderReserveProductStockMap) {
        log.debug("Start getting reserved product stocks by IDs");
        if (productOrderReserveProductStockMap == null || productOrderReserveProductStockMap.isEmpty()) {
            return Collections.emptyMap();
        }

        // Extract unique product stock IDs
        Set<String> uniqueProductStockIds = new HashSet<>(productOrderReserveProductStockMap.values());
        List<String> productStockIds = new ArrayList<>(uniqueProductStockIds);

        // Call endpoint to get by IDs
        List<ProductStock> reservedProductStocks = getReservedProductStocksByIds(productStockIds);

        // Map results back to product order item IDs
        Map<String, ProductStock> reservedProductStocksMap = new HashMap<>();
        for (Map.Entry<String, String> entry : productOrderReserveProductStockMap.entrySet()) {
            String productOrderItemId = entry.getKey();
            String productStockId = entry.getValue();
            reservedProductStocks.stream()
                    .filter(stock -> productStockId.equals(stock.getId()))
                    .findFirst()
                    .ifPresent(productStock -> reservedProductStocksMap.put(productOrderItemId, productStock));
        }

        return reservedProductStocksMap;
    }

    private List<CompletableFuture<Map<String, ProductStock>>> reserveProductStocksAsyncList(Map<String, ProductStock> productOrderItemProductStockMap) {
        List<CompletableFuture<Map<String, ProductStock>>> futures = new ArrayList<>();
        for (Map.Entry<String, ProductStock> entry : productOrderItemProductStockMap.entrySet()) {
            CompletableFuture<Map<String, ProductStock>> future = CompletableFuture.supplyAsync(
                    () -> {
                        Map<String, ProductStock> prodOrderItemResservedProductStockyMap = new HashMap<>();
                        ProductStock reservedProduct = this.reserveProductStock(entry.getValue());
                        prodOrderItemResservedProductStockyMap.put(entry.getKey(), reservedProduct);
                        return prodOrderItemResservedProductStockyMap;
                    });
            futures.add(future);
        }
        return futures;
    }

    private List<ProductStock> getReservedProductStocksByIds(List<String> productStockIds) {
        log.debug("Getting reserved product stocks by ids: {}", productStockIds);
        if (productStockIds == null || productStockIds.isEmpty()) {
            return Collections.emptyList();
        }

        String productStockManagementUrl = discoServiceUrl.getProductStockManagementUrl();
        return webClient.get()
                .uri(productStockManagementUrl, uri -> uri
                        .queryParam("ids", String.join(",", productStockIds))
                        .build())
                .retrieve()
                .toEntityList(ProductStock.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                })
                .onErrorResume(error -> {
                    log.error("Error getting reserved product stock: {}", error.getMessage(), error);
                    return Mono.error(new DiscoException(DescriptionConstants.PRODUCT_STOCK_SERVICE_UNREACHABLE));
                })
                .block();
    }
}