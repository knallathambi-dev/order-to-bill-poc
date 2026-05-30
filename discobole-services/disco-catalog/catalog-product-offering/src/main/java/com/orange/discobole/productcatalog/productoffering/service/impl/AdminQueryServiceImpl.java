// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.CpibConfiguration;
import com.orange.discobole.productcatalog.productoffering.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productoffering.service.AdminQueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AdminQueryServiceImpl implements AdminQueryService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private AccessTokenInterceptor accessTokenInterceptor;
    @Value("${config.adminCPIBConfigUrl}")
    private String adminConfigUrl;

    @Override
    public boolean fetchCPIBConfiguration() {
        UriComponentsBuilder cpibComponentsBuilder =
                UriComponentsBuilder.fromUriString(adminConfigUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        try {
            ResponseEntity<List<CpibConfiguration>> response = restTemplate.exchange
                    (cpibComponentsBuilder.toUriString(), HttpMethod.POST,new HttpEntity<>(headers), new
                            ParameterizedTypeReference<>() { });
            List<CpibConfiguration> cpibConfigurations = new ArrayList<>(Objects.requireNonNull(response.getBody()));
            if (!cpibConfigurations.isEmpty())
            {
                return cpibConfigurations.get(0).getCpibCheck();
            }
        }catch (Exception exception){
            return false;
        }
        return false;
    }
}
