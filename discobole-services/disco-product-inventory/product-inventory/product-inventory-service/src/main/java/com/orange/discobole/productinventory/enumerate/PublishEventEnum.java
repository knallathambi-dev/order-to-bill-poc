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
import com.orange.discobole.productinventory.exception.ProductInventoryException;

public enum PublishEventEnum {

    PRODUCT_VALIDITY("CalculateProductValidity", "Product Validity"),

    ORDER_STATE_CHANGE("OrderStateChange", "Abort Product Order Rejected"),
    PRODUCT_STATE_CHANGE("ProductStateChange", "Product state change"),
    BATCH("Batch", "Terminate Product Validity Expire"),
    PRODUCT_DELETE_EVENT("DeleteProduct", "Product Delete Event"),
    PRODUCT_CREATE_EVENT("CreateProduct", "Product Create Event");

    private final String domain;
    private final String title;


    PublishEventEnum(String domain, String title) {
        this.title = title;
        this.domain = domain;

    }

    @JsonCreator
    public static PublishEventEnum fromStatus(String domain) throws ProductInventoryException {
        for (PublishEventEnum errorCodeEnum : PublishEventEnum.values()) {
            if (errorCodeEnum.domain.equalsIgnoreCase(domain)) {
                return errorCodeEnum;
            }
        }
        throw new ProductInventoryException("Invalid PublishEventEnum domain: " + domain);
    }


    @JsonCreator
    public static PublishEventEnum fromCode(String title) throws ProductInventoryException {
        for (PublishEventEnum errorCodeEnum : PublishEventEnum.values()) {
            if (errorCodeEnum.title.equalsIgnoreCase(title)) {
                return errorCodeEnum;
            }
        }
        throw new ProductInventoryException("Invalid PublishEventEnum title: " + title);
    }


    public String getDomain() {
        return domain;
    }

    public String getTitle() {
        return title;
    }
}