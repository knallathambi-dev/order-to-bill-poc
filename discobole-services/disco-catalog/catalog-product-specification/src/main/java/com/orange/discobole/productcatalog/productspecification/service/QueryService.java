// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service;

import java.util.List;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.*;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
/**
 * This interface corresponds to declare the operations related to
 * {@code ServiceSpecification}.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public interface QueryService {
	/**
     * This method is used to get the {@code ServiceSpecification} from the read
     * model repository.
     *
     * @param serviceSpecId unique identifier of {@code ServiceSpecification}
     * @param accessToken
     * @return fetched object of {@code ServiceSpecification} based on serviceSpecId
     */
	ServiceSpecification getServiceSpecById(String serviceSpecId, String accessToken);

	/**
	 * This method is used to get the {@code StockItem} from the read
	 * model repository.
	 *
	 * @param stockItemId unique identifier of {@code StockItem}
	 * @return fetched object of {@code StockItem} based on StockItemId
	 */
	StockItem getStockItemById(String stockItemId,String accessToken);

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
	 * @param accessToken
	 * @return fetched object of {@code ProductSpecification} based on
	 * productSpecIds
	 */
	List<ProductSpecification> fetchProductSpecifications(String productSpecIds, String accessToken);

	/**
	 * This method is used to fetch the category.
	 *
	 * @return categories
	 */
	List<MarketSegmentRef> fetchMarketSegments(String accessToken);

	/**
	 * Returns all the channelRef
	 *
	 * @return ChannelRef
	 */
	List<ChannelRef> fetchChannels(String accessToken);

	List<CFSRelationshipRestriction> fetchCFSRelationship(String accessToken);

	/**
	 * Fetch list of product offering
	 * 
	 * @return list of ProductOffering
	 */

	/**
	 * Fetch ProductOffering by Id
	 * 
	 * @param productOfferingId
	 * @return ProductOffering
	 */

	/**
	 * Fetch list of product offerings, realized on product spec id
	 *
	 * @param productSpecId
	 * @param accessToken
	 * @return list of ProductOffering
	 */

	StockItemType getStockItemTypeById(final String stockItemTypeId, String accessToken);

	List<StockItem> getStockItemByStockItemTypeId(final String stockItemTypeId, String accessToken);
	
	List<ProductSpecification> fetchProductSpecificationsByLifeCycleStatus(String status,String accessToken);

	List<ProductOffering> fetchProductOfferingsByProductSpecId(String entityId, String accessToken);
	
	/**
	 * Fetch category, realized on categoryId
	 * 
	 * @param categoryId
	 * @return
	 */

}
