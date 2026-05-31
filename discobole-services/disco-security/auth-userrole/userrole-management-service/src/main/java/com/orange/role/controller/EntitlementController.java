// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.controller;

import com.orange.discobole.permission.Entitlement;
import com.orange.role.service.EntitlementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Entitlement")
@RestController
@RequestMapping(value = "/userRolePermission/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class EntitlementController {
    private static final Logger LOGGER = LogManager.getLogger(EntitlementController.class);

    private EntitlementService entitlementService;
    @Autowired
    public EntitlementController(EntitlementService entitlementService) {
        this.entitlementService = entitlementService;
    }
    @GetMapping("/entitlement")
    public ResponseEntity<?> getEntitlements(@RequestParam(name = "id", required = false) String id,
                                             @RequestParam(name = "fields", required = false) String fields,
                                             @RequestParam(name = "offset", required = false) Long offset,
                                             @RequestParam(name = "limit", required = false) Long limit) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("_id", id);
        try {
            List<Entitlement> entitlements = entitlementService.fetchEntitlements(requestParams, offset, limit, fields);
            return ResponseEntity.ok(entitlements);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch entitlements: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/entitlement")
    public ResponseEntity<?> createEntitlements(@RequestBody List<Entitlement> entitlements,
                                                @RequestHeader(name = "Token") String token) {
        LOGGER.info("Create createEntitlements Data ");
        try {
            List<Entitlement> entitlementList = entitlementService.createEntitlement(entitlements,token);
            return ResponseEntity.status(HttpStatus.CREATED).body(entitlementList);
        } catch (Exception e) {
            LOGGER.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/entitlement/{id}")
    public ResponseEntity<String> deleteEntitlement(@PathVariable String id,
                                            @RequestHeader(name = "Token") String token) {
        entitlementService.deleteEntitlement(id,token);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/entitlement/{id}")
    public ResponseEntity<Entitlement> update(
            @PathVariable String id,
            @RequestBody Entitlement entitlement) {

        Entitlement existingEntitlement = entitlementService.fetchEntitlementById(id);
        if(existingEntitlement!=null){
            existingEntitlement.setAction(entitlement.getAction());

            Entitlement updatedEntitlement = entitlementService.updateEntitlement(existingEntitlement,id);
            return ResponseEntity.ok(updatedEntitlement);
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/entitlement/{id}")
    public ResponseEntity<Entitlement> entitlementById(@PathVariable String id,
                                                 @RequestParam(name = "fields", required = false) String fields) {
        Entitlement entitlement;
        LOGGER.info("Entered into entitlementById Method");
        if (null == fields)
            entitlement = entitlementService.fetchEntitlementById(id);
        else {
            List<String> fieldList = Arrays.asList(fields.split(","));
            entitlement = entitlementService.fetchEntitlementById(id, fieldList);
        }
        if (null == entitlement) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(entitlement);
    }
}
