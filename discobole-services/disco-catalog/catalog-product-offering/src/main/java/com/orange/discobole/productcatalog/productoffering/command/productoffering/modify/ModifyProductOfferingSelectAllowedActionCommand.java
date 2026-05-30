// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.AllowedProductAction;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

public class ModifyProductOfferingSelectAllowedActionCommand {


    @TargetAggregateIdentifier
    private final String productOfferingId;

    private final List<AllowedProductAction> selectAllowedAction;

    public ModifyProductOfferingSelectAllowedActionCommand(
            String productOfferingId,
            List<AllowedProductAction> selectAllowedAction) {
        this.productOfferingId = productOfferingId;
        this.selectAllowedAction = selectAllowedAction;
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public List<AllowedProductAction> getSelectAllowedAction() {
        return selectAllowedAction;
    }

    @Override
    public String toString() {
        return "ModifyProductOfferingSelectAllowedActionCommand{" +
                "productOfferingId='" + productOfferingId + '\'' +
                ", selectAllowedAction=" + selectAllowedAction +
                '}';
    }
}
