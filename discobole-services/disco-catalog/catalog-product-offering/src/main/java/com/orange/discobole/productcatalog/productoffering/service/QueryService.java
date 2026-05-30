// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service;

import java.util.List;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productcatalogadministration.FrequencyTypes;

/**
 * This interface corresponds to declare the operations related to
 * {@code ServiceSpecification}.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public interface QueryService {

	/**
	 * This method is used to get the {@code ProductSpecification} from the read
	 * model repository.
	 *
	 * @param productSpecId unique identifier of {@code ProductSpecification}
	 * @return fetched object of {@code ProductSpecification} based on serviceSpecId
	 */
	ProductSpecification fetchProductSpecById(String productSpecId, String token);

	/**
	 * This method is used to get the list of {@code ProductSpecification} from the
	 * read model repository.
	 *
	 * @param productSpecIds unique identifier of {@code ProductSpecification},
	 *                       productSpecIds can be multiple and will be separated by
	 *                       comma.
	 * @return fetched object of {@code ProductSpecification} based on
	 *         productSpecIds
	 */
	List<ProductSpecification> fetchProductSpecifications(String productSpecIds, String token);

	/**
	 * This method is used to fetch the category.
	 *
	 * @return categories
	 */
	List<Category> fetchCategory(String token);

	List<MarketSegmentAdmin> fetchMarketSegments(String token);

	/**
	 * Returns all the channelRef
	 *
	 * @return ChannelRef
	 */
	List<ChannelAdmin> fetchChannels(String token);



	/**
	 * Fetch list of product offering
	 * 
	 * @return list of ProductOffering
	 */
	List<ProductOffering> fetchProductOffering(String token);

	/**
	 * Fetch ProductOffering by Id
	 * 
	 * @param productOfferingId
	 * @return ProductOffering
	 */
	ProductOffering fetchProductOfferingById(String productOfferingId, String token);

	/**
	 * Fetch list of product offerings, realized on product spec id
	 * 
	 * @param productSpecId
	 * @return list of ProductOffering
	 */
	List<ProductOffering> fetchProductOfferingsByProductSpecId(String productSpecId);
	
	ProductOfferingPrice getProductOfferingPrice(String productOfferingPriceId);	

	List<ProductOffering> fetchProductOfferingsByProductOfferingPriceId(String productOfferingPriceId);

	List<ProductOffering> fetchBundleProductOfferingByAtomicProductOfferingId(String entityId);

	List<ProductSpecification> fetchProductSpecificationsByLifeCycleStatus(String status, String token);

	List<ProductOffering> fetchProductOfferingsByLifeCycleStatus(String status);

	/**
	 * Fetch category, realized on categoryId
	 * 
	 * @param categoryId
	 * @return
	 */
	Category fetchCategoryById(String categoryId);

	CategoryEntityRelationship fetchCategoryEntityById(String categoryEntityId);

	List<Category> fetchCategoryEntityBySubCategoryId(String categoryEntityId);
	
	List<ProductOffering> fetchBundleAndContractProductOfferingByBundlingProductOfferingId(String entityId);

	List<ProductOffering> fetchProductOfferingsByIds(List<String> productOfferingIds, String token);

	List<ProductOfferingPrice> fetchProductOfferingPriceByIds(List<String> productOfferingPriceIds, String token);

	List<Category> fetchCategoryByIds(List<String> categoryIds, String token);

	List<FrequencyTypes> fetchFrequency(String token);

	List<ProductOffering> fetchProductOfferingsByPolicyRule(String policyRuleId);
}
