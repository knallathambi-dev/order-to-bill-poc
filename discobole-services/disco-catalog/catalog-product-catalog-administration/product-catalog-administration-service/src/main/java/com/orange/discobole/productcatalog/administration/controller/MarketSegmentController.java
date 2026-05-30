// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;

import com.orange.disco.admin.Channel;
import com.orange.disco.admin.MarketSegment;
import com.orange.discobole.productcatalog.administration.service.MarketSegmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "MarketSegment", description = "Supported market segment")
@Validated
public class MarketSegmentController {



    private MarketSegmentService marketSegmentService;


    @Autowired
    public MarketSegmentController(MarketSegmentService marketSegmentService) {
        this.marketSegmentService = marketSegmentService;
    }

    @PostMapping("/marketSegment")
    public ResponseEntity<MarketSegment> createMarketSegment(@RequestBody(required = true) @Valid MarketSegment marketSegment) {
        return ResponseEntity.ok().body(this.marketSegmentService.createMarketSegment(marketSegment));
    }

    @GetMapping("/marketSegment")
    public ResponseEntity <List< MarketSegment >> getAllMarketSegment() {
        return ResponseEntity.ok().body(marketSegmentService.getAllMarketSegment());
    }

    @GetMapping("/marketSegment/{id}")
    public ResponseEntity < MarketSegment > getMarketSegmentById(@PathVariable String id) {
        return ResponseEntity.ok().body(marketSegmentService.getMarketSegmentById(id));
    }

    @PutMapping("/marketSegment/{id}")
    public ResponseEntity < MarketSegment > updateMarketSegment(@PathVariable String id, @RequestBody MarketSegment marketSegment) {
        marketSegment.setId(id);
        return ResponseEntity.ok().body(this.marketSegmentService.updateMarketSegment(marketSegment));
    }

    @DeleteMapping("/marketSegment/{id}")
    public HttpStatus deleteMarketSegment(@PathVariable String id) {
        this.marketSegmentService.deleteMarketSegment(id);
        return HttpStatus.OK;
    }

}
