// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.constant;

import com.orange.discobole.productcatalog.productofferingprice.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.productofferingprice.pojo.ValidateEntityOperation;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.*;

public class ProductOfferingPriceConstants {
	private
	ProductOfferingPriceConstants() {
	}
	public static final String ID = "id";
	public static final String SELECT_POP_TYPE = SelectPOPType.class.getSimpleName();
	public static final String PRODUCT_OFFERING_PRICE_ID = "productOfferingPriceId";
	public static final String PRODUCT_OFFERING_PRICE_CHARGE_IDENTITY = DefineProductOfferingPriceChargeIdentity.class.getSimpleName();
	public static final String PRODUCT_OFFERING_PRICE_ALTERATION_IDENTITY = DefineProductOfferingPriceAlterationIdentity.class.getSimpleName();
	public static final String DISCO_POP_PERCENTAGE_DEFINED_IN_RANGE = "DISCO_POP_PERCENTAGE_DEFINED_IN_RANGE";
	public static final String PRODUCT_OFFERING_PRICE_TAX_ALTERATION_IDENTITY = DefineTaxProductOfferingPriceAlterationIdentity.class.getSimpleName();
	public static final String PRODUCT_OFFERING_PRICE_INSTALLMENT_CHARGE_IDENTITY = DefineInstallmentChargeProductOfferingPriceIdentity.class.getSimpleName();
	public static final String PRODUCT_OFFERING_PRICE_PRICE_TYPE = "productOfferingPricePriceType";
	public static final String RECURRING_CHARGE_PERIOD = "RecurringChargePeriod";
	public static final String VALIDITY = DefinePOPStatusValidityPeriod.class.getSimpleName();
	public static final String DEFINE_RELATIONSHIP = DefineRelationship.class.getSimpleName();
	public static final String CANCEL_PRODUCT_OFFERING_PRICE = CancelEntityOperation.class.getSimpleName();
	public static final String SELECT_PRODUCT_OFFERING_PRICE = SelectPOP.class.getSimpleName();
	public static final String VERSION_TYPE = "versionType";
	public static final String SELECT_POP_LIFECYCLE_STATUS = "lifeCycleStatus";
	public static final String SELECT_POP_VERSION_TYPE = SelectProductOfferingPriceVersion.class.getSimpleName();
	public static final String VALIDATE = ValidateEntityOperation.class.getSimpleName();
	public static final String DISO_POP_INVALID_POP_IDENTITYDATA = "DISO_POP_INVALID_POP_IDENTITYDATA";
	public static final String DISO_POP_INVALID_POP_TYPE = "DISO_POP_INVALID_POP_TYPE";
	public static final String DISO_POP_INVALID_POP_TYPE_RC = "DISO_POP_INVALID_POP_TYPE_RC";
	public static final String DISO_POP_INVALID_IMMEDIATE_PAYMENT_WITH_CURRNET_PRICE_TYPE = "DISO_POP_INVALID_IMMEDIATE_PAYMENT_WITH_CURRNET_PRICE_TYPE";
	public static final String DISO_POP_INVALID_POPC_LIFECYCLE_STATUS = "DISO_POP_INVALID_POPC_LIFECYCLE_STATUS";
	public static final String DISO_POP_INVALID_POP_VALIDFOR = "DISO_POP_INVALID_POP_VALIDFOR";
	public static final String DISO_POP_INVALID_POPC_RELATIONSHIP_POPA_PRIORITY = "DISO_POP_INVALID_POPC_RELATIONSHIP_POPA_PRIORITY";
	public static final String DISO_POP_INVALID_POPA_TYPE = "DISO_POP_INVALID_POPA_TYPE";
	public static final String DISO_POP_PRICE_TYPE_NOT_RC = "DISO_POP_PRICE_TYPE_NOT_RC";
	public static final String DISO_POP_INVALID_POP_TYPE_APPLICATION_DURATION = "DISO_POP_INVALID_POP_TYPE_APPLICATION_DURATION";
	public static final String DISO_POP_DIFFERENT_POPA_PRICETYPE = "DISO_POP_DIFFERENT_POPA_PRICETYPE";
	public static final String DISO_POP_INVALID_POPA_LIFECYCLE_STATUS = "DISO_POP_INVALID_POPA_LIFECYCLE_STATUS";
	public static final String DISO_POP_INCONSISTENT_USEOF_POPA_RELATIONSHIP_TYPE = "DISO_POP_INCONSISTENT_USEOF_POPA_RELATIONSHIP_TYPE"
			+ "";
	public static final String DISO_POP_INVALID_POPC_LIFECYCLE_STATUS_UNAVAILABLE = "DISO_POP_INVALID_POPC_LIFECYCLE_STATUS_UNAVAILABLE"
			+ "";
	public static final String DISO_POP_CANNOT_MODIFY_PAYMENT = "DISO_POP_CANNOT_MODIFY_PAYMENT";
	public static final String DISO_POP_INVALID_POP_TYPE_DURATION = "DISO_POP_INVALID_POP_TYPE_DURATION";
	public static final String DISO_POP_POP_NOT_FOUND = "DISO_POP_POP_NOT_FOUND";
	public static final String DISO_POP_POP_MINOR_VERSION = "DISO_POP_POP_MINOR_VERSION";
	public static final String DISO_POP_MAJOR_VERSION = "ProductOfferingPriceConstants";
	public static final String DISO_POP_NO_MAJOR_VERSION = "DISO_POP_NO_MAJOR_VERSION";
	public static final String DISO_POP_INVALID_CHARACERISTICS_VALUE = "DISO_POP_INVALID_CHARACERISTICS_VALUE";
	public static final String DISO_POP_INVALID_POPATYPE_VALUE = "DISO_POP_INVALID_POPATYPE_VALUE";
	public static final String DISO_POP_INVALID_PERCENTAGE_VALUE = "DISO_POP_INVALID_PERCENTAGE_VALUE";
	public static final String DISCO_POP_INVALID_STATUS_NOT_MODIFIED = "DISCO_POP_INVALID_STATUS_NOT_MODIFIED";
	public static final String DISCO_POP_INVALID_POP_LIFECYCLE = "DISCO_POP_INVALID_POP_LIFECYCLE";
	public static final String DISCO_POP_INVALID_CYCLECHARGE_NRC = "DISCO_POP_INVALID_CYCLECHARGE_NRC";
	public static final String DISCO_POP_INVALID_PRORATIONTYPE = "DISCO_POP_INVALID_PRORATIONTYPE";
	public static final String DISCO_POP_BOTH_PRICE_PERCENTAGE_DEFINED = "DISCO_POP_BOTH_PRICE_PERCENTAGE_DEFINED";
	public static final String DISCO_POP_NEITHER_PRICE_PERCENTAGE_DEFINED = "DISCO_POP_NEITHER_PRICE_PERCENTAGE_DEFINED";
	public static final String DISCO_POPA_BOTH_PRICE_PERCENTAGE_DEFINED = "DISCO_POPA_BOTH_PRICE_PERCENTAGE_DEFINED";
	public static final String DISCO_POPA_NEITHER_PRICE_PERCENTAGE_DEFINED = "DISCO_POPA_NEITHER_PRICE_PERCENTAGE_DEFINED";
	public static final String DISO_POP_ASSOCIATED_WITH_TAXPOPA_THROUGH_ALTERED_BY = "DISO_POP_ASSOCIATED_WITH_TAXPOPA_THROUGH_ALTERED_BY";
	public static final String DISCO_POP_VALIDITY_INVALID_STARTTIME="DISCO_POP_VALIDITY_INVALID_STARTTIME";
	public static final String DISO_POP_INVALID_POPA_PRICE = "DISO_POP_INVALID_POPA_PRICE";

