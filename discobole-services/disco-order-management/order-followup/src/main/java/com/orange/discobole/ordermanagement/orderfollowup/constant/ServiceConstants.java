// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.constant;

public class ServiceConstants {
    public static final String CONTRACT_TYPE = "Contract";
    public static final String PRODUCT_INVENTORY_URI = "/productInventoryManagement/v1/product/";
    public static final String STATUS_URI = "/status";
    public static final String ID_URI = "/{id}";
    public static final String OPERATIONAL_STATUS_URI = "/operationalStatus";
    public static final String APPLICATION_JSON_PATCH_JSON = "application/json-patch+json";
    public static final String RESPONSE_STATUS_CODE = "Response Status code: {}";
    public static final String RESPONSE_BODY = "Response Body: {}";
    public static final String BUNDLES = "bundles";
    public static final String ROOT_PRODUCT = "rootProduct";
    public static final String ADD = "add";
    public static final String PRODUCT_PRICE_URI = "/productPrice/";
    public static final String PRODUCT_PRICE_ALTERATION_URI = "/productPriceAlteration/";
    public static final String VALID_FOR_URI = "/validFor";
    public static final String BUNDLES_MIGRATE = "bundlesMigrate";
    public static final String RELIES_ON = "reliesOn";
    public static final String RELIES_FROM = "reliesFrom";
    public static final String RELIES_ON_MIGRATE = "reliesOnMigrate";
    public static final String MIGRATE = "migrate";
    public static final String NO_CHANGE = "noChange";
    public static final String MODIFY = "modify";
    public static final String MIGRATE_FROM = "migrateFrom";
    public static final String ATOMIC_PRODUCT_OFFERING = "AtomicProductOffering";
    public static final String BUNDLE_PRODUCT_OFFERING = "BundleProductOffering";
    public static final String PRODUCT_TERM = "/productTerm/";
    public static final String DELETE = "delete";

    private ServiceConstants() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }
}