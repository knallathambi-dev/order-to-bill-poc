// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
package com.orange.discobole.productcatalog.policyrule.dto;

import java.util.List;

public class PolicyOfferingEvent {
    private String polciyRuleId;
    private List<ProductOffering> productOfferingList;

    public String getPolciyRuleId() {
        return polciyRuleId;
    }

    public void setPolciyRuleId(String polciyRuleId) {
        this.polciyRuleId = polciyRuleId;
    }

    public List<ProductOffering> getProductOfferingList() {
        return productOfferingList;
    }

    public void setProductOfferingList(List<ProductOffering> productOfferingList) {
        this.productOfferingList = productOfferingList;
    }
}
