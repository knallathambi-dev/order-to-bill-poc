// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.catalog.service.ServiceSpecService;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

/**
 * The ServiceSpecServiceImpl is implementation of ServiceSpecService
 * 
 * @author Vivek Singh
 * @since 1.0
 * @see ServiceSpecService
 *
 */
@Service
public class ServiceSpecServiceImpl implements ServiceSpecService {

	@Resource
	private MongoTemplate mongoTemplate;

	public static final String VALID_FOR_START_DATE_TIME = "validFor.startDateTime";

	public static final String SSPEC = "serviceSpecification";

	/**
	 * Save/update the serviceSpec instance in database.
	 *
	 * @param serviceSpecification the serviceSpecification
	 */
	@Override
	public void saveServiceSpecification(ServiceSpecification serviceSpecification) {
		mongoTemplate.save(serviceSpecification);
	}

	/**
	 * Find serviceSpec instance/s from database according to the query passed,
	 * which is build on filter criteria.
	 *
	 * @param requestParams the query containing the filter criteria.
	 * @return list of serviceSpecification matching the filter criteria
	 */
	@Override
	public List<ServiceSpecification> fetchServiceSpecifications(Map<String, Object> requestParams)
			throws UnsupportedEncodingException {

		Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);

		Query query = new Query();

		for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
		}
		return mongoTemplate.find(query, ServiceSpecification.class);

	}

	/**
	 * Find serviceSpec instance/s from database according to the query passed,
	 * which is build on filter criteria.
	 *
	 * @param requestParams the query containing the filter criteria.
	 * @return list of serviceSpecification matching the filter criteria
	 */

	@Override
	public List<ServiceSpecification> fetchServiceSpecifications(Map<String, Object> requestParams, Long skip,
			Long limit) throws UnsupportedEncodingException {

		Aggregation aggregation = QueryParamUtil.fetchEntityFiltered(requestParams,skip,limit, "", ServiceSpecification.class);
		return aggregation != null
				? mongoTemplate.aggregate(aggregation, SSPEC, ServiceSpecification.class).getMappedResults()
				: mongoTemplate.findAll(ServiceSpecification.class);
	}


	@Override
	public long countServiceSpecification(Map<String, Object> requestParams) throws UnsupportedEncodingException {
		return  QueryParamUtil.fetchCount(requestParams, SSPEC, mongoTemplate, ServiceSpecification.class);

	}

	/**
	 * Find serviceSpec instance w.r.t id parameter.
	 *
	 * @param id the id of serviceSpecification
	 * @return serviceSpecification
	 */
	@Override
	public ServiceSpecification fetchServiceSpecificationById(String id) {
		return mongoTemplate.findById(id, ServiceSpecification.class);
	}

	/**
	 * Find serviceSpecification instance w.r.t id parameter and only show fields of
	 * fieldList of serviceSpecification.
	 *
	 * @param id        the id of serviceSpecification
	 * @param fieldList the fieldList for serviceSpecification
	 * @return serviceSpecification service Specification
	 */
	@Override
	public ServiceSpecification fetchServiceSpecificationById(String id, List<String> fieldList) {
		Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
		return mongoTemplate.findOne(query, ServiceSpecification.class);
	}

	@Override
	public Map<String, Object> fetchServiceSpecificationWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
	        throws UnsupportedEncodingException {
	    return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, SSPEC, ServiceSpecification.class, mongoTemplate);
	}
}
