// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;
import com.orange.disco.admin.FrequencyTypes;
import com.orange.discobole.productcatalog.administration.service.FrequencyTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @CrossOrigin(origins = "*")
@RestController
@Tag(name = "FrequencyTypes", description = "Identifies the types of frequencies defined in the product catalog entities")
public class FrequencyTypeController {


    private final FrequencyTypeService frequencyTypeService;

    @Autowired
    public FrequencyTypeController(FrequencyTypeService frequencyTypeService) {
        this.frequencyTypeService = frequencyTypeService;
    }

    @PostMapping("/frequencyTypes")
    public ResponseEntity<FrequencyTypes> createFrequencyType(@RequestBody FrequencyTypes frequencyType) {
        return ResponseEntity.ok().body(this.frequencyTypeService.createFrequencyTypes(frequencyType));
    }

    @GetMapping("/frequencyTypes")
    public ResponseEntity <List< FrequencyTypes >> getAllFrequencyTypes() {
        return ResponseEntity.ok().body(frequencyTypeService.getAllFrequencyTypes());
    }

    @GetMapping("/frequencyTypes/{id}")
    public ResponseEntity < FrequencyTypes > getfrequencyTypesById(@PathVariable String id) {
        return ResponseEntity.ok().body(frequencyTypeService.getFrequencyTypesById(id));
    }

    @PutMapping("/frequencyTypes/{id}")
    public ResponseEntity < FrequencyTypes > updatefrequencyType(@PathVariable String id, @RequestBody FrequencyTypes frequencyType) {
        frequencyType.setId(id);
        return ResponseEntity.ok().body(this.frequencyTypeService.updateFrequencyTypes(frequencyType));
    }

    @DeleteMapping("/frequencyTypes/{id}")
    public HttpStatus deletefrequencyType(@PathVariable String id) {
        this.frequencyTypeService.deleteFrequencyType(id);
        return HttpStatus.OK;
    }
}
