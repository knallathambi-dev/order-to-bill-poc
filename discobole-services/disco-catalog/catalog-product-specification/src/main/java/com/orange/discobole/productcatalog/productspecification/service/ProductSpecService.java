// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service;

import java.time.OffsetDateTime;
import java.util.List;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationRelationship;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;

/**
 * This service specifies the operations that needs to be performed for product
 * specification.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public interface ProductSpecService {
	/**
	 * This method specifies the operation to initiate the creation of product
	 * specification.
	 *
	 * @param serviceSpecId service specification id
	 * 
	 * @return returns product specification id
	 */
	String initiateProductSpecCreation(String serviceSpecId);

	String initiateStockItemProductSpecCreation(final String stockItemId);

	void initiateProductSpecDef(String productSpecId, IdentityData defineIdentityData,
			List<RelatedParty> relParty, List<RelatedResource> relResource,
			TimePeriod timePeriod, EntityType type);

	void updateProductSpecRel(String productSpecId, List<ProductSpecificationRelationship> serviceSpecRelationships,
			List<PolicyRuleRef> policyRules);

	void updateProductSpecCharacteristics(String productSpecId,
			List<ProductSpecificationCharacteristic> productSpecCharacteristicValuesMap, List<UsageSpecification> usageSpecifications);

	void updateStockItemProductSpecCharacteristics(String productSpecId,
										  List<ProductSpecificationCharacteristic> stockItemProductSpecCharacteristicValuesMap);
	String deleteProductSpecification(OffsetDateTime delDate,Long interval,String intervalUnit);
	String temporaryDeleteProductSpecification();

	/**
	 * Validates product specification and sets the lifecycle status to IN_TEST and
	 * create a new version.
	 *
	 * @author Vivek Singh
	 * @param productSpecId the product specification id
	 */
	void validateProductSpecification(String productSpecId);

	/**
	 * Cancel the incomplete product spec based on the productSpec id param.
	 *
	 * @param productSpecId the product spec id
	 */
	void cancelProductSpec(String productSpecId);

	void initiateProductSpecModification(String productSpecId);

	void modifyProductSpecDefineIdentity(String productSpecId, IdentityData defineIdentityData, List<RelatedParty> relParty,
			List<RelatedResource> relResource, TimePeriod timePeriod, EntityType type,ProductSpecificationLifecycle lifecycle);

	void modifyProductSpecCharacteristics(String productSpecId,
			List<ProductSpecificationCharacteristic> productSpecCharacteristics,List<UsageSpecification> usageSpecifications);

	void modifyProductSpecRel(String productSpecId, List<ProductSpecificationRelationship> productSpecRelationships, List<PolicyRuleRef> policyRules);

	void validateProductSpecificationModification(String productSpecId);

	void cancelProductSpecModification(String productSpecId);

	void modifyStockItemProductSpecCharacteristics(String productSpecId,
												   List<ProductSpecificationCharacteristic> stockItemProductSpecCharacteristicValuesMap);

}
