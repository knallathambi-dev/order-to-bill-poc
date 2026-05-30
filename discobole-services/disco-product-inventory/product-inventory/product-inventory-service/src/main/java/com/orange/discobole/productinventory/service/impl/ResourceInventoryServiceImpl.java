// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.config.ApplicationConfigProperties;
import com.orange.discobole.productinventory.constant.Constant;
import com.orange.discobole.productinventory.dto.ResourceInventoryResponseDTO;
import com.orange.discobole.productinventory.enumerate.ResourceEntityType;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ResourceRefEntity;
import com.orange.discobole.productinventory.service.ResourceInventoryRequestService;
import com.orange.discobole.productinventory.service.ResourceInventoryService;
import com.orange.discobole.productinventory.util.MiscUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.*;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class ResourceInventoryServiceImpl implements ResourceInventoryService {
    private final ResourceInventoryRequestService resourceInventoryRequestService;
    private final ApplicationConfigProperties applicationConfigProperties;

    @Override
    public void checkResourceInventory(ProductEntity tangibleProductSpec) {
        if (applicationConfigProperties.isEnableResourceInventoryManagementCheck()) {
            List<CompletableFuture<Boolean>> result = new ArrayList<>();

            for (ResourceRefEntity resourceRefEntity : tangibleProductSpec.getRealizingResource()) {
                if (resourceRefEntity.getAtType().equals(ResourceEntityType.PHYSICAL_RESOURCE.getValue())) {
                    result.add(validateResourceInventoryForTangibleProduct(resourceRefEntity.getId(), tangibleProductSpec));
                }
            }

            MiscUtil.getCompletableFutureList(result)
                    .exceptionally(ex -> {
                        if (ex.getCause() instanceof ProductInventoryException productInventoryException) {
                            throw productInventoryException;
                        } else {
                            throw new ProductInventoryException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR.getCode(), "Error getting result");
                        }
                    })
                    .join();
        }
    }

    private CompletableFuture<Boolean> validateResourceInventoryForTangibleProduct(String id, ProductEntity tangibleProductSpec) {
        return resourceInventoryRequestService.getResourceInventoryById(id)
                .flatMap(responseEntity -> {
                    log.debug("Response Status code: {}", responseEntity.getStatusCode());
                    log.debug("Response Body: {}", responseEntity.getBody());
                        ResourceInventoryResponseDTO resourceInventoryResponse = Objects.requireNonNull(responseEntity.getBody());
                        try {
                            validateTangibleProductSpecAgainstResourceInventoryResponse(resourceInventoryResponse, tangibleProductSpec);
                        } catch (ProductInventoryException | NullPointerException e) {
                            return Mono.error(e);
                        }
                        log.debug("All Product param exists");
                        return Mono.just(true);

                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof ProductInventoryException exception) {
                        HttpStatusCode statusCode = exception.getExceptionResponse().getStatus();
                        if (statusCode.isSameCodeAs(HttpStatus.NO_CONTENT) || statusCode.isSameCodeAs(HttpStatus.NOT_FOUND)) {
                            return Mono.error(new ProductInventoryException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND.getCode(), RESOURCE_NOT_FOUND.getStatus(), String.format(RESOURCE_DOESN_T_EXIST, id)));
                        } else {
                            log.error("Internal server error occurred");
                            return Mono.error(throwable);
                        }
                    } else {
                        log.error("Unexpected error occurred", throwable);
                        return Mono.error(new ProductInventoryException(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR.getCode(), INTERNAL_ERROR.getStatus(), INTERNAL_SERVER_ERROR));
                    }
                }).toFuture();
    }

    private void validateTangibleProductSpecAgainstResourceInventoryResponse(ResourceInventoryResponseDTO resourceInventoryResponse, ProductEntity tangibleProductSpec) {
        if (!resourceInventoryResponse.getResourceSpecification().getReferredType().equals(Constant.INVENTORY_RESOURCE_TANGIBLE_REFERRED_TYPE)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(),
                    String.format(RESOURCE_NOT_TANGIBLE, resourceInventoryResponse.getId()));
        }
        if (!resourceInventoryResponse.getResourceStatus().equals(Constant.INVENTORY_RESOURCE_RESERVED_RESOURCE_STATUS)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(),
                    String.format(RESOURCE_NOT_RESERVED, resourceInventoryResponse.getId()));
        }


        if (!tangibleProductSpec.getProductSerialNumber().equals(resourceInventoryResponse.getSerialNumber())) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(),
                    String.format(RESOURCE_SERIAL_NUMBER_DIFFERENT, resourceInventoryResponse.getId(), resourceInventoryResponse.getSerialNumber(), tangibleProductSpec.getProductSerialNumber()));
        }
    }

}
