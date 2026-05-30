// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

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

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.Product;
import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productspecification.service.CommercialProductInstalledBaseQueryService;

import java.util.Objects;

@Service
public class CommercialProductInstalledBaseQueryServiceImpl implements CommercialProductInstalledBaseQueryService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private AccessTokenInterceptor accessTokenInterceptor;
    @Value("${CPIB.offset}")
    private int offset;
    @Value("${CPIB.limit}")
    private int limit;
    @Value("${config.cpibProductUrl}")
    private String cpibProductUrl;

    @Override
    public boolean fetchProductByProductSpecId(String productSpecId, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        if(accessToken == null){
            accessToken = accessTokenInterceptor.getToken();
        }
        headers.add(HttpHeaders.AUTHORIZATION, accessToken);
        String url=UriComponentsBuilder.fromUriString(cpibProductUrl
        ).queryParam("productSpecification.id",productSpecId).toUriString();
        try{
            ResponseEntity<String> response = restTemplate.exchange
                    (url, HttpMethod.GET,new HttpEntity<>(headers), String.class);
            Integer totalCount = Integer.parseInt(response.getHeaders().getFirst("x-total-count"));
            if(totalCount == 0){
                return false;
            }
            return true;
        }catch (Exception exception){
            return false;
        }
    }

    @Override
    public boolean fetchProductByProductOfferingId(String productOfferingId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        String url=UriComponentsBuilder.fromUriString(cpibProductUrl
        ).queryParam("productOffering.id",productOfferingId).toUriString();
        try{
            ResponseEntity<Product> response = restTemplate.exchange
                    (url, HttpMethod.GET,null, new
                            ParameterizedTypeReference<>() { });
            Objects.requireNonNull(response.getBody());
           return true;
        }catch (Exception exception){
            return false;
        }
    }
}
