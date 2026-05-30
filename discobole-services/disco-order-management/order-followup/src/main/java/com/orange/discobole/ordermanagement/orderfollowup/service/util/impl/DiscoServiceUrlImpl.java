// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderfollowup.service.util.impl;

import com.orange.discobole.ordermanagement.orderfollowup.config.ApplicationConfigProperties;
import com.orange.discobole.ordermanagement.orderfollowup.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.orderfollowup.service.util.DiscoServiceUrl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class DiscoServiceUrlImpl implements DiscoServiceUrl {
    private final ApplicationConfigProperties applicationConfigProperties;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public DiscoServiceUrlImpl(ApplicationConfigProperties applicationConfigProperties) {
        this.applicationConfigProperties = applicationConfigProperties;
    }

    @Override
    public String getProductInventoryUrl() {
        log.debug("Getting product inventory url");
        String endPoint = applicationConfigProperties.getProductInventoryUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getProductInventoryByIdUrl(String id) {
        log.debug("Getting product inventory url by id : {} ", id);
        String endPoint = applicationConfigProperties.getProductInventoryUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }
}