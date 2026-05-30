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

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;

/**
 * The Interface StockItemTypeService for handling StockItemType crud requests.
 *
 * @author Varshika Choudhary
 * @since 1.0
 *
 */
public interface StockItemTypeService {
    /**
     * Save/update the StockItemType instance in database.
     *
     * @param StockItemType the stock item
     */
    void saveStockItemType(final StockItemType stockItemType);

    /**
     * Find StockItemType instance/s from database according to the query passed, which
     * is build on filter criteria.
     *
     * @param requestParams the query containing the filter criteria.
     * @return list of StockItemType matching the filter criteria
     */
    List<StockItemType> fetchStockItemType(Map<String, Object> requestParams) throws UnsupportedEncodingException;

     /**
     * Find StockItemType instance/s from database according to the query passed, which
     * is build on filter criteria.
     *
     * @param requestParams the query containing the filter criteria.
     * @return list of StockItemType matching the filter criteria
     */
    List<StockItemType> fetchStockItemType(Map<String, Object> requestParams,Long skip, Long limit) throws UnsupportedEncodingException;

    /**
     * Find serviceSpec instance  w.r.t id parameter.
     *
     * @param id the id of serviceSpecification
     * @return serviceSpecification
     */
    StockItemType fetchStockItemTypeById(String id);

    /**
     * Find serviceSpec instance  w.r.t id parameter.
     *
     * @param id the id of serviceSpecification
     * @return serviceSpecification
     */
    StockItemType fetchStockItemTypeById(String id, List<String> fieldList);

    long countStockItemType(Map<String, Object> requestParams) throws UnsupportedEncodingException;

	Map<String, Object> fetchStockItemTypeWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
			throws UnsupportedEncodingException;

}
