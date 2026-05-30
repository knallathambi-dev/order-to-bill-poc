// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.service.impl;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.orange.discobole.productcatalog.lifecyclemanagement.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.Product;
import com.orange.discobole.productcatalog.lifecyclemanagement.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.CommercialProductInstalledBaseQueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CommercialProductInstalledBaseQueryServiceImpl implements CommercialProductInstalledBaseQueryService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ConfigurableProperties configurableProperties;
    @Resource
    private AccessTokenInterceptor accessTokenInterceptor;
    @Value("${CPIB.offset}")
    private int offset;
    @Value("${CPIB.limit}")
    private int limit;

    @Override
    public boolean fetchProductByProductSpecId(String productSpecId, String accessToken) {
        if(accessToken == null){
            accessToken = accessTokenInterceptor.getToken();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessToken);
        UriComponentsBuilder cpibComponentsBuilder =
                    UriComponentsBuilder.fromUriString(configurableProperties.getCpibProductUrl()
                    ) .queryParam("productSpecification.id", productSpecId).queryParam("limit", limit);

            cpibComponentsBuilder.queryParam("offset",offset);
            ResponseEntity<List<Product>> response = restTemplate.exchange
                    (cpibComponentsBuilder.toUriString(), HttpMethod.GET,null, new
                            ParameterizedTypeReference<>() { });
        try {
            List<Product> allProducts = new ArrayList<>(Objects.requireNonNull(response.getBody()));
            if (!allProducts.isEmpty())
            {
               return true;
            }
        }catch (Exception exception){
            return false;
        }
        return false;
    }

    @Override
    public boolean fetchProductByProductOfferingId(String productOfferingId, String accessToken) {
        if(accessToken == null){
            accessToken = accessTokenInterceptor.getToken();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessToken);
        String url=UriComponentsBuilder.fromUriString(configurableProperties.getCpibProductUrl()
        ).queryParam("productOffering.id",productOfferingId).toUriString();
        ResponseEntity<Product> response = restTemplate.exchange
                (url, HttpMethod.GET,null, new
                        ParameterizedTypeReference<>() { });
        try{
            Objects.requireNonNull(response.getBody());
           return true;
        }catch (Exception exception){
            return false;
        }
    }
}
