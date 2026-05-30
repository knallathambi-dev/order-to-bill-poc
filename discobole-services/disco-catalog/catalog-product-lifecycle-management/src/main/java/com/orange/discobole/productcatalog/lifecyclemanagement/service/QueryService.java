// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.service;

import java.util.List;

import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductSpecification;

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
     * @param accessToken
     * @return fetched object of {@code ProductSpecification} based on serviceSpecId
     */
	ProductSpecification fetchProductSpecById(String productSpecId, String accessToken);

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
	List<ProductSpecification> fetchProductSpecifications(String productSpecIds, String accessToken);

	

	/**
	 * Fetch list of product offering
	 * 
	 * @return list of ProductOffering
	 */
	List<ProductOffering> fetchProductOffering(String accessToken);


	/**
	 * Fetch ProductOffering by Id
	 *
	 * @param productOfferingId
	 * @param accessToken
	 * @return ProductOffering
	 */
	ProductOffering fetchProductOfferingById(String productOfferingId, String accessToken);

	/**
     * Fetch list of product offerings, realized on product spec id
     *
     * @param productSpecId
     * @param accessToken
     * @return list of ProductOffering
     */
	List<ProductOffering> fetchProductOfferingsByProductSpecId(String productSpecId, String accessToken);
	
	List<ProductOffering> fetchProductOfferingsByProductOfferingPriceId(String productOfferingPriceId, String accessToken);

	ProductOfferingPrice getProductOfferingPrice(String productOfferingPriceId, String accessToken);

	List<ProductOffering> fetchBundleProductOfferingByAtomicProductOfferingId(String entityId, String accessToken);


	/**
	 * Fetch list of Bundle and Contract product offerings, realized on Bundling ProductOffering Id
	 * 
	 * @param Bundle product Offering id
	 * @return list of BundleProductOffering
	 */
	
	List<ProductOffering> fetchBundleAndContractProductOfferingByBundlingProductOfferingId(String entityId, String accessToken);
	
	
	
	
	
	
	List<ProductOfferingPrice> getProductOfferingPricesByProductOfferingPriceId(String entityId, String accessToken);



	
}
