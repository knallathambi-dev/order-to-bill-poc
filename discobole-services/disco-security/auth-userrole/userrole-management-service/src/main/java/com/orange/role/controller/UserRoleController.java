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
import com.orange.discobole.permission.UserRole;
import com.orange.role.handler.DiscoClientException;
import com.orange.role.service.UserRoleService;
import com.orange.role.util.QueryParamUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UserRoleCreation defines the endpoint to create the user roles for ODACAT
 * components.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */


@Tag(name = "UserRole")
@RestController
@RequestMapping(value = "/userRolePermission/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRoleController {

    public static final String UNEXPECTED_ERROR = "Unexpected error: {}";
    private static final Logger LOGGER = LogManager.getLogger(UserRoleController.class);
    @Value("${spring.admin-role}")
    private String superAdmin;
    private UserRoleService userRoleService;
    @Autowired
    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }
    @PostMapping(path = "/userRole", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createRoles(@RequestBody UserRole userRole,
                                         @Parameter(hidden = true) @RequestHeader(value = "Authorization")
                                         String authorization)
    {

        try {
            UserRole response = userRoleService.createUserRole(userRole, authorization);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            LOGGER.error(UNEXPECTED_ERROR, e.getMessage(), e);
            throw new DiscoClientException(e.getMessage());
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping(value = "/userRole/{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRole(@RequestBody UserRole userRole,
                                        @PathVariable String roleId) {
        try {
            userRoleService.updateUserRole(userRole, roleId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOGGER.error(UNEXPECTED_ERROR, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/userRole/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable String roleId,
                                             @Parameter(hidden = true) @RequestHeader(value = "Authorization")
                                             String authorization) {
        LOGGER.info("Method deleteRole -> delete Role : {} ", roleId);
        try {
            userRoleService.deleteRole(roleId, authorization);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            LOGGER.error(UNEXPECTED_ERROR, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/userRole")
    public ResponseEntity<List<UserRole>> findUserRoles(
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "involvementRole", required = false) String involvementRole,
            @RequestParam(name = "@type", required = false) String type,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "offset", required = false) Long offset,
            @RequestParam(name = "limit", required = false) Long limit,
            @RequestParam(name = "component", required = false) String component,
            @RequestParam(name = "entitlement.id", required = false) String entitlementId,
            @RequestParam(name = "entitlement.action", required = false) String entitlementAction,
            @RequestParam(name = "entitlement.function", required = false) String entitlementFunction,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = true) String token) throws UnsupportedEncodingException {
        String convertedToken = String.join(",", QueryParamUtil.convert(token));
        HttpHeaders headers = new HttpHeaders();
        long totalRecords = 0;

        List<UserRole> userRole = new ArrayList<>();
        if(StringUtils.isBlank(involvementRole)){
            if(!convertedToken.contains(superAdmin)){
                involvementRole = convertedToken;
            }
        } else {
            if(!convertedToken.contains(superAdmin)){
                List<String> involvementRoleList = Arrays.asList(involvementRole.split(","));
                List<String> convertedTokenList = Arrays.asList(convertedToken.split(","));
                involvementRole = convertedTokenList.stream()
                        .filter(involvementRoleList::contains)
                        .collect(Collectors.joining(","));
            }
            if(StringUtils.isBlank(involvementRole)){
                headers.add("X-Total-Count", String.valueOf(totalRecords));
                return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).body(userRole);
            }
        }
        if(StringUtils.isBlank(involvementRole)){
            if(!convertedToken.contains(superAdmin)){
                involvementRole = convertedToken;
            }
        }
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("_id", id);
        requestParams.put("involvementRole", involvementRole);
        requestParams.put("type", type);
        requestParams.put("component", component);
        requestParams.put("entitlement.id", entitlementId);
        requestParams.put("entitlement.action", entitlementAction);
        requestParams.put("entitlement.function", entitlementFunction);
        requestParams.put("fields", fields);

        Map<String, Object> roleWithCunt = userRoleService.fetchCategoryWithCount(requestParams,offset, limit, fields) ;
        totalRecords = (long) roleWithCunt.get("count");
        userRole = (List<UserRole>) roleWithCunt.get("data");

        if (userRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        headers.add("X-Total-Count", String.valueOf(totalRecords));
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(userRole);
    }

    @GetMapping("/userRole/{id}")
    public ResponseEntity<UserRole> userRoleById(@PathVariable String id,
                                                 @RequestParam(name = "fields", required = false) String fields) {
        UserRole userRole;
        LOGGER.info("Entered into userRoleById Method");
        if (null == fields)
            userRole = userRoleService.fetchUserRoleById(id);
        else {
            List<String> fieldList = Arrays.asList(fields.split(","));
            userRole = userRoleService.fetchUserRoleById(id, fieldList);
        }
        if (null == userRole) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(userRole);
    }

}
