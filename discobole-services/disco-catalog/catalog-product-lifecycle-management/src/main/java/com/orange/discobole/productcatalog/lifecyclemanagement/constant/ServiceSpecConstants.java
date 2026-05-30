// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.constant;

/**
 * The ServiceSpecConstants type define constants for service specification.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class ServiceSpecConstants {

    private ServiceSpecConstants() {
    }

    /**
     * The constant STATECHANGE_EVENT.
     */
    public static final String STATECHANGE_EVENT = "ServiceSpecificationStateChange";
    /**
     * The constant ATTRIBUTEVALUECHANGE_EVENT.
     */
    public static final String ATTRIBUTEVALUECHANGE_EVENT = "ServiceSpecificationAttributeValueChange";

    public static final String STOCKITEMSTATECHANGE_EVENT = "StockItemStateChange";

    public static final String  STOCKITEMATTRIBUTEVALUECHANGE_EVENT = "StockItemAttributeValueChange" ;
}
