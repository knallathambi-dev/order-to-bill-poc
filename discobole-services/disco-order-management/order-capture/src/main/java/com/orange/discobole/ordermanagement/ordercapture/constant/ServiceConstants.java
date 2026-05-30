// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.constant;

public class ServiceConstants {
    public static final String CONTRACT_PRODUCT_OFFERING_TYPE = "Contract";
    public static final String ATOMIC_PRODUCT_OFFERING_TYPE = "AtomicProductOffering";
    public static final String BUNDLE_PRODUCT_OFFERING_TYPE = "BundleProductOffering";
    public static final String LAUNCHED = "launched";
    public static final String ACTIVE = "active";
    public static final String QUALIFIED_STATUS = "qualified";
    public static final String RESERVED = "reserved";
    public static final String AVAILABLE = "available";
    public static final String ID_URI = "/{id}";
    public static final String AVAILABLE_RESOURCE_FILTER_URI = "?fields=id,href,value,@type,category,resourceStatus,name&resourceStatus=available&resourceSpecification.id={ids}";
    public static final String PRODUCT_INVENTORY_URI = "/productInventoryManagement/v1/product/";
    public static final String STATUS_URI = "/status";
    public static final String OPERATIONAL_STATUS_URI = "/operationalStatus";
    public static final String RESOURCE_INVENTORY_URI = "/resourceInventoryManagement/v1/resource/";
    public static final String RESOURCE_STATUS_URI = "/resourceStatus";
    public static final String BILLING_ACCOUNT_URI = "/billingAccount";
    public static final String PRODUCT_RELATIONSHIP_URI = "/productRelationship/-";
    public static final String PRODUCT_ORDER_ITEM_URI = "/productOrderItem/-";
    public static final String PRODUCT_PRICE_URI = "/productPrice/-";
    public static final String PRODUCT_NOT_FOUND = "Can't find a product with order Item id: %s";
    public static final String ID = "id";
    public static final String TYPE = "@type";
    public static final String PRODUCT = "product";
    public static final String RELATIONSHIP_TYPE = "relationshipType";
    public static final String PRODUCT_REF = "ProductRef";
    public static final String APPLICATION_JSON_PATCH_JSON = "application/json-patch+json";
    public static final String RESPONSE_STATUS_CODE = "Response Status Code: {}";
    public static final String RESPONSE_BODY = "Response Body: {}";
    public static final String REQUEST_RECEIVED = "Received {} request to {} with queryString {}";
    public static final String ERROR_PRINT_REQUEST_BODY = "Error while print request body {}";
    public static final String CHANNEL_REF_TYPE = "ChannelRef";
    public static final String RELATED_CHANNEL_TYPE = "RelatedChannel";
    public static final String ORDER_PRICE_TYPE = "OrderPrice";
    public static final String ORDER_ITEM_RELATIONSHIP_TYPE = "OrderItemRelationship";
    public static final String PRICE_TYPE = "Price";
    public static final String PRODUCT_RELATIONSHIP_TYPE = "ProductRelationship";
    public static final String PRODUCT_RELATIONSHIP_DELETE_URI = "/productRelationship/";
    public static final String PRODUCT_CHARACTERISTIC_URI =  "/productInventoryManagement/v1/product/%s/productCharacteristic/%d/value";
    private ServiceConstants() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }
}