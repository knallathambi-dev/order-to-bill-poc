// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.controller;



import com.orange.discobole.permission.ComponentConfiguration;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.orange.role.service.ComponentConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "ComponentConfiguration")
@RestController
@RequestMapping(value = "/userRolePermission/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class ComponentConfigurationController {
    private static final Logger LOGGER = LogManager.getLogger(ComponentConfigurationController.class);


    private ComponentConfigurationService service;

    @Autowired
    public ComponentConfigurationController(ComponentConfigurationService service) {
        this.service = service;

    }

    @GetMapping("/componentConfiguration")
    public ResponseEntity<List<ComponentConfiguration>> getAll() {
        LOGGER.info("Get ComponentConfiguration");
        List<ComponentConfiguration> components = service.getComponentConfiguration();
        return ResponseEntity.ok(components);
    }

    @GetMapping("/componentConfiguration/{id}")
    public ResponseEntity<ComponentConfiguration> getById(@PathVariable String id) {
        ComponentConfiguration component = service.fetchComponentById(id);
        return ResponseEntity.status(HttpStatus.OK).body(component);
    }

    @PostMapping(path = "/componentConfiguration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ComponentConfiguration create(@RequestBody ComponentConfiguration componentConfiguration,
                                         @RequestHeader(name = "Token") String token) {
        return service.save(componentConfiguration);
    }

    @PutMapping("/componentConfiguration/{id}")
    public ResponseEntity<ComponentConfiguration> update(
            @PathVariable String id,
            @RequestBody ComponentConfiguration componentConfiguration) {

        ComponentConfiguration existingConfig = service.fetchComponentById(id);
        if (existingConfig != null) {
            existingConfig.setComponentName(componentConfiguration.getComponentName());
            ComponentConfiguration updatedConfig = service.save(existingConfig);
            return ResponseEntity.ok(updatedConfig);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/componentConfiguration/{id}")
    public ResponseEntity<String> deleteFunction(@PathVariable String id,
                                                 @RequestHeader(name = "Token") String token) {
        service.deleteComponent(id);
        return ResponseEntity.ok().build();
    }

}
