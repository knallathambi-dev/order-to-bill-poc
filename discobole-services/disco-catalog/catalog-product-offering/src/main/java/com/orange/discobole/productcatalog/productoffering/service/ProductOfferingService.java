// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.BundledProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.PolicyRuleRef;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.CommercialOperation;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.dto.policyrule.Event;
import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineContractIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.SelectProductOfferingType;

/**
 * Contract to process Product Offering Commands.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public interface ProductOfferingService {

	/**
	 * function to create Product offering.
	 *
	 * @param offeringType
	 * @return product offering id
	 */
	String createProductOfferingType(SelectProductOfferingType offeringType);

	/**
	 * function to create Product offering using product spec id.
	 *
	 * @param productSpecId the product spec id
	 * @param productOffId
	 */
	void createProductOffering(String productSpecId, String productOffId);

	/**
	 * function to define Product offering description.
	 *
	 * @author Diksha Srivastava
	 * @param productOffId       the product off id
	 * @param defineIdentityData the description
	 * @param channelIds         the name
	 * @param marketSegmentIds   the user entry
	 * @param relatedParties     the brand
	 * @param type               the type
	 * @param poTerms            the is installable
	 * @param statusReason       the status reason
	 */
	public void defineProductOffDesc(String productOffId, DefineIdentityData defineIdentityData, Set<String> channelIds,
			Set<String> marketSegmentIds, Set<RelatedParty> relatedParties, Set<ProductOfferingTerm> poTerms,
			TimePeriod validity, ProductOfferingType type, String statusReason);

	/**
	 * function to define Product Offering categories.
	 *
	 * @param productOfferingId the product offering id
	 * @param categories        categories to be defined
	 */

	void defineProductOfferingCategory(String productOfferingId, List<String> categories);

	/**
	 * function to define Product Offering relationships.
	 *
	 * @param productOfferingId the product offering id
	 * @param relationships     relationships to be defined
	 * @param policyRules 
	 */
	void defineProductOfferingEnitityRelationship(String productOfferingId, List<ProductOfferingRelationship> relationships);

	/**
	 * @author Shreya Sharma
	 *
	 *         function to Update Product Offering Characteristics.
	 *
	 * @param productOfferingId                        the product offering id
	 * @param pickAtomicProductOfferingCharacteristics the pick atomic product
	 *                                                 offering characteristics
	 */
	void updateProductOfferingCharacteristics(String productOfferingId,
			List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristics);

	/**
	 * Validates product offering and sets the lifecycle status to IN_STUDY and
	 * create a new version.
	 *
	 * @author Diksha Srivastava
	 * @param productOfferingId the product offering id
	 */
	void validateProductOffering(String productOfferingId);

	/**
	 * Define product offering operation specification.
	 *
	 * @author Varshika Choudhary
	 * @param productOfferingId       the product offering id
	 * @param operationSpecifications list of operation specification
	 */
	void defineProductOfferingOperation(String productOfferingId, List<CommercialOperation> operationSpecifications);

	/**
	 * Cancel the incomplete product offering based on the productOff id param.
	 *
	 * @param productOffId the product off id
	 */
	void cancelProductOff(String productOffId);

	/**
	 * Associate POP to Operation Specification
	 *
	 * @author Varshika Choudhary
	 * @param associatePOPtoOperationSpecList
	 * @param productOfferingId
	 */
	void associatePOPtoOperationSpecification(List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList,
			String productOfferingId);

	/**
	 * Define Bundled product offering operation specification.
	 *
	 * @author Vishal Vachaspati
	 * @param productOffId
	 * @param operationSpecifications
	 */
	void defineBundledProductOfferingOperation(String productOffId, List<CommercialOperation> operationSpecifications);

	/**
	 * Define Bundled product offerings.
	 *
	 * @author Vishal Vachaspati
	 * @param productOffId
	 * @param productOfferBundlings
	 * @param globalMinCardinality  globalMinCardinality
	 * @param globalMaxCardinality  globalMaxCardinality
	 */
	void defineBundleProductOfferings(String productOffId, List<BundledProductOffering> productOfferBundlings,
			int globalMinCardinality, int globalMaxCardinality);

	void deleteProductOffering(Long interval, OffsetDateTime delDate, String intervalUnit);

	void atomicProductOfferingCategoryModifyByCategoryDeletion(String productOfferingId, Set<CategoryRef> addCategories,
			Set<CategoryRef> delCategories, OffsetDateTime lastUpdate);

	/**
	 * Define Contract product offerings IdentityData.
	 *
	 * @author Vishal Vachaspati
	 * @param productOffId
	 */
	void defineContractProductOffDesc(String productOffId, DefineContractIdentityData defineData,
			Set<String> channelIds, Set<String> marketSegmentIds, Set<RelatedParty> relParty,
			Set<ProductOfferingTerm> prodOffTerms, TimePeriod generated, ProductOfferingType fromValue, String reason,
			OffsetDateTime lastUpdate);

	void defineProductOfferingPolicyRuleAssociation(String productOffId, List<PolicyRuleRef> policyRules);



	/**
	 * Define product offering Selected allowed action.
	 *
	 * @author Jagriti Pahwa
	 * @param productOfferingId       the product offering id
	 * @param alloweddto list of  selectAllowedAction
	 */
	void defineSelectAllowedAction(String productOfferingId, List<AllowedProductAction> alloweddto);

	void processPolicyEvent(final Event event);
}
