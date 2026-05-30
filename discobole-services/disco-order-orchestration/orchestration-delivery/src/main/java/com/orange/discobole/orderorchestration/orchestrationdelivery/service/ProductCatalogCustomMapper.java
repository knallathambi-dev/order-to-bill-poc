// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.specification.ProductSpecificationCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;

import java.util.List;
import java.util.Set;

public interface ProductCatalogCustomMapper {
    Set<Characteristic> toProductCharacteristic(List<ProductSpecificationCharacteristic> productSpecificationCharacteristics) throws JsonProcessingException;

    Characteristic buildDefaultProductCharacteristic(ProductSpecificationCharacteristic productSpecificationCharacteristic);
}
