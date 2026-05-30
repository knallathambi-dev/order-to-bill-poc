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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.service.StockItemService;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

/**
 * The StockItemServiceImpl is implementation of StockItem
 *
 * @author Varshika Choudhary
 * @since 1.0
 * @see StockItemService
 *
 */
@Service
public class StockItemServiceImpl implements StockItemService {

	private static final String STOCK_ITEM = "stockItem";
	@Resource
	private MongoTemplate mongoTemplate;

	public static final String VALID_FOR_START_DATE_TIME = "validFor.startDateTime";

	/**
	 * Save/update the serviceSpec instance in database.
	 *
	 * @param stockItem the serviceSpecification
	 */
	@Override
	public void saveStockItem(StockItem stockItem) {
		mongoTemplate.save(stockItem);
	}

	/**
	 * Find stockItem instance/s from database according to the query passed, which
	 * is build on filter criteria.
	 *
	 * @param requestParams the query containing the filter criteria.
	 * @return list of stockItem matching the filter criteria
	 */
	@Override
	public List<StockItem> fetchStockItem(Map<String, Object> requestParams) throws UnsupportedEncodingException {

		Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);

		Query query = new Query();

		for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
		}
		return mongoTemplate.find(query, StockItem.class);

	}

	/**
	 * Find stockItem instance/s from database according to the query passed, which
	 * is build on filter criteria.
	 *
	 * @param requestParams the query containing the filter criteria.
	 * @return list of stockItem matching the filter criteria
	 */
	@Override
	public List<StockItem> fetchStockItem(Map<String, Object> requestParams, Long skip, Long limit)
			throws UnsupportedEncodingException {

		Aggregation aggregation = QueryParamUtil.fetchEntityFiltered(requestParams, skip, limit, "", StockItem.class);
		return aggregation != null
				? mongoTemplate.aggregate(aggregation, STOCK_ITEM, StockItem.class).getMappedResults()
				: mongoTemplate.findAll(StockItem.class);
	}

	/**
	 * Find stockItem instance w.r.t id parameter.
	 *
	 * @param id the id of stockItem
	 * @return stockItem
	 */
	@Override
	public StockItem fetchStockItemById(String id) {
		return mongoTemplate.findById(id, StockItem.class);
	}

	/**
	 * Find stockItem instance w.r.t id parameter and only show fields of fieldList
	 * of stockItem.
	 *
	 * @param id        the id of stockItem
	 * @param fieldList the fieldList for stockItem
	 * @return stockItem
	 */
	@Override
	public StockItem fetchStockItemById(String id, List<String> fieldList) {
		Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
		return mongoTemplate.findOne(query, StockItem.class);
	}

	/**
	 * Find stockItem List w.r.t id parameter
	 *
	 * @param id the id of stockItemType
	 * @return list of stockItem matching the filter criteria
	 */
	@Override
	public List<StockItem> fetchStockItemByStockItemTypeId(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("stockItemType.id").is(id));
		return mongoTemplate.find(query, StockItem.class);
	}

	@Override
	public long countStockItem(Map<String, Object> requestParams) throws UnsupportedEncodingException {
	    return  QueryParamUtil.fetchCount(requestParams, STOCK_ITEM, mongoTemplate, StockItem.class);
	}

	@Override
	public Map<String, Object> fetchStockItemWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
	        throws UnsupportedEncodingException {
	    return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, STOCK_ITEM, StockItem.class, mongoTemplate);
	}

}
