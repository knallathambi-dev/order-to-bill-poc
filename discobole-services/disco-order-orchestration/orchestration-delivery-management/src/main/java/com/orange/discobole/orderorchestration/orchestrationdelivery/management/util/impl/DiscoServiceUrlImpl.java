// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.util.impl;


 import com.orange.discobole.orderorchestration.orchestrationdelivery.management.config.ApplicationConfigProperties;
 import com.orange.discobole.orderorchestration.orchestrationdelivery.management.util.DiscoServiceUrl;
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
    public String getServiceOrderingUrl() {
        log.debug("Getting service ordering url from app.yml");
        String endPoint = applicationConfigProperties.getServiceOrderingUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getServiceCatalogManagementUrl() {
        log.debug("Getting service catalog url from app.yml");
        String endPoint = applicationConfigProperties.getServiceCatalogManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getShippingOrderUrl() {
        log.debug("Getting shipping order url from app.yml");
        String endPoint = applicationConfigProperties.getShippingOrderUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }
}
