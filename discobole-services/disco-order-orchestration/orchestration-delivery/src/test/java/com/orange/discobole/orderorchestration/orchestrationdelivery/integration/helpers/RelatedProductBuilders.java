// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.integration.helpers;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RealisingService;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;

import java.util.List;
import java.util.Set;

public class RelatedProductBuilders {

    public static RelatedProduct.RelatedProductBuilder relatedProductBuilderAllTypes() {
        return RelatedProduct.builder()
                .id("649aaf9f2b4b3225cb5737ed")
                .relationshipType(RelatedProductRelationType.DELIVERS)
                .realisingService(List.of(RealisingService.builder().id("1109_10").build()))
                .productCharacteristic(Set.of(StringCharacteristic.builder().name("iccia").value("string").valueType("string").build(),
                        StringCharacteristic.builder().name("iccib").value("70.0").valueType("numeric").build(),
                        StringCharacteristic.builder().name("iccic").value("70").valueType("integer").build(),
                        StringCharacteristic.builder().name("iccid").value("true").valueType("boolean").build(),
                        StringCharacteristic.builder().name("iccie").value("{\"name\": \"name\"}").valueType("object").build(),
                        StringCharacteristic.builder().name("iccif").value("[\"string\",\"string2\"]").valueType("string array").build(),
                        StringCharacteristic.builder().name("iccip").value("[70.0, 80.0]").valueType("numeric array").build(),
                        StringCharacteristic.builder().name("iccik").value("[70, 80]").valueType("integer array").build(),
                        StringCharacteristic.builder().name("iccis").value("[true, false]").valueType("boolean array").build(),
                        StringCharacteristic.builder().name("iccir").value("[{\"name\": \"name\"},{\"name\": \"name\"}]").valueType("object array").build()))
                .productSpecification(ProductSpecification.builder()
                        .id("df32402e-ceb9-4467-aafd-fec0bbff3124")
                        .name("SIM_CARD")
                        .build());
    }
}
