// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.kafka.producer.impl.ProductAttributeValueChangeEventProducerImpl;
import com.orange.discobole.productinventory.model.ProductEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.orange.discobole.productinventory.enumerate.PublishEventEnum.PRODUCT_VALIDITY;

@Service
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class ProductTerminationEventService {
    private final ProductAttributeValueChangeEventProducerImpl productAttributeValueChangeEventProducer;
    @Async
    public void sendEventOnTerminationDateChange(List<ProductEntity> prePatchedProducts, List<ProductEntity> patchedProducts) {
        for (ProductEntity prePatchedProduct : prePatchedProducts) {
            Optional<ProductEntity> productPatched = patchedProducts.stream().filter(productEntity -> productEntity.getId().equals(prePatchedProduct.getId())).findAny();
            if (productPatched.isPresent() && !Objects.equals(productPatched.get().getTerminationDate(), prePatchedProduct.getTerminationDate())) {
                ProductEntity product = productPatched.get();
                Product productPublishEvent = Product.builder().id(product.getId()).terminationDate(product.getTerminationDate()).atType(product.getAtType()).status(product.getStatus()).build();
                productAttributeValueChangeEventProducer.publishEvent(productPublishEvent, PRODUCT_VALIDITY.getTitle(), PRODUCT_VALIDITY.getDomain());
            }
        }

    }

}