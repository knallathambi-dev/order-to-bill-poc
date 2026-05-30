// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.builder;


import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.ProductSpecification;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.RelatedProduct;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.ServiceSpecification;

import java.util.List;

public class RelatedProductBuilder {

    public static RelatedProduct.RelatedProductBuilder getRelatedProductBuilder() {
        return RelatedProduct.builder()
                .id("relatedId")
                .relationshipType(RelatedProductRelationType.DELIVERS)
                .productSpecification(ProductSpecification.builder().id("111").name("ssss")
                        .serviceSpecification(List.of(ServiceSpecification.builder()
                                .id("111")
                                .name("4444").build())).build());
    }


}
