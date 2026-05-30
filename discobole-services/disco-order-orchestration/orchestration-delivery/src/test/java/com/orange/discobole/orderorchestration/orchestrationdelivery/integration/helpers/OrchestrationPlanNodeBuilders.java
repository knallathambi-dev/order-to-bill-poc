// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.integration.helpers;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedOrchestrationPlanNodeRelationshipType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductRelationType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.RelatedProductType;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;

import java.util.List;
import java.util.Set;

public class OrchestrationPlanNodeBuilders {


    public static OrchestrationPlanNode.OrchestrationPlanNodeBuilder orchestrationPlanNodeSIMCardModifyBuilder() {
        return OrchestrationPlanNode.builder()
                .id("7636502b-02d3-447c-86d8-ee646fb8f109")
                .state(OrchestrationPlanNodeState.COMPLETED)
                .relatedProductOrder(RelatedProductOrder.builder().id("1109").build())
                .relatedProductOrderItem(List.of(RelatedProductOrderItem.builder().id("10").action("modify").build()))
                .relatedProduct(List.of(
                        RelatedProduct.builder()
                                .id("649aaf9f2b4b3225cb5737ed")
                                .relationshipType(RelatedProductRelationType.DELIVERS)
                                .realisingService(List.of(RealisingService.builder().id("1109_10").build()))
                                .productCharacteristic(Set.of(StringCharacteristic.builder().name("iccid").value("70.0").valueType("string").build(),
                                        StringCharacteristic.builder().name("iccip").value("70.0").valueType("string").build()))
                                .productSpecification(ProductSpecification.builder()
                                        .id("df32402e-ceb9-4467-aafd-fec0bbff3124")
                                        .name("SIM_CARD")
                                        .build())
                                .build()
                ))
                .relatedOrchestrationPlanNode(List.of());
    }


    public static OrchestrationPlanNode.OrchestrationPlanNodeBuilder orchestrationPlanNodeConnectivityModifyBuilder(String relatedProductOrderId, String relatedProductId, String productSpecId) {
        return OrchestrationPlanNode.builder()
                .id("f1081cd2-d2f1-4ea9-8e8d-7fe890e03d95")
                .state(OrchestrationPlanNodeState.COMPLETED)
                .relatedProductOrder(RelatedProductOrder.builder().id(relatedProductOrderId).build())
                .relatedProductOrderItem(List.of(RelatedProductOrderItem.builder().id("08").action("modify").build()))
                .relatedProduct(List.of(
                        RelatedProduct.builder().id(relatedProductId)
                                .relationshipType(RelatedProductRelationType.RELIES_ON)
                                .type(RelatedProductType.CFS)
                                .isInstallable(true)
                                .productSpecification(ProductSpecification.builder().id("df32402e-ceb9-4467-aafd-fec0bbff3124").build()).build(),
                        RelatedProduct.builder()
                                .id(relatedProductId)
                                .relationshipType(RelatedProductRelationType.DELIVERS)
                                .realisingService(List.of(RealisingService.builder().id("1109_10").build()))
                                .productCharacteristic(Set.of(StringCharacteristic.builder().name("iccid").value("70.0").valueType("string").build(),
                                        StringCharacteristic.builder().name("iccip").value("70.0").valueType("string").build()))
                                .productSpecification(ProductSpecification.builder().id(productSpecId)
                                        .name("Connectivity")
                                        .serviceSpecification(List.of(ServiceSpecification.builder().id("Connectivity").name("Connectivity").build())).build())
                                .type(RelatedProductType.CFS)
                                .productOrderItemId("08")
                                .isInstallable(true)
                                .build()
                ))
                .relatedOrchestrationPlanNode(List.of(RelatedOrchestrationPlanNode.builder()
                        .relatedNodeId("7636502b-02d3-447c-86d8-ee646fb8f109")
                        .relationshipType(RelatedOrchestrationPlanNodeRelationshipType.DELIVER_AFTER)
                        .build()))
                .relatedServiceOrder(RelatedServiceOrder.builder().id("d2e28645-1938-41e4-a5c1-2e5501e74fd1").orderItemId("1").build());
    }

}
