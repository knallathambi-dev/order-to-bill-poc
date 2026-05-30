// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.PolicyRuleRef;

import java.util.List;

public class ModifyProductOfferingPolicyRuleAssociationCommand {

    @TargetAggregateIdentifier
    private final String productOfferingId;
    private final List<PolicyRuleRef> policyRuleRef;

    public ModifyProductOfferingPolicyRuleAssociationCommand(String productOfferingId,
                                                       List<PolicyRuleRef> policyRuleRef) {
        this.productOfferingId = productOfferingId;
        this.policyRuleRef = policyRuleRef;
    }

    @Override
    public String toString() {
        return "ModifyProductOfferingPolicyRuleAssociationCommand{" +
                "productOfferingId='" + productOfferingId + '\'' +
                ", policyRuleRef=" + policyRuleRef +
                '}';
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public List<PolicyRuleRef> getPolicyRuleRef() {
        return policyRuleRef;
    }
}
