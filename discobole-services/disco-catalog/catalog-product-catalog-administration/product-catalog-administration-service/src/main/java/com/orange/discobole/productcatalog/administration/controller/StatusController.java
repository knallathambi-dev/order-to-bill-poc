// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;

import com.orange.disco.admin.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

    @Tag(name = "Status", description = "Return the status of the service (health check)")
    public interface StatusController {

        @Operation(operationId = "StatusFind" ,summary = "find status",description = "Return the status of the service (health check)")
        @RequestMapping(method = {RequestMethod.GET}, value = {"/status"}, produces = {"application/json;charset=utf-8"})
        ResponseEntity<Status> getStatus();
    }

