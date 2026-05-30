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

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.service.StockItemTypeService;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StockItemTypeController defines the endpoint to fetch stock item type
 * based on different params or specifically by id.
 *
 * @author Pankaj Gautam
 * @since 1.0
 */
@Tag(name = "stockItemType")
@RequestMapping(value = "/serviceCatalogManagement/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
// @PreAuthorize("hasAnyRole(@securityConfiguration.getRoles())")
public class StockItemTypeController {

    @Resource
    StockItemTypeService stockItemTypeService;

    /**
     * Find list of stock item type
     *
     * @param state			  		the state
     * @param name            		the name
     * @param startDateTime   		the start date time
     * @param endDateTime     		the end date time
     * @return the response entity of stockitemtype
     */
    @GetMapping("/stockItemType")
    public ResponseEntity<List<StockItemType>> findStockItemType(
    		@RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "lifecycleStatus", required = false) String state,
            @RequestParam(name = "name", required = false) String name,
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
        requestParams.put("validFor.startDateTime", startDateTime);
        requestParams.put("validFor.endDateTime", endDateTime);
        requestParams.put("orConditions", orConditions);
        requestParams.put("sort", sort);

        //long totalRecords = stockItemTypeService.countStockItemType(requestParams);
        //List<StockItemType> stockItemTypes = stockItemTypeService.fetchStockItemType(requestParams,offset, limit);
        
        Map<String, Object> stockItemTypesWithCunt = stockItemTypeService.fetchStockItemTypeWithCount(requestParams,offset, limit, fields) ;
        
        long totalRecords = (long) stockItemTypesWithCunt.get("count");
        @SuppressWarnings("unchecked")
		List<StockItemType> stockItemTypes = (List<StockItemType>) stockItemTypesWithCunt.get("data");
        
        
        if (null == stockItemTypes) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalRecords));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(stockItemTypes);
    }

    /**
     * Find StockItemType by id response entity.
     *
     * @param id the id
     * @param fields to filter the fields from StockItemType
     * @return the response entity of StockItemType
     */
    @GetMapping("/stockItemType/{id}")
    public ResponseEntity<StockItemType> findStockItemTypeById(@PathVariable final String id,
                                                                             @RequestParam(name = "fields", required = false) String fields) {
        StockItemType stockItemType;
        if (null == fields)
            stockItemType = stockItemTypeService.fetchStockItemTypeById(id);
        else {
            List<String> fieldList = Arrays.asList(fields.split(","));
            stockItemType = stockItemTypeService.fetchStockItemTypeById(id, fieldList);
        }
        if (null == stockItemType) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(stockItemType);
    }

}