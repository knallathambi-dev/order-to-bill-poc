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
import org.springframework.data.mongodb.core.query.Update;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;

/**
 * The Interface ProductSpecService for handling productSpecification crud
 * requests.
 *
 * @author Vivek Singh
 * @since 1.0
 */
public interface ProductSpecService {

	/**
	 * Save the productSpec instance in database.
	 *
	 * @param productSpecification the productSpecification
	 */
	void saveProductSpecification(final ProductSpecification productSpecification);

	/**
	 * update the productSpec instance in database as a patch.
	 *
	 * @param productSpecId  the productSpecificationId
	 * @param update the update
	 */
	void updateProductSpecification(final String productSpecId, final Update update);

	/**
	 * Fetches the List of productSpec on the basis of parameters given.
	 *
	 * @param requestParams Map of Parameters to search for.
	 * @param skip          the skip
	 * @param limit         the limit
	 * @return List of ProductSpecification
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	List<ProductSpecification> fetchProductSpecification(Map<String, Object> requestParams, Long skip, Long limit,String fields) throws UnsupportedEncodingException;


	/**
	 * Find serviceSpec instance w.r.t id parameter.
	 *
	 * @param id the id of productSpecification
	 * @return productSpecification product specification
	 */
	ProductSpecification fetchProductSpecificationById(String id);

	/**
	 * Find serviceSpec instance w.r.t id parameter and only show fields of fieldList of ProductSpecification.
	 *
	 * @param id the id of productSpecification
	 * @param fieldList the fieldList for productSpecification
	 * @return productSpecification product specification
	 */
	ProductSpecification fetchProductSpecificationById(String id, List<String> fieldList);

	/**
	 * remove productSpec instance from mongodb w.r.t id parameter.
	 *
	 * @param productSpecId the id of productSpecification
	 */
	void removeProductSpecification(String productSpecId);

	long countProductSpecification(Map<String, Object> requestParams) throws UnsupportedEncodingException;

	Map<String, Object> fetchProductSpecificationWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
			throws UnsupportedEncodingException;
}
