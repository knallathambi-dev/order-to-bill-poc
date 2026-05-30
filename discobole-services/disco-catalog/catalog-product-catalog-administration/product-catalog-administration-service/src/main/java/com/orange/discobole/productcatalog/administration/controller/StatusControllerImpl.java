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
import com.orange.discobole.productcatalog.administration.config.AppProperties;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class StatusControllerImpl implements StatusController {

    @Resource
    private AppProperties appConfig;


    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Status.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))}),
            @ApiResponse(responseCode = "400", content = {@Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))}),
            @ApiResponse(responseCode = "503", content = {@Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))})}
    )

    @Override
    public ResponseEntity<Status> getStatus() {
//
        return ResponseEntity.ok(new Status(appConfig.getName(), "OK", appConfig.getVersion(),null,null,null));
    }

}
