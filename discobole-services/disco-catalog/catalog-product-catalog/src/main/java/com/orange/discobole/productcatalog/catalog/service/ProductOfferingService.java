// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Update;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;

/**
 * The Interface ProductOfferingService to save and update the product offering
 * received.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
public interface ProductOfferingService {

	/**
	 * Save/update ProductOffering in Database.
	 *
	 * @param productOffering to be saved
	 */
	void saveProductOffering(ProductOffering productOffering);

	/**
	 * Fetch the ProductOffering on the basis of the parameters given.
	 *
	 * @param requestParams parameters to search upon
	 * @return List of filtered ProductOffering
	 */
	List<ProductOffering> fetchProductOffering(Map<String, Object> requestParams, Long skip, Long limit,String fields) throws UnsupportedEncodingException;

	/**
	 * Fetch the ProductOffering on the basis of id.
	 *
	 * @param id to search
	 * @return ProductOffering
	 */
	ProductOffering fetchProductOfferingById(String id);

	/**
	 * Fetch the ProductOffering on the basis of id.
	 *
	 * @param id to search
	 * @return ProductOffering
	 */
	ProductOffering fetchProductOfferingById(String id, List<String> fieldList);


	/**
	 * remove productOffering instance from mongodb w.r.t id parameter.
	 *
	 * @param productOffId the id of productOffering
	 */
	void removeProductOffering(String productOffId);

	void updateProductOffering(String poId, Update update);

	Map<String, String> fetchProductOfferingNamesByIds(Set<String> ids);

	long countProductOffering(Map<String, Object> requestParams) throws UnsupportedEncodingException;

	Map<String, Object> fetchProductOfferingWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
			throws UnsupportedEncodingException;
}
