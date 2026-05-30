// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;


import com.orange.disco.admin.CFSRelationshipRestriction;
import com.orange.discobole.productcatalog.administration.service.CFSRelationshipRestrictionService;
import com.orange.discobole.productcatalog.administration.service.ProductInventoryConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "CFSRelationshipRestriction", description = "Enables/disables the relationship restriction from CFS level to PS.")
public class CFSRelationshipRestrictionController {

    private CFSRelationshipRestrictionService cfsRelationshipRestrictionService;

    @Autowired
    public CFSRelationshipRestrictionController(CFSRelationshipRestrictionService cfsRelationshipRestrictionService) {
        this.cfsRelationshipRestrictionService = cfsRelationshipRestrictionService;
    }

    @PostMapping("/cfsRelationshipRestriction")
    public ResponseEntity<CFSRelationshipRestriction> createCFSRelationship(@RequestBody CFSRelationshipRestriction cfsRelationship) {
        return ResponseEntity.ok().body(this.cfsRelationshipRestrictionService.createCFSRelationship(cfsRelationship));
    }


    @GetMapping("/cfsRelationshipRestriction")
    public ResponseEntity <List<CFSRelationshipRestriction>> getAllCFSRelationship() {
        return ResponseEntity.ok().body(cfsRelationshipRestrictionService.getAllCFSRelationship());
    }

    @GetMapping("/cfsRelationshipRestriction/{id}")
    public ResponseEntity <CFSRelationshipRestriction> getCFSRelationshipById(@PathVariable String id) {
        return ResponseEntity.ok().body(cfsRelationshipRestrictionService.getCFSRelationshipById(id));
    }

    @PutMapping("/cfsRelationshipRestriction/{id}")
    public ResponseEntity <CFSRelationshipRestriction> updateCFSRelationship(@PathVariable String id, @RequestBody CFSRelationshipRestriction cfsRelationship) {
        cfsRelationship.setId(id);
        return ResponseEntity.ok().body(this.cfsRelationshipRestrictionService.updateCFSRelationship(cfsRelationship));
    }


}
