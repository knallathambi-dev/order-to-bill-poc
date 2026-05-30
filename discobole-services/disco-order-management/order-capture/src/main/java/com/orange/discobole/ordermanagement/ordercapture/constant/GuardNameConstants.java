// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.constant;

public class GuardNameConstants {
    public static final String ACQUISITION_ELIGIBILITY_GUARD = "isEligibleAcquisitionGuard";
    public static final String MODIFICATION_ELIGIBILITY_GUARD = "isEligibleModificationGuard";
    public static final String PARTY_IDENTIFICATION_ON_PATCH_GUARD = "isPartyIdentifiedOnPatchGuard";
    public static final String PARTY_IDENTIFICATION_ON_POST_GUARD = "isPartyIdentifiedOnPostGuard";
    public static final String ELIGIBILITY_CHECKER_GUARD = "isEligibilityPerformedGuard";
    public static final String PRODUCT_ORDER_INSTANTIATION_GUARD = "isProductOrderInstantiatedGuard";
    public static final String PRODUCT_ORDER_UPDATE_GUARD = "isProductOrderUpdatedGuard";
    public static final String PRODUCT_ORDER_ACCEPTED_GUARD = "isProductOrderAcceptedGuard";
    public static final String INSTALLED_BASE_GUARD = "isInstalledBaseCreatedGuard";
    public static final String COMPLETION_TASKS_GUARD = "areCompletionTasksSetGuard";
    public static final String CATALOG_DRIVEN_TASKS_GUARD = "isCatalogDrivenTasksSetGuard";
    public static final String RESERVATION_REQUIREMENT_GUARD = "isReservationNeededGuard";
    public static final String RESOURCES_RESERVATION_GUARD = "areResourcesReservedGuard";
    public static final String ORDER_COMPLETION_GUARD = "isOrderCompletedGuard";
    public static final String ACQUISITION_USE_CASE_CHECKER_GUARD = "isAcquisitionUseCaseGuard";
    public static final String MODIFICATION_OR_TERMINATION_USE_CASE_CHECKER_GUARD = "isModificationOrTerminationUseCaseGuard";
    public static final String PRODUCTS_UPDATE_GUARD = "productsUpdateGuard";
    public static final String EXISTENCE_PRODUCT_OFFERING_GUARD = "isExistProductOfferingGuard";
    public static final String PRODUCT_OFFERING_PROVIDED_ON_PATCH_GUARD = "isProductOfferingProvidedOnPatchGuard";
    public static final String EXISTENCE_PRODUCT_IDENTIFIER_GUARD = "isExistProductIdentifierGuard";
    public static final String VALIDATION_PRODUCT_IDENTIFIER_GUARD = "isValidProductIdentifierGuard";
    public static final String EXISTENCE_PRODUCT_OFFERING_ID_ON_MODIFICATION_GUARD = "isExistProductOfferingIdOnModificationGuard";
    public static final String VALID_PRODUCT_ORDER_ID_GUARD = "isValidProductOrderIdGuard";
    public static final String PROSPECT_PARTY_ROLE_GUARD = "isProspectPartyRoleGuard";
    public static final String CUSTOMER_OR_PROSPECT_PARTY_ROLE_GUARD = "isCustomerOrProspectPartyRoleGuard";
    public static final String NOT_CUSTOMER_AND_NOT_PROSPECT_PARTY_ROLE_GUARD = "isNotCustomerAndNotProspectPartyRoleGuard";
    public static final String CUSTOMER_PARTY_ROLE_CREATION_GUARD = "isCustomerPartyRoleCreatedGuard";
    public static final String PROSPECT_PARTY_ROLE_CREATION_GUARD = "isProspectPartyRoleCreatedGuard";
    public static final String PARTY_ROLES_CHECKING_GUARD = "arePartyRolesCheckedGuard";
    public static final String REFERENCES_VALIDATION_GUARD = "areReferencesValidatedGuard";
    public static final String REFERENCES_ADDITION_GUARD = "areReferencesAddedGuard";
    public static final String TECHNICAL_ELIGIBILITY_GUARD = "isTechnicalEligibleGuard";
    public static final String FINANCIALLY_ELIGIBLE_GUARD = "isFinanciallyEligibleGuard";

    private GuardNameConstants() {
        throw new IllegalStateException(ExceptionMessage.UTILITY_CLASS);
    }
}