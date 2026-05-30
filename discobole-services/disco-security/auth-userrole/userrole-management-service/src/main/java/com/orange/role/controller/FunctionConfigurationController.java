// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.controller;

import com.orange.discobole.permission.FunctionConfiguration;
import com.orange.role.service.FunctionConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FunctionConfiguration")
@RestController
@RequestMapping(value = "/userRolePermission/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class FunctionConfigurationController {
    private static final Logger LOGGER = LogManager.getLogger(FunctionConfigurationController.class);

    private FunctionConfigurationService service;
    @Autowired
    public FunctionConfigurationController(FunctionConfigurationService service) {
        this.service = service;
    }

    @GetMapping("/functionConfiguration")
    public ResponseEntity<List<FunctionConfiguration>> getAll() {
        LOGGER.info("Get FunctionConfiguration");
        List<FunctionConfiguration> functions = service.getFunctionConfiguration();
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/functionConfiguration/{id}")
    public ResponseEntity<FunctionConfiguration> getById(@PathVariable String id) {
        FunctionConfiguration function = service.fetchFunctionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(function);
    }

    @PostMapping(path = "/functionConfiguration", produces = MediaType.APPLICATION_JSON_VALUE)
    public FunctionConfiguration create(@RequestBody FunctionConfiguration functionConfiguration,
                                        @RequestHeader(name = "Token") String token) {
        return service.save(functionConfiguration);
    }

    @DeleteMapping("/functionConfiguration/{id}")
    public ResponseEntity<String> deleteFunction(@PathVariable String id,
                                                 @RequestHeader(name = "Token") String token) {
        service.deleteFunction(id);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/functionConfiguration/{id}")
    public ResponseEntity<FunctionConfiguration> update(
            @PathVariable String id,
            @RequestBody FunctionConfiguration functionConfiguration) {
        FunctionConfiguration existingConfig = service.fetchFunctionById(id);
        if (existingConfig != null) {
            existingConfig.setFunctionName(functionConfiguration.getFunctionName());
            FunctionConfiguration updatedConfig = service.save(existingConfig);
            return ResponseEntity.ok(updatedConfig);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
