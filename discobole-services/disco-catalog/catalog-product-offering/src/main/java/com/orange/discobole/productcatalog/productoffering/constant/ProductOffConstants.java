// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.constant;

import com.orange.discobole.productcatalog.productoffering.pojo.*;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.AssociatePOPtoOperationSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;

public class ProductOffConstants {

	private ProductOffConstants() {
	}

	public static final String ID = "id";
	public static final String OFFERING_TYPE = SelectProductOfferingType.class.getSimpleName();
	public static final String SUPPORT_ENTITY = SelectSupportEntity.class.getSimpleName();
	public static final String PRODUCT_OFF_ID = "productOffId";
	public static final String STATUS_REASON = "statusReason";
	public static final String CATEGORY = DefineProductOfferingCategory.class.getSimpleName();
	public static final String MARKET_SEGMENT = DefineProductOfferingMarketSegment.class.getSimpleName();
	public static final String CHANNEL = DefineProductOfferingSaleChannel.class.getSimpleName();
	public static final String RELATED_PARTY = SelectRelatedParty.class.getSimpleName();
	public static final String PRODUCT_OFF_CHARACTERISTICS = PickAtomicProductOfferingCharacteristic.class
			.getSimpleName();
	public static final String RELATIONSHIP = EntityRelationships.class.getSimpleName();
	public static final String POLICY_RULE_ASSOCIATION = PolicyRuleAssociation.class.getSimpleName();
	public static final String VALID_FOR = DefineEntityValidityPeriod.class.getSimpleName();
	public static final String TERM = ManageProductOfferingTerm.class.getSimpleName();
	public static final String VALIDATE = ValidateEntityOperation.class.getSimpleName();
	public static final String DESCRIPTION = "description";
	public static final String NAME = "name";
	public static final String BRAND = "brand";
	public static final String TYPE = "type";
	public static final String PRODUCTOFF_CANCEL = CancelEntityOperation.class.getSimpleName();
	public static final String IDENTITY_DATA = DefineProductOfferingIdentityData.class.getSimpleName();
	public static final String CONTRACT_IDENTITY_DATA = DefineContractProductOfferingIdentityData.class.getSimpleName();
	public static final String OPERATION_SPEC = DefineOperationSpecification.class.getSimpleName();
	public static final String BUNDLED_OPERATION_SPEC = DefineBundledOperationSpecification.class.getSimpleName();
	public static final String LINKPOPTOATOMICPRODOFFERING = AssociatePOPtoOperationSpecification.class.getSimpleName();
	public static final String PRODUCTOFFERINGTYPE = "productOfferingType";
	public static final String PRODUCT_OFFERING_BUNDLING = ManageProductOfferingBundling.class.getSimpleName();
	public static final String SELECT_PRODUCT_OFFERING = SelectPO.class.getSimpleName();
	public static final String SELECT_PRODOFF_LIFECYCLE_STATUS = "lifeCycleStatus";
	public static final String SELECT_PRODOFF_VERSION_TYPE = SelectProductOfferingVersion.class.getSimpleName();
	public static final String VERSION_TYPE = "versionType";
	public static final String USER_PRODOFF_CHAR ="ProductOfferingCharacteristic";
	public static final String DISCO_PO_INVALID_PO_STATE = "DISCO_PO_INVALID_PO_STATE";

	public static final String DISCO_PO_INVALID_PO_CHARACTRISTICS = "DISCO_PO_INVALID_PO_CHARACTRISTICS";

	public static final String DISCO_PO_INVALID_PO_CATEGORY_SELECTED = "DISCO_PO_INVALID_PO_CATEGORY_SELECTED";

	public static final String DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED_BPO_SAME = "DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED_BPO_SAME";

	public static final String DISCO_PO_INVALID_PO_RELATION = "DISCO_PO_INVALID_PO_RELATION";

	public static final String DISCO_INVALID_PO_HIERARCHY = "DISCO_INVALID_PO_HIERARCHY";

	public static final String DISCO_PO_INVALID_PO_STATE_INSTUDY = "DISCO_PO_INVALID_PO_STATE_INSTUDY";

	public static final String DISCO_PO_INVALID_PO_OPERATION = "DISCO_PO_INVALID_PO_OPERATION";

	public static final String DISCO_PO_INVALID_PO_OPERATION_ID = "DISCO_PO_INVALID_PO_OPERATION_ID";

	public static final String DISCO_PO_INVALID_POP_ID = "DISCO_PO_INVALID_POP_ID";

	public static final String DISCO_PO_INVALID_POP_STATUS = "DISCO_PO_INVALID_POP_STATUS";

	public static final String DISCO_PO_INVALID_BPO_NO_CHILD_ASSOCIATION = "DISCO_PO_INVALID_BPO_NO_CHILD_ASSOCIATION";

	public static final String DISCO_PO_INVALID_PO_STATE_INTEST_ACTIVE_LAUNCHED = "DISCO_PO_INVALID_PO_STATE_INTEST_ACTIVE_LAUNCHED";
	public static final String DISCO_PO_INVALID_PS_STATE = "DISCO_PO_INVALID_PS_STATE";

