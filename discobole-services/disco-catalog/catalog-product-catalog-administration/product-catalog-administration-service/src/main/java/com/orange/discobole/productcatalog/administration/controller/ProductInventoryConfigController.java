// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;

import com.orange.disco.admin.ProductInventoryConfiguration;
import com.orange.discobole.productcatalog.administration.service.ProductInventoryConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;


@RestController
@Tag(name = "ProductInventoryConfiguration", description = "Identifies if product inventory is to be checked before change in lifecycle")
public class ProductInventoryConfigController {


    private ProductInventoryConfigService cpibConfigService;

    @Autowired
    public ProductInventoryConfigController(ProductInventoryConfigService cpibConfigService) {
        this.cpibConfigService = cpibConfigService;
    }

    @PostMapping("/productInventoryCheck")
    public ResponseEntity<ProductInventoryConfiguration> createCPIBConfig(@RequestBody ProductInventoryConfiguration cpibConfiguration) {
        return ResponseEntity.ok().body(this.cpibConfigService.createCPIBConfig(cpibConfiguration));
    }


    @GetMapping("/productInventoryCheck")
    public ResponseEntity <List<ProductInventoryConfiguration>> getAllCPIBConfig() {
        return ResponseEntity.ok().body(cpibConfigService.getAllCPIBConfig());
    }

    @GetMapping("/productInventoryCheck/{id}")
    public ResponseEntity <ProductInventoryConfiguration> getCPIBConfigById(@PathVariable String id) {
        return ResponseEntity.ok().body(cpibConfigService.getCPIBConfigById(id));
    }

    @PutMapping("/productInventoryCheck/{id}")
    public ResponseEntity <ProductInventoryConfiguration> updateCPIBConfig(@PathVariable String id, @RequestBody ProductInventoryConfiguration cpibConfiguration) {
        cpibConfiguration.setId(id);
        return ResponseEntity.ok().body(this.cpibConfigService.updateCPIBConfig(cpibConfiguration));
    }

    @DeleteMapping("/productInventoryCheck/{id}")
    public HttpStatus deleteCPIBConfig(@PathVariable String id) {
        this.cpibConfigService.deleteCPIBConfig(id);
        return HttpStatus.OK;
    }

}
