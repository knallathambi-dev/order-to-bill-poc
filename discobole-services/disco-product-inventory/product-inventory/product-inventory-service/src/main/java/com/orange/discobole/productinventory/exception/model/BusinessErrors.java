// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.exception.model;

public final class BusinessErrors {
    public static final String THE_PRODUCT_WITH_ID_S_DOES_NOT_EXIST = "The product with id %s does not exist";
    public static final String THE_JOB_SPECIFICATION_WITH_ID_S_DOES_NOT_EXIST = "The jobSpecification with id %s does not exist";
    public static final String THE_JOB_WITH_ID_S_DOES_NOT_EXIST = "The Job with id %s does not exist";
    public static final String THE_JOB_SPECIFICATION_WITH_ID_RELATED_TO_JOB_WITH_ID_DOES_NOT_EXIST = "The Job Specification with id %s related to job with id %s does not exist";
    public static final String THE_PRODUCTS_WITH_IDS_S_DOES_NOT_EXIST = "The products with ids %s does not exist";
    public static final String PRODUCT_REF_MISSING = "The id should be provided in the product with type ProductRef";
    public static final String THE_START_DATE_NOT_ADDED_IN_POST_REQUEST = "The start date should not be provided in POST request";
    public static final String THE_LIST_OF_PRODUCTS_SHOULD_NOT_BE_EMPTY = "The list of products should not be empty";
    public static final String THE_STATUS_SHOULD_BE_CREATED = "The status should be Created";
    public static final String THE_OPERATIONAL_STATUS_SHOULD_BE_CREATED = "The operational status should be created or confirmed";
    public static final String NULL_PRODUCT_OFFERING_NULL_PRODUCT_SPECIFICATION_CANNOT_ACCEPTED = "Null productOffering & null productSpecification cannot be accepted";
    public static final String NOT_INCLUDED_IN_PRODUCT_FIELDS = " not included in product fields";
    public static final String SUPPLEMENT_SPACES_CANNOT_BE_INCLUDED_ON_FIELDS = "Supplement spaces cannot be included on fields";
    public static final String OFFSET_AND_LIMIT_SHOULD_NOT_BE_NEGATIVE = "Offset and limit should not be negative";
    public static final String OFFSET_SHOULD_NOT_BE_NEGATIVE = "Offset should not be negative";
    public static final String LIMIT_SHOULD_NOT_BE_NEGATIVE = "Limit should not be negative";
    public static final String LIMIT_SHOULD_NOT_EXCEED = "Limit should not exceed max limit";
    public static final String ERROR_IN_OFFSET = "The offset provided exceeds the total number of available items.";
    public static final String VALUE_IS_NOT_A_VALID_TYPE = "%s value is not a valid type";
    public static final String THE_VALUE_OF_THE_KEY_SHOULD_NOT_BE_NULL = "The value of the key %s should not be null";
    public static final String THE_MAPPING_BETWEEN_THE_STATUS_AND_THE_OPERATIONAL_STATUS_IS_INVALID = "The mapping between the status %s and the operational status %s is invalid";
    public static final String THE_STATUS_CANNOT_BE_MODIFIED = "The status %s cannot be modified into %s";
    public static final String THE_OPERATIONAL_STATUS_CANNOT_BE_MODIFIED = "The operational status %s cannot be modified into %s";
    public static final String PRODUCT_CANNOT_BE_ON_RELATIONSHIP_WITH_THE_SAME_PRODUCT = "Product cannot be on relationship with the same product";
    public static final String SAME_PRODUCT_WITH_DIFFERENT_RELATIONSHIP_TYPES_IS_NOT_ALLOWED = "Duplication of products on relationship with specific product is not allowed";
    public static final String THIS_PRODUCT_IS_ALREADY_ON_RELATIONSHIP_WITH_THE_PRODUCT = "This product is already on relationship with the product of id= %s";
    public static final String PRODUCT_STATUS_CANNOT_BE_TERMINATED = "Product with Id : %s can not be terminated because product inner Relationship with id: %s not in a final status";
    public static final String PRODUCT_STATUS_CANNOT_BE_ACTIVE = "Product with Id : %s  cannot be activated because it must have at least one active inner relationship ";
    public static final String PRODUCT_STATUS_CANNOT_BE_SOLD = "Product with Id : %s  cannot be Sold because it needs to have at least one sold inner relationship ";
    public static final String INVALID_TERMINATION_DATE = "Invalid termination date";
    public static final String ALREADY_HAVE_START_DATE = "Start date cannot be changed once set";
    public static final String STATUS_OF_THE_RELATED_PRODUCT_IS_INVALID = "The status of the related product is invalid";
    public static final String THE_OPERATIONAL_STATUS_OF_THE_RELATED_PRODUCT_IS_INVALID = "The operational status of the related product is invalid";
    public static final String ONE_OR_MORE_OPERATION_FIELDS_OP_MUST_BE_VERIFIED = "One or more operation fields (op) must be verified";
    public static final String ONE_OR_MULTIPLE_ISSUES_ARE_EXIST_IN_THE_PATH_OR_THE_VALUE_FIELDS = "One or multiple issues are exist in the path or the value fields";
    public static final String PATCH_METHOD_NOT_SUPPORTED_BY_THAT_RESOURCE = "PATCH method not supported by that resource <%s>";
    public static final String INVALID_STATUS_SOLD_FOR_PRODUCT_TYPE = "Invalid status Sold for product type %s";
    public static final String SHIPMENT_PRODUCT_MISSING_RELIES_ON_RELATIONSHIP_WITH_PHYSICAL_PRODUCT = "ShipmentProduct %s missing relies on relationship with physicalProduct";
    public static final String INVALID_RELATIONSHIP = " has invalid relationship that doesn't exists in catalog with id %s";
    public static final String DOESN_T_EXIST_IN_CATALOG = " doesn't exist in catalog";
    public static final String RESOURCE_DOESN_T_EXIST = "Inventory Resource with id %s doesn't exist";
    public static final String RESOURCE_NOT_RESERVED = "Inventory Resource with id %s status is not reserved";
    public static final String RESOURCE_SERIAL_NUMBER_DIFFERENT = "Inventory Resource with id %s serial number %s is different from %s";
    public static final String RESOURCE_NOT_TANGIBLE = "Inventory Resource with id %s referredType is not Tangible";
    public static final String INTERNAL_SERVER_ERROR = "Internal server Error";
    public static final String INCORRECT_VALUE_TYPE = "An incorrect value type has been included during the filtering process for ";
    public static final String INVALID_PRODUCT = "Invalid product";
    public static final String EMPTY_PRODUCT_ORDER_ID_DETECTED = "Empty product order ID detected";
    public static final String EMPTY_ORDER_ITEM_ID_DETECTED = "Empty order item ID detected";
    public static final String PRODUCT_OFFERING_ID_CANNOT_BE_EMPTY = "Product offering id cannot be empty";
    public static final String EMPTY_PRODUCT_SPECIFICATION_ID_DETECTED = "Empty product specification ID detected";
    public static final String EMPTY_PRODUCT_OFFERING_PRICE_ID_DETECTED = "Empty product offering price ID detected";
    public static final String TANGIBLE_PRODUCT_CAN_NOT_BE_INSTANTIATED_AT_THIS_LEVEL = "Tangible Product can not be instantiated at this level: %s";
    public static final String EMPTY_PRODUCT_SERIAL_NUMBER = "Empty product serial number";
    public static final String PRODUCT_SPECIFICATION_HAS_NOT_STOCK_ITEM_TYPE_IN_SUPPORT_ENTITY = "Product Specification with id :  %s does not have stock item type in support Entity";
    public static final String TANGIBLE_PRODUCT_REALIZING_RESOURCE_ID_CANNOT_BE_NULL = "Tangible Product Realizing Resource id cannot be null";
    public static final String TANGIBLE_PRODUCT_REALIZING_RESOURCE_CANNOT_BE_NULL = "Tangible Product Realizing Resource cannot be null";
    public static final String PRODUCT_SPECIFICATION_HAS_NOT_CFS_SPEC_OR_SHIPMENT_PRODUCT_SPECIFICATION = "Product Specification with id :  %s does not have CFS Spec type in support entity or shipping product specification in @baseType";
    public static final String SHIPMENT_PRODUCT_CAN_NOT_BE_INSTANTIATED_AT_THIS_LEVEL = "Shipment Product can not be instantiated at this level: %s";
    public static final String ONLY_ONE_OCCURRENCE_IS_ACCEPTED_FOR_VALIDITY_CHARACTERISTIC = "Only one occurrence is accepted for validity characteristic";
    public static final String PRODUCT_MUST_NOT_BE_NULL = "Product must not be null";
    public static final String PRODUCT_ID_MUST_NOT_BE_NULL = "Product ID must not be null";
    public static final String PRODUCT_TYPE_MUST_NOT_BE_NULL = "Product @type must not be null";
    public static final String PRODUCT_STATUS_MUST_NOT_BE_NULL = "Product status must not be null";
    public static final String NOT_BE_EMPTY = " must not be empty";
    public static final String INVALID_FORMAT_FOR_PRODUCT_RELATIONSHIP_ID = "Invalid format for product relationShip product.id";
    public static final String FAILED_TO_PROCESS_PRODUCT = "Failed to process product inventory";
    public static final String INVALID_FORMAT_FOR_DATE_TIME_PRODUCT_CHARACTERISTIC = "Invalid format for dateTime product characteristic";
    public static final String CANNOT_DELETE_ALREADY_STARTED_JOB_SPECIFICATION = "Cannot delete already started jobSpecification";
    public static final String CANNOT_DOWNLOAD_FILE = "Cannot get download link because jobSpecification status is not done, actual status : %s";
    public static final String UNSUPPORTED_FILTER = "Unsupported filter parameter: ";
    public static final String INVALID_MODIFICATION_USE_CASE = "Modification use case should have one product relationship equal to has_parent";
    public static final String JOB_SPECIFICATION_NOT_FOUND = "JobSpecification with Id %s not found";
    public static final String START_SCHEDULE_CANNOT_BE_LATER_THAN_END_SCHEDULE = "Start Schedule cannot be later than end Schedule";
    public static final String PLANNED_DATE_CANNOT_BE_IN_THE_PAST = "Planned Date cannot be in the past";
    public static final String FIELD_MUST_NOT_BE_NULL = "%s must not be null";
    public static final String JOB_SCHEDULE_MUST_NOT_BE_NULL = "Job Schedule must not be null";
    public static final String CREATED_PRODUCT_ATTEMPTED_TO_BE_MODIFIED_TO_ABORTED_INSTEAD_OF_TERMINATED = "product id: %s with status created and status is attempted to be modified to aborted instead of terminated.";
    public static final String PRODUCT_WITH_INVALID_STATUS_CAN_NOT_BE_TERMINATED = "product id: %s with invalid status can not be terminated.";
    public static final String FREQUENCY_AMOUNT_CANNOT_BE_MORE_THAN_24_FOR_PERIOD_TYPE_HOUR = "Frequency amount cannot be more than 24 for period type hour";

