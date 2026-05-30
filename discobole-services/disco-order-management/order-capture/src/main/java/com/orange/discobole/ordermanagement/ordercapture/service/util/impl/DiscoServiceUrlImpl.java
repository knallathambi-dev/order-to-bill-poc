// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.util.impl;

import com.orange.discobole.ordermanagement.ordercapture.config.ApplicationConfigProperties;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
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
    public String getProductOfferingByIdUrl(String id) {
        log.debug("Getting product offering url by id : {} ", id);
        String endPoint = applicationConfigProperties.getProductOfferingUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }

    @Override
    public String getProductOfferingQualificationUrl() {
        log.debug("Getting product offering qualification url");
        String endPoint = applicationConfigProperties.getProductOfferingQualificationUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getProductConfigurationItemsByIdUrl(String id) {
        log.debug("Getting product configuration by for id : {} ", id);
        String endPoint = applicationConfigProperties.getProductConfigurationUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }


    @Override
    public String getProductSpecificationByIdUrl(String id) {
        log.debug("Getting product specification url by id : {} ", id);
        String endPoint = applicationConfigProperties.getProductSpecificationUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }

    @Override
    public String getProductSpecificationUrl() {
        log.debug("Getting product specification url");
        String endPoint = applicationConfigProperties.getProductSpecificationUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getProductOfferingPriceUrl() {
        log.debug("Getting product offering price url");
        String endPoint = applicationConfigProperties.getProductOfferingPriceUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getAvailableResourceByIdsUrl(String ids) {
        log.debug("Getting available resource url by id : {} ", ids);
        String endPoint = applicationConfigProperties.getInventoryManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.AVAILABLE_RESOURCE_FILTER_URI)
                .buildAndExpand(ids)
                .toUriString();
    }
    @Override
    public String getProductInventoryUrl() {
        log.debug("Getting product management url");
        String endPoint = applicationConfigProperties.getProductInventoryUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getProductManagementByIdUrl(String id) {
        log.debug("Getting product management url by id : {} ", id);
        String endPoint = applicationConfigProperties.getProductInventoryUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }

    @Override
    public String getOrderInventoryUrl() {
        log.debug("Getting order inventory url");
        String endPoint = applicationConfigProperties.getOrderInventoryUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getProductOrderInventoryByIdUrl(String id) {
        log.debug("Getting product order inventory url for id : {} ", id);
        String endPoint = applicationConfigProperties.getOrderInventoryUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }

    @Override
    public String getPartyRoleManagementUrl() {
        log.debug("Getting party role management url");
        String endPoint = applicationConfigProperties.getPartyRoleManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getProductStockManagementUrl() {
        log.debug("Getting product stock management url");
        String endPoint = applicationConfigProperties.getProductStockManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }


    @Override
    public String getProductStockManagementByIdUrl(String id) {
        log.debug("Getting product stock management for id : {} ", id);
        String endPoint = applicationConfigProperties.getProductStockManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }

    @Override
    public String getPaymentManagementByIdUrl(String id) {
        log.debug("Getting payment management url for id {}", id);
        String endPoint = applicationConfigProperties.getPaymentManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }

    @Override
    public String getAccountManagementByIdUrl(String id) {
        log.debug("Getting account management url for id {}", id);
        String endPoint = applicationConfigProperties.getAccountManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }

    @Override
    public String getAccountManagement() {
        log.debug("Getting account management url");
        String endPoint = applicationConfigProperties.getAccountManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getProductConfigurationItemsByIdMockedUrl(String id) {
        log.debug("Getting mocked product configuration for id : {}", id);
        String endPoint = applicationConfigProperties.getProductConfigurationMockedUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }

    @Override
    public String getServiceQualificationManagementUrl() {
        log.debug("Getting service qualification management url");
        String endPoint = applicationConfigProperties.getServiceQualificationManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getAppointmentManagementByIdUrl(String id) {
        log.debug("Getting appointment management url for id : {}", id);
        String endPoint = applicationConfigProperties.getAppointmentManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(id)
                .toUriString();
    }

    @Override
    public String getReserveResourcesUrl() {
        log.debug("Getting reserve resources batch url");
        String endPoint = applicationConfigProperties.getInventoryManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .toUriString();
    }

    @Override
    public String getPartyManagementByIdUrl(String partyId) {
        log.debug("Getting party management url");
        String endPoint = applicationConfigProperties.getPartyManagementUrl();
        return UriComponentsBuilder
                .fromUriString(endPoint)
                .path(ServiceConstants.ID_URI)
                .buildAndExpand(partyId)
                .toUriString();
    }
}