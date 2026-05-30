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
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProceesEntityCleanUpDTO;
import com.orange.discobole.productcatalog.catalog.service.MongodbDataFilterService;


@RestController
@Tag(name="cleanUp")
@RequestMapping(value = "/productCatalogManagement/v1/productCatalog/clean", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessEntityCleanUpController {
    @Resource
    private MongodbDataFilterService mongodbDataFilterService;
    @DeleteMapping
    public String deleteProcessEntity(@Valid @RequestBody ProceesEntityCleanUpDTO processEntityCleanUp){
       return mongodbDataFilterService.processEntityCleanUp(processEntityCleanUp);
    }

}