    public static final String WHEN_PRICETYPE_IS_RECURRING_RECURRINGCHARGEPERIOD_FIELD_SHOULD_NOT_BE_NULL = "When priceType is Recurring recurringChargePeriod field should not be null";
    public static final String WHEN_PRICETYPE_IS_RECURRING_RECURRINGCHARGEPERIOD_FIELD_SHOULD_NOT_BE_EMPTY = "When priceType is Recurring recurringChargePeriod field should not be empty";
    public static final String DOCUMENT_COUNT_EXCEEDED_PAGINATION_LIMIT = "Offset exceeds the maximum allowed pagination limit of %d";
    public static final String PURGE_QUERY_CANNOT_BE_EMPTY = "Purge Query cannot be empty";
    public static final String INVALID_VALUE_OF_PURGE_TYPE = "%s field must be one of the following values: %s.";
    public static final String ALL_STATUS_VALUES_SHOULD_BE_IN = "All status values should be one of this list: %s.";
    public static final String ALL_LIFE_CYCLE_STATUS_VALUES_SHOULD_BE_IN = "All lifecycleStatus values should be one of this list: %s.";
    public static final String THE_QUERY_SHOULD_INCLUDE_AT_LEAST_ON_OF_THESE_STATUS_VALUES = "The query should include at least one of these %s values: %s.";
    public static final String THE_PROVIDED_QUERY_STRING_IS_INVALID = "The provided query string is invalid";
    public static final String INVALID_DATE_FORMAT_EXPECTED_ISO_8601_FORMAT = "Invalid date format, expected ISO 8601 format.";
    public static final String FIELD_S_IS_NOT_A_VALID_DATE_OR_DATETIME_FIELD = "Field %s is not a valid date or datetime field.";
    public static final String INVALID_FILTER_FORMAT = "Invalid filter format, should be 'variableName=value'";
    public static final String INVALID_FILTER_VALUE_FOR_ENUM = "Invalid filter value '%s' for enum class '%s'";

