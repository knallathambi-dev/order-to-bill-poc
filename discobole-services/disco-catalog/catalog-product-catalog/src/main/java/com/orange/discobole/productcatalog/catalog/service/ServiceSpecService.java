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

import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;

/**
 * The Interface ServiceSpecService for handling serviceSpecification crud requests.
 * 
 * @author Vivek Singh
 * @since 1.0
 *
 */

public interface ServiceSpecService {

	/**
	 * Save/update the serviceSpec instance in database.
	 * 
	 * @param serviceSpecification the serviceSpecification
	 */
	void saveServiceSpecification(final ServiceSpecification serviceSpecification);

	/**
	 * Find serviceSpec instance/s from database according to the query passed, which
	 * is build on filter criteria.
	 * 
	 * @param requestParams the query containing the filter criteria.
	 * @return list of serviceSpecification matching the filter criteria
	 */
	List<ServiceSpecification> fetchServiceSpecifications(Map<String, Object> requestParams) throws UnsupportedEncodingException;

	/**
	 * Find serviceSpec instance  w.r.t id parameter.
	 * 
	 * @param id the id of serviceSpecification
	 * @return serviceSpecification
	 */
	ServiceSpecification fetchServiceSpecificationById(String id);

	/**
	 * Find serviceSpec instance  w.r.t id parameter.
	 *
	 * @param id the id of serviceSpecification
	 * @return serviceSpecification
	 */
	ServiceSpecification fetchServiceSpecificationById(String id, List<String> fieldList);

	/**
	 * Find serviceSpec instance/s from database according to the query passed, which
	 * is build on filter criteria.
	 * 
	 * @param requestParams the query containing the filter criteria.
	 * @return list of serviceSpecification matching the filter criteria
	 */

	List<ServiceSpecification> fetchServiceSpecifications(Map<String, Object> requestParams,Long skip, Long limit) throws UnsupportedEncodingException;

	long countServiceSpecification(Map<String, Object> requestParams) throws UnsupportedEncodingException;

	Map<String, Object> fetchServiceSpecificationWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
			throws UnsupportedEncodingException;
}
