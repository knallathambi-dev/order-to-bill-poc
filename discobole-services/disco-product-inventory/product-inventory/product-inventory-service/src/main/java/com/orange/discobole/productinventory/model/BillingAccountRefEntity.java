// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * BillingAccount reference. A BillingAccount is a detailed description of a bill structure.
 **/

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BillingAccountRefEntity {
    private String id;
    private String href;
    private String name;
    private String ratingType;
    //TODO this property MUST be "BillingAccount"
    @JsonProperty("@referredType")
    @JsonAlias("atReferredType")
    private String atReferredType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BillingAccountRefEntity)) {
            return false;
        }
        BillingAccountRefEntity that = (BillingAccountRefEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


