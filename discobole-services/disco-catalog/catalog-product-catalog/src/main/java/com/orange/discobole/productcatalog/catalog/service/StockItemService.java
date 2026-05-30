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

import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;

/**
 * The Interface StockItemService for handling stockItem crud requests.
 *
 * @author Varshika Choudhary
 * @since 1.0
 *
 */
public interface StockItemService {
    /**
     * Save/update the stockItem instance in database.
     *
     * @param stockItem the stock item
     */
    void saveStockItem(final StockItem stockItem);
    
    /**
     * Find stockItem instance/s from database according to the query passed, which
     * is build on filter criteria.
     *
     * @param requestParams the query containing the filter criteria.
     * @return list of stockItem matching the filter criteria
     */
    List<StockItem> fetchStockItem(Map<String, Object> requestParams) throws UnsupportedEncodingException;

    /**
     * Find stockItem instance/s from database according to the query passed, which
     * is build on filter criteria.
     *
     * @param requestParams the query containing the filter criteria.
     * @return list of stockItem matching the filter criteria
     */
    List<StockItem> fetchStockItem(Map<String, Object> requestParams,Long skip, Long limit) throws UnsupportedEncodingException;

    /**
     * Find stockItem instance  w.r.t id parameter.
     *
     * @param id the id of stockItem
     * @return stockItem
     */
    StockItem fetchStockItemById(String id);

    /**
     * Find stockItem instance  w.r.t id parameter.
     *
     * @param id the id of stockItem
     * @return stockItem
     */
    StockItem fetchStockItemById(String id, List<String> fieldList);
    
    /**
     * Find stockItem instance  w.r.t id parameter.
     *
     * @param id the id of stockItemType
     * @return list of stockItem
     */
    List<StockItem> fetchStockItemByStockItemTypeId(String id);

    long countStockItem(Map<String, Object> requestParams) throws UnsupportedEncodingException;

	Map<String, Object> fetchStockItemWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
			throws UnsupportedEncodingException;
}
