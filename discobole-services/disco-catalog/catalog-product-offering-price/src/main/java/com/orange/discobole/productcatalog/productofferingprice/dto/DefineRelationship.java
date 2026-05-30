// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.dto;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.POPRelationshipType;
import com.orange.discobole.productcatalog.productofferingprice.pojo.TimePeriod;

public class DefineRelationship {
    private String id;
    private POPRelationshipType relationshipType;
    private TimePeriod validFor;

    @Override
    public String toString() {
        return "DefineRelationshipAction{" +
                "id='" + id + '\'' +
                ", relationshipType=" + relationshipType +
                ", validFor=" + validFor +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public POPRelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(POPRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public TimePeriod getValidFor() {
        return validFor;
    }

    public void setValidFor(TimePeriod validFor) {
        this.validFor = validFor;
    }
}
