// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.orange.discobole.productinventory.exception.ProductInventoryException;

public enum ProductTypeEnum {
    PRODUCT("Product"),
    PHYSICAL_PRODUCT("PhysicalProduct"),
    SHIPMENT_PRODUCT("ShipmentProduct"),
    OFFER("Offer"),
    SIM_CARD("SimCard"),
    MOBILE_LINE("MobileLine"),
    SERVICE("Service"),
    PRODUCT_REF("ProductRef");
    private final String value;

    ProductTypeEnum(String value) {
        this.value = value;
    }


    @JsonCreator
    public static ProductTypeEnum fromValue(String type) throws ProductInventoryException {
        for (ProductTypeEnum relationshipEnum : ProductTypeEnum.values()) {
            if (relationshipEnum.value.equalsIgnoreCase(type)) {
                return relationshipEnum;
            }
        }
        throw new ProductInventoryException("Invalid product type value: " + type);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
