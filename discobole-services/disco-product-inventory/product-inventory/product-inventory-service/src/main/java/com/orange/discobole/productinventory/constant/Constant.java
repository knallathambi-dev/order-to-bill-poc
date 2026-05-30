// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.constant;


import com.orange.discobole.productinventory.dto.v1.Product;

import java.util.List;
import java.util.Map;

public final class Constant {
    public static final String PRODUCT_INVENTORY_MANAGEMENT_BASE_URL = "/productInventoryManagement/v1";
    public static final String JOB_SUB_PATH = "job";
    public static final String PRODUCT_SUB_PATH = "product";
    public static final String JOB_SPECIFICATION_SUB_PATH = "jobSpecification";

    public static final String FIELDS = "fields";

    public static final String LIMIT = "limit";

    public static final String SORT = "sort";

    public static final String OFFSET = "offset";

    public static final String NONE = "none";

    public static final String NULL = "null";

    public static final String ID = "id";

    public static final String HREF_FIELD = "href";
    public static final String PRODUCT_ORDER_HREF = "productOrderHref";

    public static final String VALUE = "value";

    public static final String PRODUCT_SPECIFICATION_REF = "ProductSpecificationRef";

    public static final int AVERAGE_PRODUCT_SIZE = 2048; // Average product size in bytes (2 KB)
    public static final int QUERY_LIMIT_MULTIPLIER = 2;  // Business logic multiplier
    public static final String REGEX = ".*\\s,|,\\s.*|.*\\s;|;\\s.*|^\\s.*|.*\\s$";
    
    public static final String PRODUCT_OFFERING = "Product Offering";

    public static final String PRODUCT_OFFERING_PRICE = "Product Offering Price";
    public static final String PRODUCT_CHARACTERISTIC = "productCharacteristic";

    public static final String LIFE_CYCLE_STATUS = "lifecycleStatus";
    public static final String STATUS = "status";
    public static final String NAME = "name";
    public static final String AT_TYPE = "@type";
    public static final String CREATION_DATE = "creationDate";

    public static final String WITH_IDS = " with id(s) : ";
    public static final String WITH_ID = " with id : ";


    public static final String STOCK_ITEM_TYPE = "StockItemType";
    public static final Map<String, String> NOT_PATCHABLE_ATTRIBUTES = Map.of(
            "id", "id",
            "href", "href",
            "creationDate", "creationDate",
            "@type", "atType",
            "startDate", "startDate",
            "statusChange", "statusChange",
            "operationalStatusChange", "operationalStatusChange");

    public static final List<String> REQUIRED_PRODUCT_FIELDS = List.of(Product.Fields.id, Product.Fields.href, Product.Fields.atType);

    public static final String CFS_SPEC = "CFSSpec";
    public static final String SHIPPING_PRODUCT_SPECIFICATION = "ShippingProductSpecification";
    public static final String INVENTORY_RESOURCE_TANGIBLE_REFERRED_TYPE = "Tangible";
    public static final String INVENTORY_RESOURCE_RESERVED_RESOURCE_STATUS = "reserved";
    public static final String VALIDITY_CHARACTERISTIC = "ValidityCharacteristic";
    public static final String OBJECT_CHARACTERISTIC = "ObjectCharacteristic";
    public static final String PRODUCT_STATE_CHANGE_EVENT_TOPIC = "disco.product-inventory.productStateChangeEvent-event";
    public static final String PRODUCT_ATTRIBUTE_VALUE_CHANGE_EVENT_TOPIC = "disco.product-inventory.productAttributeValueChangeEvent-event";
    public static final String DISCO_ORDER_MANAGEMENT_PRODUCT_ORDER_STATE_CHANGE_EVENT_TOPIC = "disco.order-management.productOrderStateChange-event";
    public static final String PRODUCT_DELETE_EVENT_TOPIC = "disco.product-inventory.productDeleteEvent-event";

    public static final String PRODUCT_CREATE_EVENT_TOPIC = "disco.product-inventory.productCreateEvent-event";

    public static final String EXPORT_PRODUCT_FILE_PREFIX  = "products-export-";
    public static final String UPLOAD_PRODUCT_FILE_PREFIX  = "products-upload-";
    public static final String BAD_URL_OR_RESOURCE_NOT_FOUND = "The requested URI or the requested resource does not exist.";
    public static final String JOB_SPECIFICATION = "JobSpecification";
    public static final String PRICE_TYPE_RECURRING = "recurringCharge";
    public static final String UNAUTHORIZED_PURGE_MESSAGE = "User is not authorized to perform the purge operation";
    public static final String COLLECTION_NAME = "products";
    public static final String PRODUCT_RELATIONSHIP = "productRelationship";
    public static final String PRODUCT = "product";
    public static final String _ID = "_id";

    public static final String UNITE_OF_MEASURE = "unitOfMeasure";
    public static final String VALID_TO = "validTo";
    public static final int BATCH_SIZE = 1000;
    public static final String PRODUCT_RELATIONSHIP_PATH = "/productRelationship/-";
    public static final String PRODUCT_CHARACTERISTIC_PATH = "/productCharacteristic/-";
    public static final String PRODUCT_PRICE_PATH = "/productPrice/-";

    public static final String DISCOUNT = "discountPriceAlteration";
    public static final String TAX = "TaxProductOfferingPriceAlteration";
    public static final String INSTALLMENT_CHARGE = "InstallmentCharge";

    private Constant() {
    }

}
