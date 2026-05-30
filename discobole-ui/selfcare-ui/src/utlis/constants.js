// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export const SHIPPING_PRODUCT_SPECIFICATION_BASE_TYPE = "ShippingProductSpecification";

export const CONTRACT_TYPE = "Contract";

export const ATOMIC_TYPE = "AtomicProductOffering";

export const BUNDLE_TYPE = "BundleProductOffering";

export const ADDRESS_CHARACTERISTIC_TYPE = 'AddressCharacteristic';

export const TARGET_QUERY_PRODUCT_CONFIGURATION_ITEM = "TargetQueryProductConfigurationItem";

export const SOURCE_QUERY_PRODUCT_CONFIGURATION_ITEM = "SourceQueryProductConfigurationItem";

export const ORDER_CAPTURE_SELECT_OFFER = "OrderCapture.selectOfferOrContract";
export const ORDER_CAPTURE_VALIDATE_ORDER = "OrderCapture.validateOrder";

export const ORDER_STATUSES = {
    acknowledged: 'Acknowledged',
    cancelled: 'Cancelled',
    completed: 'Completed',
    held: 'Held',
    inProgress: 'In progress',
    draft: 'Draft',
    accepted: 'Accepted',
    partial: 'Partial',
    rejected: 'Rejected',
    failed: 'Failed',
    pendingCancellation: 'Pending cancellation',
    assessingCancellation: 'Assessing cancellation',
    pending: 'Pending',
    terminated: 'Terminated'
};

export const PRICE_TYPES = {
    NON_RECURRING_CHARGE: "nonRecurringCharge",
    RECURRING_CHARGE: "recurringCharge"
};

export const ALTERATION_TYPES = {
    NON_RECURRING_DISCOUNT: 'nonRecurringDiscount',
    RECURRING_DISCOUNT: 'recurringDiscount',
    TAX: 'TaxProductOfferingPriceAlteration',
};

export const STATIC_BILLING_IDS = {
    lisa: "13aZ81kLmN0pQrStUvWx",
    homer: "13Xy7Bv3TnP6sUdEfGhJ",
};

export const UNIT_OF_MEASURE_INTERVAL = ["sms", "hour", "hours", "gb"];

export const COLOR_KEYWORDS = ["colour", "color", "couleur"];

export const INSTALLMENT_CHARGE_TYPE = "InstallmentCharge";

export const UPFRONT_PAYMENT_VALUE = "Upfront Payment";

export const INSTALLMENT_PERIOD_CHARACTERISTIC_NAME = "Installment Period";

export const PARTNER_CHARACTERISTIC_NAME = "Partner";

export const UNQUALIFIED_OFFER_MESSAGE = "The selected offer is unqualified";