	public static final String DISCO_PO_INVALID_PS_LIFECYCLE_STATUS = "DISCO_PO_INVALID_PS_LIFECYCLE_STATUS";

	public static final String DISCO_PO_INVALID_BPO_LIFECYCLE_STATUS = "DISCO_PO_INVALID_BPO_LIFECYCLE_STATUS";

	public static final String DISCO_PO_CHARACTRISTICS_ID_OR_NAME_REPEATED = "DISCO_PO_CHARACTRISTICS_ID_OR_NAME_REPEATED";

	public static final String DISCO_PO_MINCARDINALITY_LESSOREQUAL_MAXCARDINALITY = "DISCO_PO_MINCARDINALITY_LESSOREQUAL_MAXCARDINALITY";

	public static final String DISCO_PO_INVALID_CATEGORY = "DISCO_PO_INVALID_CATEGORY";

	public static final String DISCO_PO_INVALID_CATEGORY_LIFECYCLE = "DISCO_PO_INVALID_CATEGORY_LIFECYCLE";

	public static final String DISCO_PO_INVALID_PO_CHANNEL = "DISCO_PO_INVALID_PO_CHANNEL";
	public static final String DISCO_PO_INVALID_PO_MARKET_SEGMENT="DISCO_PO_INVALID_PO_MARKET_SEGMENT";
	public static final String DISCO_PO_INVALID_PO_VALID_FOR = "DISCO_PO_INVALID_PO_VALID_FOR";

	public static final String DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED = "DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED";

	public static final String DISCO_PO_INVALID_POP_ASSOCIATION = "DISCO_PO_INVALID_POP_ASSOCIATION";

	public static final String DISCO_PO_NOT_EXSISTS = "DISCO_PO_NOT_EXSISTS";

	public static final String DISCO_PO_NOT_CPO_CANNOTBE_CHILDFOR_PO = "DISCO_PO_NOT_CPO_CANNOTBE_CHILDFOR_PO";

	public static final String DISCO_PO_INVALID_BPO_OPERATION_LIMIT = "DISCO_PO_INVALID_BPO_OPERATION_LIMIT";
	public static final String DISCO_BPO_GLOBAL_MAX_CARDINALITY_GREATER_THAN_CUMULATIVE_UPPER_LIMIT="DISCO_BPO_GLOBAL_MAX_CARDINALITY_GREATER_THAN_CUMULATIVE_UPPER_LIMIT";
	public static final String DISCO_BPO_GLOBAL_MAX_CARDINALITY_LESS_THAN_CUMULATIVE_LOWER_LIMIT="DISCO_BPO_GLOBAL_MAX_CARDINALITY_LESS_THAN_CUMULATIVE_LOWER_LIMIT";

	public static final String DISCO_PO_BPO_MINMAX_CARDINALITY = "DISCO_PO_BPO_MINMAX_CARDINALITY";
	public static final String DISCO_BPO_GLOBAL_MIN_CARDINALITY_GREATER_THAN_CUMULATIVE_UPPER_LIMIT="DISCO_BPO_GLOBAL_MIN_CARDINALITY_GREATER_THAN_CUMULATIVE_UPPER_LIMIT";
	public static final String DISCO_BPO_GLOBAL_MIN_CARDINALITY_LESS_THAN_CUMULATIVE_LOWER_LIMIT="DISCO_BPO_GLOBAL_MIN_CARDINALITY_LESS_THAN_CUMULATIVE_LOWER_LIMIT";

	public static final String DISCO_PO_NO_FEILD_SELECTED = "DISCO_PO_NO_FEILD_SELECTED";
	public static final String DISCO_PO_INVALID_PO_CHARACTRISTICS_VALUE = "DISCO_PO_INVALID_PO_CHARACTRISTICS_VALUE";
	public static final String DISCO_PO_IDENTITY_DATA_CANNOT_NULL = "DISCO_PO_IDENTITY_DATA_CANNOT_NULL";
	public static final String DISCO_PO_PROVIDE_IDORNAME = "DISCO_PO_PROVIDE_IDORNAME";
	public static final String DISCO_PO_VALIDITY_MUST_NOTNULL = "DISCO_PO_VALIDITY_MUST_NOTNULL";
	public static final String DISCO_PO_INVALID_PO_LIFECYCLE_SELECTED = "DISCO_PO_INVALID_PO_LIFECYCLE_SELECTED";
	public static final String DISCO_PO_INVALID_STATUS_NOT_MODIFIED = "DISCO_PO_INVALID_STATUS_NOT_MODIFIED";
	public static final String DISCO_PO_INVALID_PO_LIFECYCLE = "DISCO_PO_INVALID_PO_LIFECYCLE";
	public static final String DISCO_PO_INVALID_PO_CHARACTERISTICS_VALUE_TIME_RANGE_INVALID = "DISCO_PO_INVALID_PO_CHARACTERISTICS_VALUE_TIME_RANGE_INVALID";
	public static final String DISCO_PO_INVALID_STARTDATIME="DISCO_PO_INVALID_STARTDATIME";
	public static final String DISCO_PO_INVALID_ENDDATIME="DISCO_PO_INVALID_ENDDATIME";
	public static final String DISCO_PO_INVALID_PS = "DISCO_PO_INVALID_PS";

