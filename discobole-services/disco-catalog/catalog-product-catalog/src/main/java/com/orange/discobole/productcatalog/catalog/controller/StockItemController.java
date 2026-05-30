// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.service.StockItemService;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StockItemController defines the endpoint to fetch stock item
 * based on different params or specifically by id.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
@Tag(name = "stockItem")
@RequestMapping(value = "/serviceCatalogManagement/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
// @PreAuthorize("hasAnyRole(@securityConfiguration.getRoles())")
public class StockItemController {

    @Resource
    StockItemService stockItemService;

    /**
     * Find list of stock item
     *
     * @param state			  		the state
     * @param name            		the name
     * @param EAN	          		the EAN
     * @param stockItemTypeId    	the stock item type id
	 * @param stockItemTypeName  	the stock item type name
     * @param startDateTime   		the start date time
     * @param endDateTime     		the end date time
     * @return the response entity of stockitem
     */
    @GetMapping("/stockItem")
    public ResponseEntity<List<StockItem>> findStockItem(
    		@RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "EAN", required = false) String ean,
			@RequestParam(name = "StockItemType.id", required = false) final String stockItemTypeId,
			@RequestParam(name = "StockItemType.name", required = false) final String stockItemTypeName,
            @RequestParam(name = "offset", required = false) Long offset,
            @RequestParam(name = "limit", required = false) Long limit,
            @RequestParam(name = "validFor.startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDateTime,
            @RequestParam(name = "validFor.endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDateTime,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "orConditions", required = false) String orConditions,
            @RequestParam(name = "sort", required = false) String sort)


    throws UnsupportedEncodingException {

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("_id", id);
        requestParams.put("state", state);
        requestParams.put("name", name);
        requestParams.put("EAN", ean);
		requestParams.put("stockItemType._id", stockItemTypeId);
		requestParams.put("stockItemType.name", stockItemTypeName);
		requestParams.put("validFor.startDateTime", startDateTime);
        requestParams.put("validFor.endDateTime", endDateTime);
        requestParams.put("orConditions", orConditions);
        requestParams.put("sort", sort);
        //long totalRecords = stockItemService.countStockItem(requestParams);

        //List<StockItem> stockItems = stockItemService.fetchStockItem(requestParams,offset, limit);

		Map<String, Object> stockItemWithCunt = stockItemService.fetchStockItemWithCount(requestParams,offset, limit, fields) ;
        
        long totalRecords = (long) stockItemWithCunt.get("count");
        @SuppressWarnings("unchecked")
		List<StockItem> stockItems = (List<StockItem>) stockItemWithCunt.get("data");
        
        
        if (null == stockItems) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalRecords));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(stockItems);
    }

    /**
     * Find StockItem by id response entity.
     *
     * @param id the id
     * @param fields to filter the fields from StockItem
     * @return the response entity of StockItem
     */
    @GetMapping("/stockItem/{id}")
    public ResponseEntity<StockItem> findStockItemById(@PathVariable final String id,
                                                                             @RequestParam(name = "fields", required = false) String fields) {
        StockItem stockItem;
        if (null == fields)
            stockItem = stockItemService.fetchStockItemById(id);
        else {
            List<String> fieldList = Arrays.asList(fields.split(","));
            stockItem = stockItemService.fetchStockItemById(id, fieldList);
        }
        if (null == stockItem) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(stockItem);
    }
    
    /**
     * Find list of stock item
     *
     * @param id the id
     * @return the response entity of StockItem
     */
    @GetMapping("/stockItem/stockItemType/{id}")
    public ResponseEntity<List<StockItem>> fetchStockItemByStockItemTypeId(@PathVariable final String id,
                                                                             @RequestParam(name = "fields", required = false) String fields) {


        List<StockItem> stockItems = stockItemService.fetchStockItemByStockItemTypeId(id);
        if (null == stockItems) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(stockItems);
    }

}