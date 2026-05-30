// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.constant;

import com.orange.discobole.ordermanagement.ordercapture.pojo.spec.characteristic.operation.*;

public class OrderCaptureConstants {
    public static final String PICK_MAIN_OFFER_OR_CONTRACT_PRODUCT_CLASS = PickMainOfferOrContractProduct.class.getSimpleName();
    public static final String CONFIRM_CONFIGURATION_IS_PROCESSED_CLASS = ConfirmConfigurationIsProcessed.class.getSimpleName();
    public static final String PARTY_IDENTIFIER_CLASS = PartyIdentifier.class.getSimpleName();
    public static final String VALIDATE_ORDER_BY_CUSTOMER_CLASS = ValidateOrderByCustomer.class.getSimpleName();
    public static final String PAYMENT_REF_CLASS = PaymentRef.class.getSimpleName();
    public static final String BILLING_ACCOUNT_REF_CLASS = BillingAccountRef.class.getSimpleName();
    public static final String APPOINTMENT_REF_CLASS = AppointmentRef.class.getSimpleName();
    public static final String REFERRED_TYPE = "referredType";
    public static final String OPTIONAL_PRODUCT_OFFERING_ID = "optionalProductOfferingId";
    public static final String PRODUCT_OFFERING_ID = "productOfferingId";
    public static final String CONTRACT_PRODUCT_ID = "productId";
    public static final String RELATED_PARTY_ID = "relatedPartyId";
    public static final String RELATED_PARTY_ROLE = "relatedPartyRole";
    public static final String PROCESS_RELATED_ENTITY = "processRelatedEntity";
    public static final String PROCESS_RELATED_PARTY = "processRelatedParty";
    public static final String PROCESS_CHANNEL = "processChannel";
    public static final String TASK_CHANNEL = "taskChannel";
    public static final String TASK_RELATED_PARTY = "taskRelatedParty";
    public static final String DESCRIPTION_RECEIVED_PRODUCT_OFFERING_ELIGIBILITY_DESCRIPTION = "check offer eligibility for acquisition";
    public static final String DESCRIPTION_MODIFIED_PRODUCT_OFFERING_ELIGIBILITY_DESCRIPTION = "check offer eligibility for modification";
    public static final String CHANNEL_ID = "channelId";
    public static final String PRODUCT_ORDER_ITEM_LOGICAL_RESOURCES = "productOrderItemLogicalResources";
    public static final String PHYSICAL_PRODUCT_ITEM_ID_LIST = "physicalProductItemIdList";
    public static final int DEFAULT_QUANTITY_VALUE = 1;
    public static final String CUSTOMER = "customer";
    public static final String PROSPECT = "prospect";
    public static final String DEFAULT_ID = "1";
    public static final int DEFAULT_MIN_CARDINALITY = 1;
    public static final Integer DEFAULT_MAX_CARDINALITY = 1;
    public static final String OBJECT_NAME = "Object";
    public static final String CONFIGURATION_ID = "configurationId";
    public static final String CONFIGURATION_STATE = "configurationState";
    public static final String PRODUCT_ORDER_ID = "productOrderId";
    public static final String PARTY_ID = "partyId";
    public static final String PARTY_NAME = "partyName";
    public static final String PARTY_REFERRED_TYPE = "PartyReferredType";
    public static final String IS_PRODUCT_ORDER_INSTANTIATED = "isProductOrderInstantiated";
    public static final String IS_PRODUCT_ORDER_UPDATED = "isProductOrderUpdated";
    public static final String IS_CUSTOMER_OR_PROSPECT_PARTY_ROLE = "isCustomerOrProspectPartyRole";
    public static final String IS_NOT_CUSTOMER_AND_NOT_PROSPECT_PARTY_ROLE = "isNotCustomerAndNotProspectPartyRole";
    public static final String IS_PRODUCT_ORDER_ACCEPTED = "isProductOrderAccepted";
    public static final String ARE_CATALOG_DRIVEN_TASKS_SET = "areCatalogDrivenTasksSet";
    public static final String CREATED_PRODUCT_ORDER = "createdProductOrder";
    public static final String PRODUCT_ORDER_TYPE = "ProductOrder";
    public static final String RELATED_ENTITY = "relatedEntity";
    public static final String IS_COMPLETE_ORDER_REQUIRED = "isCompleteOrderRequired";
    public static final String ARE_COMPLETION_TASKS_SET = "areCompletionTasksSet";
    public static final String ARE_PARTY_ROLES_CHECKED = "arePartyRolesChecked";
    public static final String ARE_RESOURCES_RESERVED = "areResourcesReserved";
    public static final String RESOURCE_TYPE = "Resource";
    public static final String PAYMENT_TYPE = "Payment";
    public static final String BILLING_ACCOUNT_TYPE = "BillingAccount";
    public static final String APPOINTMENT_TYPE = "appointmentRef";
    public static final String PAYMENT_REF = "PaymentRef";
    public static final String BILLING_ACCOUNT_REF = "BillingAccountRef";
    public static final String APPOINTMENT_REF = "AppointmentRef";
    public static final String ORDER_ITEM = "OrderItem";
    public static final String ORDER_ITEM_PAYMENT_REF_MAP = "orderItemPaymentRefMap";
    public static final String ORDER_ITEM_BILLING_ACCOUNT_REF_MAP = "orderItemBillingAccountRefMap";
    public static final String ORDER_ITEM_APPOINTMENT_REF_MAP = "orderItemAppointmentRefMap";
    public static final String ARE_REFERENCES_VALIDATED = "areReferencesValidated";
    public static final String ARE_REFERENCES_ADDED = "areReferencesAdded";
    public static final String ARE_PRODUCTS_CREATED = "areProductsCreated";
    public static final String ARE_PRODUCTS_UPDATED = "areProductsUpdated";
    public static final String RELATED_PARTY_REFERRED_TYPE = "individual";
    public static final String RELATED_PARTY_TYPE = "RelatedParty";
    public static final String RELATED_PARTY = "relatedParty";
    public static final String PRODUCT_OFFERING_TYPE = "productOffering";
    public static final String PRODUCT_TYPE = "product";
    public static final String UNPAID_ORDER_ITEM_IDS = "unPaidOrderItemIds";
    public static final String ORDER_ITEM_IDS_REQUIRING_BA_REF = "orderItemIdsRequiringBARef";
    public static final String ORDER_ITEM_IDS_REQUIRING_APPOINTMENT_REF = "orderItemIdsRequiringAppointmentRef";
    public static final String ORDER_ITEM_ID = "orderItemId";
    public static final String ORDER_ITEM_ACTION = "orderItemAction";
    public static final String HAS_PARENT = "hasParent";
    public static final String BUNDLES = "bundles";
    public static final String IS_CUSTOMER_PARTY_ROLE_CREATED = "isCustomerPartyRoleCreated";
    public static final String IS_PROSPECT_PARTY_ROLE_CREATED = "isProspectPartyRoleCreated";
    public static final String VALID_PARTY_IDENTIFIER_REQUIRED = "Please provide a valid party identifier";
    public static final String IS_MODIFICATION_OR_TERMINATION_OR_MIGRATION_USE_CASE = "isModificationOrTerminationOrMigrationUseCase";
    public static final String RELIES_ON = "reliesOn";
    public static final String RELIES_FROM = "reliesFrom";
    public static final String STOCK_ITEM_TYPE = "StockItemType";
    public static final String PHYSICAL_PRODUCT_ORDER_ITEM_SERIAL_NUMBER_MAP = "physicalProductOrderItemSerialNumberMap";
    public static final String LOGICAL_RESOURCE = "LogicalResource";
    public static final String PHYSICAL_RESOURCE = "PhysicalResource";
    public static final String ORDER_TRIGGERING_THE_RESERVATION = "order triggering the reservation";
    public static final String ENTITY_REF = "EntityRef";
    public static final String RESERVE_PRODUCT_STOCK_ITEM = "ReserveProductStockItem";
    public static final String TIME_PERIOD = "TimePeriod";
    public static final String RESERVE_PRODUCT_STOCK = "ReserveProductStock";
    public static final String DATE_TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";
    public static final String ADD = "add";
    public static final String SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE = "ShippingProductSpecification";
    public static final String REQUIRES = "requires";
    public static final String REQUEST_ITEM = "requestItem";
    public static final String SETTING_CACHE = "setting";
    public static final String USER_ROLES_CACHE = "userRoles";
    public static final String SETTING_URL = "/v1/setting";
    public static final String REQUESTED_CONFIGURATION_ACTION = "requestedConfigurationAction";
    public static final String REQUESTED_DELIVERY_DATE = "Requested delivery date";
    public static final String RC_PRICE_TYPE = "recurringCharge";
    public static final String NRC_PRICE_TYPE = "nonRecurringCharge";
    public static final String SHIPMENT_PRODUCT = "ShipmentProduct";
    public static final String MODIFICATION = "modify";
    public static final String TERMINATION = "terminate";
    public static final String MIGRATE = "migrate";
    public static final String URL_FORMAT = "%s%s%s";
    public static final String POSTPAID = "postpaid";
    public static final String HYBRID = "hybrid";
    public static final String BUNDLES_MIGRATE = "bundlesMigrate";
    public static final String RELIES_ON_MIGRATE = "reliesOnMigrate";
    public static final String MIGRATE_FROM = "migrateFrom";
    public static final String ICCID = "ICCID";
    public static final String MSISDN = "MSISDN";
    public static final String IMSI = "IMSI";
    public static final String VOIP = "VOIP phone number";
    public static final String IS_APPOINTMENT_REQUIRED = "isAppointmentRequired";
    public static final String UNQUALIFIED = "unqualified";
    public static final String NO_CHANGE = "noChange";
    public static final String SERVICE_TYPE = "ServiceRef";
    public static final String DELETE = "delete";
    public static final String CONFIGURATION_PRICE_AT_TYPE = "ConfigurationPrice";



    private OrderCaptureConstants() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }
}