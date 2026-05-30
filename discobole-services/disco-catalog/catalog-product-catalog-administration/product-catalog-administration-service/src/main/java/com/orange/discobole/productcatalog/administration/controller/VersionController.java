// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;

import com.orange.disco.admin.Version;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Tag(name = "Version", description = "Returns the version of the service")
public interface VersionController {

    @Operation(operationId= "versionFind",summary = "Returns the version of the service",description = "The version of the API")
    @RequestMapping(method = {RequestMethod.GET}, value = {"/version"}, produces = {"application/json;charset=utf-8"})
    ResponseEntity<Version> getVersion();


}