    public static final String TERMINATION_JOB_SUPPORTS_ONLY_IMMEDIATE_OR_ONE_TIME_JOB_SCHEDULER = "Termination job specification supports only Immediate or One Job Scheduler";
    public static final String EXCLUSION_FOR_VALUE_UNITE_OF_MEASURE_VALID_TO = "Either 'value' and 'unitOfMeasure' must be present together or 'validTo', but not both";
    public static final String VALID_TO_DATE_ERROR_MESSAGE = "ValidTo must be greater than or equal to the current date";
    public static final String INVALID_FORMAT_DATE = "Invalid 'validTo' date format";
    public static final String UNITE_OF_MEASURE_MUST_BE_ONE_OF = "unitOfMeasure must be one of:";
    public static final String THE_CREATION_DATE_NOT_ADDED_IN_POST_REQUEST = "The creation date should not be provided in POST request";
    public static final String THE_LIFE_CYCLE_STATUS_CHANGE_NOT_ADDED_IN_POST_REQUEST = "The life cycle status change should not be provided in POST request";
    public static final String THE_ACTIVE_PERIOD_NOT_ADDED_IN_POST_REQUEST = "The active period should not be provided in POST request";
    public static final String IMPORT_JOB_SUPPORTS_ONLY_IMMEDIATE_OR_ONE_TIME_JOB_SCHEDULER = "Import job specification supports only Immediate or One Time Job Scheduler";
    public static final String CANNOT_UPLOAD_FILE = "Cannot get upload link because job status is not NotStarted, actual status : %s";
    public static final String JOB_MISSING_FILE_NAME = "Import Job is missing fileName";
    public static final String IMPORT_JOB_MISSING_HAS_EXPIRED = "Import Job has expired";
    public static final String IMPORT_JOB_MISSING_FILE = "Import Job File is not ready";
    public static final String VALUE_REQUIRED_FOR_NON_REMOVE_OPERATION = "The 'value' field must not be null for operation '%s' on path '%s'.";
    public static final String CANNOT_ACCESS_THIS_RESOURCE = "You are not authorized to access this resource (Invalid Related Party Id).";
    public static final String PRICE_TYPE_IS_MANDATORY_EXCEPT_FOR_NON_INSTALLMENT_CHARGE = "Missing Price Type for non-InstallmentCharge ProductPrice";
    private BusinessErrors() {
    }
}
