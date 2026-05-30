// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.constant;

public class DescriptionConstants {
    public static final String SELECTED_OFFER_NOT_VALID = "The selected offer is not valid";
    public static final String CONTRACT_SELECTED_OFFER = "The selected offer is contract";
    public static final String ACCESSORY_SELECTED_OFFER = "The selected offer is an accessory";
    public static final String LOGICAL_SELECTED_OFFER = "The selected offer is a logical offer";
    public static final String SELECTED_OFFER_UNQUALIFIED = "The selected offer is unqualified";
    public static final String CONFIGURED_OFFER_UNQUALIFIED = "The configured offer is unqualified, please restart configuration with a new offer";
    public static final String QUALIFICATION_SERVICE_UNREACHABLE = "The product qualification service is currently unreachable, please try later";
    public static final String QUALIFICATION_NOT_FULFILLED = "Qualification is not fulfilled, please try again";
    public static final String SELECTED_PRODUCT_DOES_NOT_EXIST = "The selected product does not exist";
    public static final String SELECTED_PRODUCT_ORDER_DOES_NOT_EXIST = "The selected product order does not exist";
    public static final String SELECTED_PRODUCT_NOT_VALID = "The selected product is not valid";
    public static final String REQUIRED_RESOURCES_NOT_AVAILABLE = "The required resources are not available";
    public static final String VALID_PRODUCT_ORDER_IDENTIFIER_REQUIRED = "Please provide a valid product order identifier";
    public static final String VALID_CONFIGURATION_IDENTIFIER_REQUIRED = "Please provide a valid configuration identifier";
    public static final String VALID_PARTY_IDENTIFIER_REQUIRED = "Please provide a valid party identifier";
    public static final String PRODUCT_INSTANTIATION_ERROR = "An error has been encountered during the product instantiation step, the error is likely to be resolved shortly";
    public static final String PARTY_MANAGEMENT_SERVICE_UNREACHABLE = "The party management service is currently unreachable, please try later";
    public static final String PRODUCT_OFFERING_PRICE_SERVICE_UNREACHABLE = "The product offering price service is currently unreachable, please try later";
    public static final String CATALOG_SERVICE_UNREACHABLE = "The catalog service is currently unreachable, please try later";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error, please try later";
    public static final String PRODUCT_UPDATE_ERROR = "An error has been encountered during the product update step, the error is likely to be resolved shortly";
    public static final String PRODUCT_CONFIGURATOR_SERVICE_UNREACHABLE = "The product configurator service is currently unreachable, please try later";
    public static final String RESOURCES_CANCELLATION_ERROR = "An error has been encountered during the resources cancellation step, the error is likely to be resolved shortly";
    public static final String VALID_PAYMENT_REFERENCE_REQUIRED = "Please provide a valid payment reference(s)";
    public static final String INVALID_BILLING_ACCOUNT_REFERENCE = "The provided billing account reference %s is not valid";
    public static final String INVALID_PRODUCT_SPECIFICATION_REFERENCE = "The provided product specification reference %s is not valid";
    public static final String INVALID_PRODUCT_STOCK_REFERENCE = "The provided product stock reference %s is not valid";
    public static final String INVALID_AVAILABLE_RESOURCE_REFERENCE = "The provided available resource reference %s is not valid";
    public static final String INVALID_RELATED_PARTY_ID_FOR_BILLING_ACCOUNT = "The provided related party ID for retrieving the billing account %s is not valid";
    public static final String PAYMENT_SERVICE_UNREACHABLE = "The payment service is currently unreachable, please try later";
    public static final String RESOURCE_INVENTORY_SERVICE_UNREACHABLE = "The resource inventory service is currently unreachable, please try later";
    public static final String PRODUCT_STOCK_SERVICE_UNREACHABLE = "The product stock service is currently unreachable, please try later";
    public static final String ACCOUNT_SERVICE_UNREACHABLE = "The account service is currently unreachable, please try later";
    public static final String PRODUCT_SPECIFICATION_SERVICE_UNREACHABLE = "The product specification service is currently unreachable, please try later";
    public static final String ERROR_RETRIEVING_PRODUCT_CONFIGURATION_ITEMS = "Error retrieving product configuration items";
    public static final String INVALID_ID_FOR_APPOINTMENT = "The provided appointment reference %s is not valid";
    public static final String APPOINTMENT_SERVICE_UNREACHABLE = "The appointment service is currently unreachable, please try later";
    public static final String TECHNICAL_ELIGIBILITY_DESCRIPTION = "The selected area is not currently covered by fiber optic services, Please select a different offer";
    public static final String ADDRESS_ID_REQUIRED_DESCRIPTION = "The address is required to check the technical eligibility";
    public static final String SERVICE_QUALIFICATION_MANAGEMENT_SERVICE_UNREACHABLE = "The eligibility management service is currently unreachable, please try later";
    public static final String VALIDITY_CHARACTERISTIC_DATE_ERROR = "Cannot change the date of validityCharacteristic :ValidTo";
    public static final String CANNOT_ACQUIRE_OFFER_WITH_INSTALLMENT = "you cannot acquire an offer with an installment option due to your current debt. Please select the upfront option and proceed";
    private DescriptionConstants() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }
}