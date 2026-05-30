// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.orange.discobole.ordermanagement.orderinventory.api.v1.DocApi;
import com.orange.discobole.ordermanagement.orderinventory.constant.ErrorCodeEnum;
import com.orange.discobole.ordermanagement.orderinventory.constant.ErrorMessages;
import com.orange.discobole.ordermanagement.orderinventory.exception.ProductOrderInventoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Controller
public class DocControllerImpl implements DocApi {

    @Value("${api.docs.path:static/api-docs/order-inventory-spec.yaml}")
    private String yamlFilePath;

    private final Logger log = LoggerFactory.getLogger(DocControllerImpl.class);

    @Override
    public ResponseEntity<Object> getApiDocumentation() {
        log.info("Starting to fetch service documentation");

        try {
            log.debug("Looking for YAML file at path: {}", yamlFilePath);
            ClassPathResource resource = new ClassPathResource(yamlFilePath);

            if (!resource.exists()) {
                log.error("YAML file not found at path: {}", yamlFilePath);
                throw new ProductOrderInventoryException(HttpStatus.NOT_FOUND, ErrorCodeEnum.RESOURCE_NOT_FOUND.getCode(),
                        ErrorCodeEnum.RESOURCE_NOT_FOUND.getStatus(), ErrorMessages.YAML_NOT_FOUND);
            }

            log.info("YAML file found, starting to read and convert");
            try (InputStream inputStream = resource.getInputStream()) {
                ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());

                Map<String, Object> yamlContent = yamlReader.readValue(inputStream, new TypeReference<Map<String, Object>>() {
                });

                log.info("Successfully converted YAML to object");
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(yamlContent);
            }
        } catch (IOException e) {
            log.error("Error occurred while processing the service documentation", e);
            throw new ProductOrderInventoryException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodeEnum.INTERNAL_ERROR.getCode(),
                    ErrorCodeEnum.INTERNAL_ERROR.getStatus(), ErrorMessages.PROCESSING_ERROR);
        }
    }
}