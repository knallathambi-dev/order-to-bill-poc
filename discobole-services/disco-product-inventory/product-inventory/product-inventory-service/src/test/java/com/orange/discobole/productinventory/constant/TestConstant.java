// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.constant;

public class TestConstant {
    public static final String PRODUCT_OFFERING_URL = "/productCatalogManagement/v1/productOffering";
    public static final String RESOURCE_INVENTORY_MANAGEMENT = "/api/resourceInventoryManagement/v1/resource";
    public static final String VALID_REALIZING_RESOURCE_SERIAL_NUMBER = "5678";
    public static final String VALID_REALIZING_RESOURCE_ID = "65x";
    public static final String NON_TANGIBLE_REALIZING_RESOURCE_ID = "67x";
    public static final String REALIZING_RESOURCE_ID_WITH_INVALID_STATUS = "68x";
    public static final String VALID_ATOMIC_PRODUCT_OFFERING_ID = "95023bc2-151d-4bf2-bb87-1b7d6cd2e847";
    public static final String VALID_ATOMIC_PRODUCT_OFFERING_ID_WITH_CORRECT_RELATIONSHIP_WITH_CONTRACT = "c2c07f99-a99b-4e0d-9eff-eb76ad4600af";
    public static final String VALID_PRODUCT_OFFERING_CONTRACT_ID_WITH_CORRECT_RELATIONSHIP_WITH_ATOMIC_PRODUCT_OFFERING = "772056cd-485b-4877-8453-1648a07c43f2";

    public static final String VALID_TANGIBLE_ATOMIC_PRODUCT_OFFERING_ID  = "8f3239c1-fad8-4936-88cf-49c2cd6b6bf7";
    public static final String VALID_PRODUCT_OFFERING_CONTRACT_ID = "772056cd-485b-4877-8453-1648a07c43f7";
    public static final String VALID_BUNDLE_PRODUCT_OFFERING_ID = "414e3b5e-7fca-4b7e-bf73-51fa1e66b87f";
    public static final String NOT_VALID_PRODUCT_OFFERING_ID = "c8846e4c-ecca-40b2-b7de-9c130f4f34661";
    public static final String VALID_SHIPMENT_ATOMIC_PRODUCT_OFFERING_ID = "78bc2297-cb07-4161-babb-35fc6b55979f";
    public static final String VALID_PRODUCT_OFFERING_IDS = "772056cd-485b-4877-8453-1648a07c43f7,772056cd-485b-4877-8453-1648a07c43f7";

    public static final String EMPTY_ID = "";
    public static final String NULL_ID = "";
    public static final String PRODUCT_OFFERING_JSON = "src/test/resources/json/productOffering.json";
    public static final String RESOURCE_INVENTORY_MANAGEMENT_VALID_JSON_FILE_PATH = "src/test/resources/json/inventoryManagement/validTangibleProduct.json";
    public static final String RESOURCE_INVENTORY_MANAGEMENT_INVALID_RESOURCE_STATUS_JSON_FILE_PATH = "src/test/resources/json/inventoryManagement/invalidResourceStatusTangibleProduct.json";
    public static final String RESOURCE_INVENTORY_MANAGEMENT_INVALID_TYPE_JSON_FILE_PATH = "src/test/resources/json/inventoryManagement/invalidTypeTangibleProduct.json";
    public static final String PRODUCT_SPECIFICATION_URL = "/productCatalogManagement/v1/productSpecification";
    public static final String VALID_PRODUCT_SPECIFICATION_ID = "63d24f7e-c529-404c-911b-7c7926fd12ff";
    public static final String VALID_TANGIBLE_PRODUCT_SPECIFICATION_ID = "14cf641e-eb3c-4007-be27-c57d22532fa7";

