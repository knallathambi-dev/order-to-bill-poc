// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.time.OffsetDateTime;
import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationCharacteristicValueUse;

/**
 * The Class AtomicProductOfferingCharacteristicsDefinedEvent
 *
 * @author Shreya Sharma
 * @since 1.0
 */
public class AtomicProductOfferingCharacteristicsDefinedEvent implements ProductOfferingEvent {

        @TargetAggregateIdentifier
        private final String productOfferingId;
        private final List<ProductSpecificationCharacteristicValueUse> productSpecificationCharacteristicValueUse;
        private final OffsetDateTime lastUpdate;

        private AtomicProductOfferingCharacteristicsDefinedEvent() {
            productOfferingId = null;
            productSpecificationCharacteristicValueUse = null;
            lastUpdate = null;
        }

        public AtomicProductOfferingCharacteristicsDefinedEvent(String productOfferingId,
                                                                List<ProductSpecificationCharacteristicValueUse> productSpecificationCharacteristicValueUse
                , OffsetDateTime lastUpdate) {
            this.productOfferingId = productOfferingId;
            this.productSpecificationCharacteristicValueUse = productSpecificationCharacteristicValueUse;
            this.lastUpdate = lastUpdate;
        }

        @Override
        public String toString() {
            return "ProductSpecCharacteristicsDefinedEvent [productOfferingId=" + productOfferingId
                    + ", ProductSpecificationCharacteristicValueUse=" + productSpecificationCharacteristicValueUse + ", lastUpdate="
                    + lastUpdate + "]";
        }

        public String getproductOfferingId() {
            return productOfferingId;
        }

        public List<ProductSpecificationCharacteristicValueUse> getProductSpecificationCharacteristicValueUse() {
            return productSpecificationCharacteristicValueUse;
        }

        public OffsetDateTime getLastUpdate() {
            return lastUpdate;
        }

}
