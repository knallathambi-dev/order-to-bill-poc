// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ValidityCharacteristic;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.productinventory.dto.v1.Value;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.PRODUCT_CHARACTERISTIC_URI;

@Service
@Slf4j
public class ProductInventoryValidityCharacteristicService {
    private final ProductInventoryService productInventoryService;
    private final ObjectMapper objectMapper;
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ProductInventoryValidityCharacteristicService(ProductInventoryService productInventoryService, ObjectMapper objectMapper) {
        this.productInventoryService = productInventoryService;
        this.objectMapper = objectMapper;
    }


    public void updateProductValidityCharacteristic(
            Map<ProductOrderItem, ValidityCharacteristic> validityMap) {

        List<PatchDTO> patches = prepareValidityDateChangeForPatchRequest(validityMap);

        if (patches.isEmpty()) {
            return;
        }

        try {
            productInventoryService.updateProducts(
                    objectMapper.writeValueAsString(patches));
        } catch (JsonProcessingException e) {
            throw new DiscoException(
                    DescriptionConstants.VALIDITY_CHARACTERISTIC_DATE_ERROR, e);
        }
    }

    private List<PatchDTO> prepareValidityDateChangeForPatchRequest(Map<ProductOrderItem, ValidityCharacteristic> validityMap) {
        Map<String, ValidityCharacteristic> productIdToValidityCharacteristic = validityMap.entrySet().stream()
                .filter(entry -> entry.getKey().getProduct() instanceof Product)
                .filter(entry -> Objects.nonNull(((Product) entry.getKey().getProduct()).getId()))
                .collect(Collectors.toMap(
                        entry -> ((Product) entry.getKey().getProduct()).getId(),
                        Map.Entry::getValue,
                        (existing, replacement) -> existing
                ));

        List<String> productIds = productIdToValidityCharacteristic.keySet().stream().toList();

        List<com.orange.discobole.productinventory.dto.v1.Product> products = productInventoryService.getProductByIds(productIds);

        return productIdToValidityCharacteristic.entrySet().stream()
                .map(entry -> {
                    com.orange.discobole.productinventory.dto.v1.Product product = getProductById(products, entry.getKey());

                    if (product == null) {
                        return null;
                    }

                    ValidityCharacteristic validityCharacteristic = entry.getValue();
                    return createPatchForValidityCharacteristic(product, validityCharacteristic);

                })
                .filter(Objects::nonNull)
                .toList();


    }

    private PatchDTO createPatchForValidityCharacteristic(com.orange.discobole.productinventory.dto.v1.Product product, ValidityCharacteristic validityCharacteristic) {
        int index = findValidityCharacteristicIndex(product.getProductCharacteristic());
        if (index == -1) {
            return null;
        }
        Value newValue = getNewValue(product, validityCharacteristic, index);
        String path = String.format(PRODUCT_CHARACTERISTIC_URI, product.getId(), index);
        return createPatchRequest(path, newValue);
    }

    private Value getNewValue(com.orange.discobole.productinventory.dto.v1.Product product, ValidityCharacteristic validityCharacteristic, int index) {
        com.orange.discobole.productinventory.dto.v1.ValidityCharacteristic productValidityCharacteristic = (com.orange.discobole.productinventory.dto.v1.ValidityCharacteristic) product.getProductCharacteristic().get(index);
        Value newValue = productValidityCharacteristic.getValue();
        Instant validToInstant = validityCharacteristic.getValue().getValidTo();
        if (validToInstant != null) {
            OffsetDateTime validToOffsetDateTime = validToInstant.atZone(ZoneOffset.UTC)
                    .toOffsetDateTime();
            newValue.setValidTo(validToOffsetDateTime);
        }
        return newValue;
    }


    private int findValidityCharacteristicIndex(List<com.orange.discobole.productinventory.dto.v1.Characteristic> characteristics) {
        for (int i = 0; i < characteristics.size(); i++) {
            com.orange.discobole.productinventory.dto.v1.Characteristic characteristic = characteristics.get(i);
            if (characteristic instanceof com.orange.discobole.productinventory.dto.v1.ValidityCharacteristic) {
                return i;
            }
        }
        return -1;
    }

    private PatchDTO createPatchRequest(String path, Object value) {
        return PatchDTO.builder()
                .op(PatchOperationType.REPLACE)
                .path(path)
                .value(value)
                .build();
    }

    private com.orange.discobole.productinventory.dto.v1.Product getProductById(List<com.orange.discobole.productinventory.dto.v1.Product> products, String productId) {
        return products
                .stream()
                .filter(product -> productId.equals(product.getId()))
                .findFirst()
                .orElse(null);
    }


}
