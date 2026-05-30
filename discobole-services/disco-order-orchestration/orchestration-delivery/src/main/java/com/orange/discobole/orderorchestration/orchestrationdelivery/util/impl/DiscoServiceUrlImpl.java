// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.config.ApplicationConfigProperties;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.DiscoServiceUrl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DiscoServiceUrlImpl implements DiscoServiceUrl {

    public static final String ID_URI = "/{id}";

    private final ApplicationConfigProperties applicationConfigProperties;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public DiscoServiceUrlImpl(ApplicationConfigProperties applicationConfigProperties) {
        this.applicationConfigProperties = applicationConfigProperties;
    }

    @Override
    public String getProductSpecByIdUrl(String id) {
        String endPoint = applicationConfigProperties.getProductSpecByIdCatalogUrl();
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ID_URI)
                .buildAndExpand(id)
                .toUriString();
        log.info("DiscoServiceUrlImpl | getProductSpecByIdUrl | Getting product specification url for id : {} using endpoint {} full uri {}", id, endPoint, uriString);
        return uriString;

    }

    @Override
    public String getProductSpecificationsByIdsUrl(List<String> ids) {
        String endPoint = applicationConfigProperties.getProductSpecByIdCatalogUrl();
        String idsQueryParams = ids.stream()
                .map(id -> "id=" + UriUtils.encodeQueryParam(id, StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .query(idsQueryParams)
                .build()
                .toUriString();
        log.info("DiscoServiceUrlImpl | getProductSpecificationsByIdsUrl | Getting product specification url for ids : {} using endpoint {} full uri {}", ids, endPoint, uriString);
        return uriString;
    }

    @Override
    public String createFallout() {
        String endPoint = applicationConfigProperties.getFalloutUrl();
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
        log.info("DiscoServiceUrlImpl | createFallout | Creating fallout using uri {}", uriString);
        return uriString;
    }

    @Override
    public String getFalloutById(String id) {
        String endPoint = applicationConfigProperties.getFalloutUrl();
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ID_URI)
                .buildAndExpand(id)
                .toUriString();

        log.info("DiscoServiceUrlImpl | getProcessFlow | Getting fallout url for id : {} using endpoint {} full uri {}", id, endPoint, uriString);
        return uriString;
    }

    @Override
    public String getFalloutByRelatedEntityId(String relatedEntityId) {
        String endPoint = applicationConfigProperties.getFalloutUrl();
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .queryParam("relatedEntity.id", relatedEntityId)
                .toUriString();

        log.info("DiscoServiceUrlImpl | getProcessFlow | Getting fallout url for related entity id : {} using endpoint {} full uri {}", relatedEntityId, endPoint, uriString);
        return uriString;
    }

    @Override
    public String getProductManagementUrl() {
        String endPoint = applicationConfigProperties.getProductManagementUrl();
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
        log.info("DiscoServiceUrlImpl | getProductManagementUrl | Getting product url CPIB from app.yml using uri {}", uriString);
        return uriString;
    }

    @Override
    public String getServiceOrderingUrl() {
        String endPoint = applicationConfigProperties.getServiceOrderingUrl();
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
        log.info("DiscoServiceUrlImpl | getServiceOrderingUrl | Getting service ordering url from app.yml using uri {}", uriString);
        return uriString;
    }

    @Override
    public String getProductManagementByIDUrl(String id) {
        String endPoint = applicationConfigProperties.getProductManagementUrl();
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ID_URI)
                .buildAndExpand(id)
                .toUriString();
        log.info("DiscoServiceUrlImpl | getProductManagementByIDUrl | Getting product url using id {}, full uri {}", id, uriString);
        return uriString;
    }

    @Override
    public String getServiceCatalogManagementUrl() {
        String endPoint = applicationConfigProperties.getServiceCatalogManagementUrl();
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
        log.debug("DiscoServiceUrlImpl | getServiceCatalogManagementUrl | Getting service catalog url from app.yml using full uri {}", uriString);
        return uriString;
    }

    @Override
    public String getProductOrderByIdUrl(String id) {
        String endPoint = applicationConfigProperties.getProductOrderByIdUrl();
        String uriString = UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ID_URI)
                .buildAndExpand(id)
                .toUriString();
        log.info("DiscoServiceUrlImpl | getProductOrderByIdUrl | Getting product order inventory url for id : {} with full uri {}", id, uriString);
        return uriString;
    }

}
