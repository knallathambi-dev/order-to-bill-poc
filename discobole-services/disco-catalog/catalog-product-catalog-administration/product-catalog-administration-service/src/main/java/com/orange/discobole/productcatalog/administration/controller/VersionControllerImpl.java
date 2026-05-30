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
import com.orange.disco.admin.VersionNumber;
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
public class VersionControllerImpl implements VersionController{

    @Resource
    private AppProperties appConfig;


    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Version.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))}),
            @ApiResponse(responseCode = "400", content = {@Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))}),
            @ApiResponse(responseCode = "503", content = {@Content(mediaType = "application/json;charset=utf-8", schema = @Schema(implementation = Error.class))})}
    )


    @Override
    public ResponseEntity<Version> getVersion() {
        String swaggerVersion = "5.4.0";
        String[] versionParts = swaggerVersion.split("\\.");
        int major = Integer.parseInt(versionParts[0]);
        int minor = Integer.parseInt(versionParts[1]);
        int patch = Integer.parseInt(versionParts[2]);


        VersionNumber versionNumber = new VersionNumber()
                .major(major)
                .minor(minor)
                .patch(patch);

        return ResponseEntity.ok(new Version(versionNumber, Boolean.FALSE, null));
    }

}
