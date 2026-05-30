// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import jakarta.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.catalog.service.StockItemTypeService;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The StockItemTypeServiceImpl is implementation of StockItemTypeService
 *
 * @author Pankaj Gautam
 * @since 1.0
 * @see StockItemTypeService
 *
 */
@Service
public class StockItemTypeServiceImpl implements StockItemTypeService {

	private static final String STOCK_ITEM_TYPE = "stockItemType";
	@Resource
	private MongoTemplate mongoTemplate;

	public static final String VALID_FOR_START_DATE_TIME = "validFor.startDateTime";

	/**
	 * Save/update the stockItemType instance in database.
	 *
	 * @param stockItemType the serviceSpecification
	 */
	@Override
	public void saveStockItemType(StockItemType stockItemType) {
		stockItemType.setLastUpdate(OffsetDateTime.now());
		mongoTemplate.save(stockItemType);
	}

	/**
	 * Find StockItemType instance/s from database according to the query passed,
	 * which is build on filter criteria.
	 *
	 * @param requestParams the query containing the filter criteria.
	 * @return list of stockItemType matching the filter criteria
	 */
	@Override
	public List<StockItemType> fetchStockItemType(Map<String, Object> requestParams)
			throws UnsupportedEncodingException {

		Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);

		Query query = new Query();

		for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
		}
		return mongoTemplate.find(query, StockItemType.class);

	}

	/**
	 * Find StockItemType instance/s from database according to the query passed,
	 * which is build on filter criteria.
	 *
	 * @param requestParams the query containing the filter criteria.
	 * @return list of stockItemType matching the filter criteria
	 */

	@Override
	public List<StockItemType> fetchStockItemType(Map<String, Object> requestParams, Long skip, Long limit)
			throws UnsupportedEncodingException {
		Aggregation aggregation = QueryParamUtil.fetchEntityFiltered(requestParams, null, null, null, StockItemType.class);
		return aggregation != null
				? mongoTemplate.aggregate(aggregation, STOCK_ITEM_TYPE, StockItemType.class).getMappedResults()
				: mongoTemplate.findAll(StockItemType.class);
	}


	/**
	 * Find stockItemType instance w.r.t id parameter.
	 *
	 * @param id the id of stockItemType
	 * @return stockItemType
	 */
	@Override
	public StockItemType fetchStockItemTypeById(String id) {
		return mongoTemplate.findById(id, StockItemType.class);
	}

	/**
	 * Find stockItemType instance w.r.t id parameter and only show fields of
	 * fieldList of StockItemType.
	 *
	 * @param id        the id of stockItemType
	 * @param fieldList the fieldList for stockItemType
	 * @return stockItemType
	 */
	@Override
	public StockItemType fetchStockItemTypeById(String id, List<String> fieldList) {
		Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
		return mongoTemplate.findOne(query, StockItemType.class);
	}

	@Override
	public long countStockItemType(Map<String, Object> requestParams) throws UnsupportedEncodingException {
		return  QueryParamUtil.fetchCount(requestParams, STOCK_ITEM_TYPE, mongoTemplate, StockItemType.class);

	}

	@Override
	public Map<String, Object> fetchStockItemTypeWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
	        throws UnsupportedEncodingException {
	    return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, STOCK_ITEM_TYPE, StockItemType.class,mongoTemplate);
	}

}