    public static final String VALID_PRODUCTS_SPECIFICATION_IDS = "3d24f7e-c529-404c-911b-7c7926fd12ff,14cf641e-eb3c-4007-be27-c57d22532fa7";
    public static final String VALID_SHIPMENT_PRODUCT_SPECIFICATION_ID = "3e828676-7345-4ac9-92f3-7606857fa588";
    public static final String NOT_VALID_PRODUCT_SPECIFICATION_ID = "82f3bab6-63a1-4008-96a3-8d411d0c5b381";
    public static final String INVALID_SHIPMENT_PRODUCT_SPECIFICATION_ID = "82f3bab6-63a1-4008-96a3-8d411d0c5b381";
    public static final String PRODUCT_SPECIFICATION_JSON = "src/test/resources/json/productSpecification.json";
    public static final String PRODUCTS_SPECIFICATION_JSON = "src/test/resources/json/productsSpecification.json";

    public static final String TANGIBLE_PRODUCT_SPECIFICATION_JSON = "src/test/resources/json/tangibleProductSpecification.json";
    public static final String TANGIBLE_PRODUCT_WITHOUT_STOCK_ITEM_JSON = "src/test/resources/json/tangibleProductSpecificationWithoutStockItem.json";
    public static final String SHIPMENT_PRODUCT_SPECIFICATION_WITHOUT_AT_BASE_TYPE_JSON = "src/test/resources/json/shipmentProductSpecificationWithoutAtBaseType.json";
    public static final String SHIPMENT_PRODUCT_SPECIFICATION_JSON = "src/test/resources/json/shipmentProductSpecification.json";

    public static final String PRODUCT_OFFERING_PRICE_URL = "/productCatalogManagement/v1/productOfferingPrice";
    public static final String VALID_PRODUCT_OFFERING_PRICE_ID = "ea2beca7-9ec1-4a05-a84d-2bbbef11b8b5";
    public static final String NOT_VALID_PRODUCT_OFFERING_PRICE_ID = "ea2beca7-9ec1-4a05-a84d-2bbbef11b8b51";
    public static final String PRODUCT_OFFERING_PRICE_JSON = "src/test/resources/json/productOfferingPrice.json";
    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT = "/productInventoryManagement/v1/product";
    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_EXPORT = "/productInventoryManagement/v1/product/export";

    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION = "/productInventoryManagement/v1/jobSpecification";
    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB = "/productInventoryManagement/v1/job";
    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_S_JOB = "/productInventoryManagement/v1/jobSpecification/%s/Job";
    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_PRODUCT_BY_ID_URI = "/productInventoryManagement/v1/product/%s";
    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION_BY_ID_URI = "/productInventoryManagement/v1/jobSpecification/%s";
    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_BY_ID_URI = "/productInventoryManagement/v1/job/%s";
    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_DOWNLOAD_LINK_URI = "/productInventoryManagement/v1/job/%s/exportFileInformation";
    public static final String STATUS_V_1_SERVICE_STATUS_URI = "/status";
    public static final String VERSION_V_1_SERVICE_VERSION_URI = "/version";
    public static final String CONFIGURATIONS_V_1_SERVICE_VERSION_URI = "/configuration";

    public static final String APPLICATION_JSON_PATCH_PATCH = "application/json-patch+json";
    public static final String APPLICATION_MERGE_PATCH_JSON = "application/merge-patch+json";
    public static final String APPLICATION_JSON = "application/json";
    public static final Integer STRING_SIZE = 10;
    public static final Float AMOUNT = 10F;
    public static final String DEFAULT_PRODUCT_ORDER_ID = "PRODUCT_ORDER_ID";
    public static final String DEFAULT_PRODUCT_ORDER_ITEM_ID = "PRODUCT_ORDER_ITEM_ID";
    public static final String PRODUCT_INVENTORY_MANAGEMENT_V_1_GET_UPLOAD_FILE_URL = "/productInventoryManagement/v1/job/uploadFileUrl/%s";

    public static final String INVALID_PRODUCT_ID = "66aa52851f65664292b37139";
}
