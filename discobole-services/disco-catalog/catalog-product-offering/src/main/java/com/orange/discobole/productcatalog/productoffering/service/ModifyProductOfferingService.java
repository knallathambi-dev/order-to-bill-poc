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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineContractIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;

/**
 * Contract to process Product Offering Commands.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public interface ModifyProductOfferingService {

	/**
	 * Function to handle product offering process cancel command
	 * 
	 * @param productOffId
	 */
	void cancelProductOfferingModification(String productOffId);

	void initiatePOModification(String poId);

	/**
	 * function to modify Product offering description.
	 *
	 * @param productOffId  the product off id
	 * @param description   the description
	 * @param name          the name
	 * @param statusReason  the user entry
	 * @param brand         the brand
	 * @param type          the type
	 * @param isInstallable the is installable
	 */
	public void modifyProductOffDesc(String productOffId, DefineIdentityData defineIdentityData, Set<String> channelIds, Set<String> marketSegmentIds, Set<RelatedParty> relatedParties,
									 Set<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type, String statusReason, ProductOfferingLifecycle lifecycleStatus);

    void modifyContractProductOffDesc(String productOffId, DefineContractIdentityData defineData,
                                      Set<String> channelIds, Set<String> marketSegmentIds, Set<RelatedParty> relatedParties,
                                      Set<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type, String statusReason,
                                      OffsetDateTime lastUpdate, ProductOfferingLifecycle lifecycleStatus);

    /**
	 * function to modify Product Offering categories.
	 * 
	 * @param productOffId the product offering id
	 * @param categoryIds        categories to be defined
	 */
	void modifyProductOfferingCategory(String productOffId, List<String> categoryIds);




	/**
	 * function to modify product offering operation specification.
	 *
	 * @param productOffId       the product offering id
	 * @param operationSpecifications list of operation specification
	 */
	void modifyProductOfferingOperation(String productOffId, List<CommercialOperation> operationSpecifications);

	/**
	 * function to modify Associate POP to Operation Specification
	 *
	 * @param associatePOPtoOperationSpecList
	 * @param productOffId
	 */
	void modifyAssociatePOPtoOperationSpecification(List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList,
			String productOffId);


	/**
	 *
	 * function to modify Product Offering Characteristics.
	 *
	 * @param productOfferingId                        the product offering id
	 * @param pickProductOfferingCharacteristic        the pick atomic product
	 *                                                 offering characteristics
	 */
	void modifyProductOfferingCharacteristics(String productOfferingId,
			List<PickAtomicProductOfferingCharacteristic> pickProductOfferingCharacteristic);

	/**
	 * function to modify Product Offering relationships.
	 *
	 * @param productOffId      the product offering id
	 * @param relationships     relationships to be defined
	 * @param policyRules 
	 */
	void modifyProductOfferingRelationship(String productOffId, List<ProductOfferingRelationship> relationships);



	void validateProductOfferingModification(String productOfferingId,String versionType);

	/**
	 * PO Aggregate Modify from Other Aggregate .
	 *
	 * @author Rajan Chauhan
	 * @param productOffId
	 * @param categories
	 * @param addition
	 */
	public void callPOAggregateForModification(String productOffId, Set<CategoryRef> categories, boolean addition,String productOfferingType);



	void modifyBundledProductOfferingOperation(String productOffId,
											   List<CommercialOperation> operationSpecifications);

	void modifyBundleProductOfferings(String productOffId, List<BundledProductOffering> productOfferBundlings,
									  int globalMinCardinality, int globalMaxCardinality);

	void modifyProductOfferingPolicyRuleAssociation(String productOffId, List<PolicyRuleRef> policyRules);

	void modifySelectAllowedAction(String productOffId, List<AllowedProductAction> alloweddto);
}
