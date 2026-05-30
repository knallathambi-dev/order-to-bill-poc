// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.dto.productoffering;

/**
 * Associates Product Offering Price Id to Product Offering Operation Spec Id
 */
public class AssociatePOPtoOperationSpec {

    private String operationspecid;
    private String productofferingpriceid;

    public String getOperationSpecId() {
        return operationspecid;
    }

    public void setOperationSpecId(String operationspecid) {
        this.operationspecid = operationspecid;
    }

    public String getProductOfferingPriceId() {
        return productofferingpriceid;
    }

    public void setProductOfferingPriceId(String productofferingpriceid) {
        this.productofferingpriceid = productofferingpriceid;
    }
}
