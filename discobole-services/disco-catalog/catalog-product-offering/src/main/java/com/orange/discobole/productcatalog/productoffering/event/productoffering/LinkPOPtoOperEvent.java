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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;

/**
 * The Class LinkPOPtoOperEvent generated when productOfferingPriceId is equal
 * to operationSpecId.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */

public class LinkPOPtoOperEvent implements ProductOfferingEvent {

    @TargetAggregateIdentifier
    private final String prodOffId;
    private final List<CommercialOperation> operationList;
    private final List<ProductOfferingTerm> productOfferingTerm;
    private final OffsetDateTime lastUpdate;

    private LinkPOPtoOperEvent() {
        prodOffId = null;
        operationList = null;
        lastUpdate = null;
        productOfferingTerm = null;
    }

    public LinkPOPtoOperEvent(String prodOffId, List<CommercialOperation> operationList, List<ProductOfferingTerm> productOfferingTerm, OffsetDateTime lastUpdate) {
        this.prodOffId = prodOffId;
        this.operationList = operationList;
        this.lastUpdate = lastUpdate;
        this.productOfferingTerm = productOfferingTerm;
    }

    public List<CommercialOperation> getOperationList() {
        return operationList;
    }


    public String getProdOffId() {
        return prodOffId;
    }

    public List<ProductOfferingTerm> getProductOfferingTerm() {
        return productOfferingTerm;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }


    @Override
    public String toString() {
        return "LinkPOPtoOperEvent{" +
                "prodOffId='" + prodOffId + '\'' +
                ", operationList=" + operationList +
                ", productOfferingTerm=" + productOfferingTerm +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