	public static final String DISCO_PO_EXPIRED_PS = "DISCO_PO_EXPIRED_PS";

	public static final String DISCO_PO_INVALID_PO = "DISCO_PO_INVALID_PO";

	public static final String DISCO_PO_EXPIRED_PO = "DISCO_PO_EXPIRED_PO";

	public static final String DISCO_PO_INVALID_ATTRIBUTE = "DISCO_PO_INVALID_ATTRIBUTE";

	public static final String DISCO_PO_DURATION_ATTRIBUTE_MANDATORY = "DISCO_PO_DURATION_ATTRIBUTE_MANDATORY";
	public static final String DISCO_PO_DURATION_SHOULD_BE_UNIQUE = "DISCO_PO_DURATION_SHOULD_BE_UNIQUE";
	public static final String DISCO_PO_INVALID_TERM_ASSOCIATE_CHARGE_STEP = "DISCO_PO_INVALID_TERM_ASSOCIATE_CHARGE_STEP";
	public static final String DISCO_PO_INVALID_TERM_UNIT = "DISCO_PO_INVALID_TERM_UNIT";
	public static final String DISCO_PO_INVALID_PO_TYPE="DISCO_PO_INVALID_PO_TYPE";
	public static final String DISCO_PO_INVALID_SUPPORT_ENTITY_TYPE="DISCO_PO_INVALID_SUPPORT_ENTITY_TYPE";
	public static final String DISCO_PO_INVALID_OPERATION_ID="DISCO_PO_INVALID_OPERATION_ID";
	public static final String DISCO_PO_INVALID_OPERATION_NAME="DISCO_PO_INVALID_OPERATION_NAME";
	public static final String DISCO_INVALID_JSON_BODY="DISCO_INVALID_JSON_BODY";
	public static final String DISCO_PO_INVALID_BUNDLING_PO_DOES_NOT_EXIST="DISCO_PO_INVALID_BUNDLING_PO_DOES_NOT_EXIST";
	public static final String DISCO_PO_INVALID_VALIDFOR_BEFORE_CURRENT_TIME="DISCO_PO_INVALID_VALIDFOR_BEFORE_CURRENT_TIME";
	public static final String DISCO_PO_INVALID_VALIDFOR_STARTTIME_ENDTIME="DISCO_PO_INVALID_VALIDFOR_STARTTIME_ENDTIME";
	public static final String DISCO_PO_MUST_HAVE_ONE_CHILD = "DISCO_PO_MUST_HAVE_ONE_CHILD";
	public static final String DISCO_PO_INVALID_CARDINALITY_FOR_SIGLE_CHILD = "DISCO_PO_INVALID_CARDINALITY_FOR_SIGLE_CHILD";
	public static final String DISCO_PO_INVALID_CHAR_NULL_NAME_OR_DESC="DISCO_PO_INVALID_CHAR_NULL_NAME_OR_DESC";
	public static final String DISCO_PO_INVALID_CHAR_DUPLICATE_NAME="DISCO_PO_INVALID_USER_CHAR_DUPLICATE_NAME";
	public static final String DISCO_PO_INVALID_USER_CHAR_VALIDITY="DISCO_PO_INVALID_USER_CHAR_VALIDITY";
	public static final String DISCO_PO_INVALID_USER_CHAR_NULL_VALUE="DISCO_PO_INVALID_USER_CHAR_NULL_VALUE";
	public static final String DISCO_PO_INVALID_USER_CHAR_DUPLICATE_VALUE="DISCO_PO_INVALID_USER_CHAR_DUPLICATE_VALUE";
	public static final String DISCO_PO_INVALID_PO_CHAR_NOTNULL_ID="DISCO_PO_INVALID_PO_CHAR_NOTNULL_ID";
	public static final String DISCO_PO_INVALID_PO_CHAR_INVALID_BASETYPE="DISCO_PO_INVALID_PO_CHAR_INVALID_BASETYPE";
	public static final String DISCO_PO_UNRECONIZED_CHANNEL="DISCO_PO_UNRECONIZED_CHANNEL";
	public static final String DISCO_PO_UNRECONIZED_ACTIONS="DISCO_PO_UNRECONIZED_ACTIONS";
	public static final String DISCO_PO_ALLOWED_ACTION_MANDATORY="DISCO_PO_ALLOWED_ACTION_MANDATORY";



	public static final String DISCO_CATEGORY_CHECK2 = "DISCO_CATEGORY_CHECK2";

	public static final String DISCO_PO_CATEGORYCHECK1 = "DISCO_PO_CATEGORYCHECK1";

	public static final String ALLOWED_ACTION= "SelectAllowedAction";
}