	public static final String DISO_POP_INVALID_POPA_UNIT = "DISO_POP_INVALID_POPA_UNIT";

	public static final String DISO_POP_INVALID_POP_CURRENCY = "DISO_POP_INVALID_POP_CURRENCY";

	public static final String DISO_POP_INVALID_POP_FREQUENCY = "DISO_POP_INVALID_POP_FREQUENCY";

	public static final String DISCO_POP_PRICE_NOT_DEFINED= "DISCO_POP_PRICE_NOT_DEFINED";
	public static final String DISCO_POP_INVALID_RELATION_MULTIPLE_REPLACEDBY="DISCO_POP_INVALID_RELATION_MULTIPLE_REPLACEDBY";
	public static final String DISCO_POP_INVALID_RELATION_BOTH_ALTEREDBY_AND_REPLACEDBY_PRESENT="DISCO_POP_INVALID_RELATION_BOTH_ALTEREDBY_AND_REPLACEDBY_PRESENT";
	public static final String DISCO_INVALID_JSON_BODY="DISCO_INVALID_JSON_BODY";
	public static final String DISCO_POP_ASSOCIATED_POPA_IS_NOT_FOUND="DISCO_POP_ASSOCIATED_POPA_IS_NOT_FOUND";
	public static final String DISCO_INSTALLMENTPLAN_POP_PRICE_NOT_DEFINED="DISCO_INSTALLMENTPLAN_POP_PRICE_NOT_DEFINED";
	public static final String DISCO_INSTALLMENTPLAN_POP_PRICE_UNIT_NOT_DEFINED="DISCO_INSTALLMENTPLAN_POP_PRICE_UNIT_NOT_DEFINED";
	public static final String DISCO_INSTALLMENTPLAN_POP_PRICE_VALUE_NOT_DEFINED="DISCO_INSTALLMENTPLAN_POP_PRICE_VALUE_NOT_DEFINED";
	public static final String DISCO_APPLICATION_DURATION_IS_MISSING="DISCO_APPLICATION_DURATION_IS_MISSING";
	public static final String DISCO_DURATION_UNIT_IS_MISSING="DISCO_DURATION_UNIT_IS_MISSING";
	public static final String DISCO_DURATION_AMOUNT_IS_MISSING="DISCO_DURATION_AMOUNT_IS_MISSING";
	public static final String DISCO_PARTNER_IS_MISSING="DISCO_PARTNER_IS_MISSING";
	public static final String DISCO_INSTALLMENTPLAN_INTEREST_RATE_INVALID="DISCO_INSTALLMENTPLAN_INTEREST_RATE_INVALID";
    public static final String DISCO_POPC_CAN_NOT_ASSOCIATED_WITH_INSTALLMENT="DISCO_POPC_CAN_NOT_ASSOCIATED_WITH_INSTALLMENT";
	public static final String DISCO_INSTALLMENT_CAN_NOT_ASSOCIATED_WITH_POPC="DISCO_INSTALLMENT_CAN_NOT_ASSOCIATED_WITH_POPC";
	public static final String DISCO_DOWNPAYMENT_SHOULD_LESS_THAN_PRICE_VALUE="DISCO_DOWNPAYMENT_SHOULD_LESS_THAN_PRICE_VALUE";
	public static final String DISCO_INSTALLMENTPLAN_DOWNPAYMENT_NEGATIVE="DISCO_INSTALLMENTPLAN_DOWNPAYMENT_NEGATIVE";

}
