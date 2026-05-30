// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;

import com.orange.disco.admin.Currency;
import com.orange.discobole.productcatalog.administration.service.CurrencyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Currency", description = "Identifies the currency format")
public class currencyController {


    private CurrencyService currencyService;


    @Autowired
    public currencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/currency")
    public ResponseEntity<Currency> createCurrency(@RequestBody Currency currency) {
        return ResponseEntity.ok().body(this.currencyService.createCurrency(currency));
    }

    @GetMapping("/currency")
    public ResponseEntity <List< Currency >> getAllCurrency(
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "isDefault", required = false) Boolean isDefault
    ) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("sort", sort);
        requestParams.put("isDefault",isDefault);
        List<Currency> currencyList=currencyService.getAllCurrency(requestParams);
        if(currencyList.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok().body(currencyList);
    }

    @GetMapping("/currency/{id}")
    public ResponseEntity < Currency > getCurrencyById(@PathVariable String id) {
        return ResponseEntity.ok().body(currencyService.getCurrencyById(id));
    }

    @PutMapping("/currency/{id}")
    public ResponseEntity < Currency > updateCurrency(@PathVariable String id, @RequestBody Currency currency) {
        currency.setId(id);
        return ResponseEntity.ok().body(this.currencyService.updateCurrency(currency));
    }

    @DeleteMapping("/currency/{id}")
    public HttpStatus deleteCurrency(@PathVariable String id) {
        this.currencyService.deleteCurrency(id);
        return HttpStatus.OK;
    }




}
