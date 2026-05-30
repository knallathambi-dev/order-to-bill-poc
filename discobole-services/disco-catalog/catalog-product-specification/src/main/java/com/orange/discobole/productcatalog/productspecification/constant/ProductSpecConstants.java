// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.constant;

import com.orange.discobole.productcatalog.productspecification.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.productspecification.pojo.DefineEntityValidityPeriod;
import com.orange.discobole.productcatalog.productspecification.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productspecification.pojo.EntityRelationships;
import com.orange.discobole.productcatalog.productspecification.pojo.PickCharacteristicSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.PickOperationSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.PickStockCharacteristicSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.ProductSpecUsageSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectProductSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectProductSpecificationVersion;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectRelatedParty;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectRelatedResource;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectSupportEntity;
import com.orange.discobole.productcatalog.productspecification.pojo.ValidateEntityOperation;

public class ProductSpecConstants {

	private ProductSpecConstants() {
	}

	public static final String SUPPORT_ENTITY = SelectSupportEntity.class.getSimpleName();
	public static final String ID = "id";
	public static final String PRODUCT_SPEC_ID = "productSpecId";
	public static final String SERVICE_SPEC_ID = "serviceSpecId";
	public static final String STOCK_ITEM_ID = "stockItemId";
	public static final String OPERATION_SPEC = PickOperationSpecification.class.getSimpleName();
	public static final String USAGE_SPEC = ProductSpecUsageSpecification.class.getSimpleName();
	public static final String RELATION_SPEC = EntityRelationships.class.getSimpleName();
	public static final String PRODUCT_SPEC_CHARACTERISTICS = PickCharacteristicSpecification.class.getSimpleName();
	public static final String STOCK_ITEM_CHARACTERISTICS = PickStockCharacteristicSpecification.class.getSimpleName();
	public static final String RELATED_PARTY = SelectRelatedParty.class.getSimpleName();
	public static final String RESOURCES = SelectRelatedResource.class.getSimpleName();
	public static final String VALID_FOR = DefineEntityValidityPeriod.class.getSimpleName();
	public static final String VALIDATE = ValidateEntityOperation.class.getSimpleName();
	public static final String INDIVIDUAL_ROLE = "individualRole";
	public static final String ORGANIZATIONAL_ROLE = "organizationalRole";
	public static final String IDENTITY_DATA = DefineIdentityData.class.getSimpleName();
	public static final String PRODUCTSPEC_CANCEL = CancelEntityOperation.class.getSimpleName();
	public static final String SELECT_PRODSPEC = SelectProductSpecification.class.getSimpleName();
	public static final String VERSION_TYPE="versionType";
	public static final String SELECT_PRODSPEC_VERSION_TYPE= SelectProductSpecificationVersion.class.getSimpleName();
	public static final String SELECT_PRODSPEC_LIFECYCLE_STATUS="lifeCycleStatus";
	public static final String DISCO_PS_INVALID_PS_LIFECYCLE = "DISCO_PS_INVALID_PS_LIFECYCLE";
	public static final String DISCO_PS_INVALID_STATUS_NOT_MODIFIED = "DISCO_PS_INVALID_STATUS_NOT_MODIFIED";
	public static final String DISCO_INVALID_JSON_BODY="DISCO_INVALID_JSON_BODY";
}
