// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.controller;

import com.orange.discobole.ordermanagement.orderinventory.api.v1.ProductOrderingManagementApi;
import com.orange.discobole.ordermanagement.orderinventory.constant.ErrorCodeEnum;
import com.orange.discobole.ordermanagement.orderinventory.constant.ErrorMessages;
import com.orange.discobole.ordermanagement.orderinventory.constant.PreAuthorizeExpressions;
import com.orange.discobole.ordermanagement.orderinventory.dto.ProductOrderResponse;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.SortEnum;
import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;
import com.orange.discobole.ordermanagement.orderinventory.service.AuthorizationService;
import com.orange.discobole.ordermanagement.orderinventory.service.ProductOrderService;
import com.orange.discobole.ordermanagement.orderinventory.service.util.ProductOrderQueryParametersBuilder;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductOrderControllerImpl implements ProductOrderingManagementApi {

    private final ProductOrderService productOrderService;
    private final Logger log = LoggerFactory.getLogger(ProductOrderControllerImpl.class);
    private final AuthorizationService authService;

    public ProductOrderControllerImpl(ProductOrderService productOrderService, AuthorizationService authService) {
        this.productOrderService = productOrderService;
        this.authService = authService;
    }

    @Hidden
    @Override
    @PreAuthorize(PreAuthorizeExpressions.CAN_CREATE_PRODUCT)
    public ResponseEntity<ProductOrder> createProductOrder(@Valid ProductOrder productOrder) {
        log.debug("REST request to save product order : {}", productOrder);
        if (productOrder.getId() != null) {
            throw new ProductOrderInventoryException(HttpStatus.BAD_REQUEST, ErrorCodeEnum.INVALID_INPUT.getCode(), ErrorCodeEnum.INVALID_INPUT.getStatus(), ErrorMessages.THE_PRODUCT_ORDER_ID_SHOULD_BE_NULL);
        }
        ProductOrder updatedProductOrder = productOrderService.saveProductOrder(productOrder);
        return new ResponseEntity<>(updatedProductOrder, HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize(PreAuthorizeExpressions.CAN_READ_PRODUCT)
    public ResponseEntity<ProductOrder> retrieveProductOrder(String id, @Valid String fields) {
        log.info("Received request to get product order by ID");
        this.authService.validateByIdRequest(id);
        ProductOrder dto = productOrderService.getProductOrderById(id, fields);
        return ResponseEntity.ok(dto);
    }

    @Override
    @PreAuthorize(PreAuthorizeExpressions.CAN_READ_PRODUCT)
    public ResponseEntity<List<ProductOrder>> listProductOrder(
            @Valid Integer offset,
            @Valid Integer limit,
            @Valid String creationDate,
            @Valid String notificationContact,
            @Valid String category,
            @Valid String relatedPartyId,
            @Valid String relatedPartyName,
            @Valid String relatedPartyRole,
            @Valid String channelName,
            @Valid String state,
            @Valid String cancellationDate,
            @Valid List<SortEnum> sort,
            @Valid String creationDateGt,
            @Valid String creationDateGte,
            @Valid String creationDateLt,
            @Valid String creationDateLte,
            @Valid List<String> id,
            @Valid String fields) {

        log.info("Received request to get product orders");

        ProductOrderQueryParametersBuilder parametersBuilder = new ProductOrderQueryParametersBuilder()
                .addIfNotBlank("fields", fields)
                .addIfPositive("offset", offset)
                .addIfPositive("limit", limit)
                .addListIfNotEmpty("sort", sort)
                .addListIfNotEmpty("id", id)
                .addIfNotBlank("notificationContact", notificationContact)
                .addIfNotBlank("category", category)
                .addIfNotBlank("relatedParty.partyOrPartyRole.id", relatedPartyId)
                .addIfNotBlank("relatedParty.partyOrPartyRole.name", relatedPartyName)
                .addIfNotBlank("relatedParty.role", relatedPartyRole)
                .addIfNotBlank("channel.channel.name", channelName)
                .addIfNotBlank("state", state)
                .addDateRange("cancellationDate", cancellationDate, null, null, null, null)
                .addDateRange("creationDate", creationDate, creationDateGt, creationDateGte, creationDateLt, creationDateLte);

        MultiValueMap<String, Object> queryParameters = parametersBuilder.build();

        authService.validateFetchRequest(queryParameters, "relatedParty.partyOrPartyRole.id");

        ProductOrderResponse productOrderResponse = productOrderService.getProductOrders(queryParameters);

        return new ResponseEntity<>(
                productOrderResponse.getProductOrders(),
                productOrderResponse.getResponseHeaders(),
                productOrderResponse.getHttpStatus()
        );
    }
}