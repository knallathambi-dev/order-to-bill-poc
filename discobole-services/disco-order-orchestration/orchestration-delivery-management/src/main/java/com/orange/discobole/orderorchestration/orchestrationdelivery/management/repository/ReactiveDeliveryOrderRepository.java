// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.repository;

import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryOrder;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReactiveDeliveryOrderRepository extends ReactiveMongoRepository<DeliveryOrder, String> {
    Flux<DeliveryOrder> findByFactoryOrderIdNotNullAndOrderItemRef_DeliveryStatusMapping_deliveryStatusIsNull();

}
