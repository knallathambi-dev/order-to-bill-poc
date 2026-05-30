// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.constant;

import com.orange.discobole.productcatalog.productoffering.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineEntityValidityPeriod;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineRelationship;
import com.orange.discobole.productcatalog.productoffering.pojo.PickCharacteristicSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.PickOperationSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.PickStockCharacteristicSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.PickUsageSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectProductSpecification;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectProductSpecificationVersion;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectRelatedParty;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectRelatedResource;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectSupportEntity;
import com.orange.discobole.productcatalog.productoffering.pojo.ValidateEntityOperation;

public class ProductSpecConstants {

	private ProductSpecConstants() {
	}

	public static final String SUPPORT_ENTITY = SelectSupportEntity.class.getSimpleName();
	public static final String PRODUCT_SPEC_ID = "productSpecId";
	public static final String SERVICE_SPEC_ID = "serviceSpecId";
	public static final String STOCK_ITEM_ID = "stockItemId";
	public static final String OPERATION_SPEC = PickOperationSpecification.class.getSimpleName();
	public static final String USAGE_SPEC = PickUsageSpecification.class.getSimpleName();
	public static final String RELATION_SPEC = DefineRelationship.class.getSimpleName();
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

}
