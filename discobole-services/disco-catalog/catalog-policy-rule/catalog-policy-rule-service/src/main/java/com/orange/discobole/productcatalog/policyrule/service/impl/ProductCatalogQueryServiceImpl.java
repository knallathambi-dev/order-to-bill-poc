// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service.impl;

import com.orange.discobole.productcatalog.policyrule.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.policyrule.constants.PolicyRuleConstants;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOffering;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOfferingPrice;
import com.orange.discobole.productcatalog.policyrule.dto.ProductSpecification;
import com.orange.discobole.productcatalog.policyrule.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.policyrule.service.ProductCatalogQueryService;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCatalogQueryServiceImpl implements ProductCatalogQueryService {
    private final RestTemplate restTemplate;
    private final ConfigurableProperties configurableProperties;
    private final AccessTokenInterceptor accessTokenInterceptor;

    @Autowired
    public ProductCatalogQueryServiceImpl(RestTemplate restTemplate, ConfigurableProperties configurableProperties, AccessTokenInterceptor accessTokenInterceptor) {
        this.restTemplate = restTemplate;
        this.configurableProperties = configurableProperties;
        this.accessTokenInterceptor = accessTokenInterceptor;
    }

    @Override
    @Retry(name = "policyRuleRetry")
    public String fetchProductOfferingById(String productOfferingId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        String productOfferingQueryUrl = UriComponentsBuilder
                .fromUriString(configurableProperties.getProductOfferingUrl()).path(PolicyRuleConstants.PATH_VARIABLE_ID)
                .buildAndExpand(productOfferingId).toUriString().trim();
        try {
            ProductOffering productOffering = restTemplate
                    .exchange(productOfferingQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductOffering.class)
                    .getBody();
            if (productOffering != null) {
                return productOffering.getId();
            } else {
                return null;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    @Retry(name = "policyRuleRetry")
    public ProductOffering fetchPOById(String productOfferingId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        String productOfferingQueryUrl = UriComponentsBuilder
                .fromUriString(configurableProperties.getProductOfferingUrl()).path(PolicyRuleConstants.PATH_VARIABLE_ID)
                .buildAndExpand(productOfferingId).toUriString().trim();
        try {
            ProductOffering productOffering = restTemplate
                    .exchange(productOfferingQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductOffering.class)
                    .getBody();
            if (productOffering != null) {
                return productOffering;
            } else {
                return null;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    @Retry(name = "policyRuleRetry")
    public List<ProductOfferingPrice> fetchProductOfferingPriceByIds(List<String> productOfferingPriceIds) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        String productOfferingPriceQueryUrl = UriComponentsBuilder
                .fromUriString(configurableProperties.getProductOfferingPriceUrl()).queryParam("id", productOfferingPriceIds.stream().collect(Collectors.joining(","))).toUriString().trim();
        return restTemplate
                .exchange(productOfferingPriceQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOfferingPrice>>() {})
                .getBody();
    }

    @Override
    public String removePolicyRule(String policyRuleId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        String policyRuleRefURL = UriComponentsBuilder
                .fromUriString(configurableProperties.getPolicyRuleRefUrl()).path(PolicyRuleConstants.POLICY_RULE_ID)
                .buildAndExpand(policyRuleId).toUriString().trim();
        return restTemplate.exchange(policyRuleRefURL, HttpMethod.PATCH, new HttpEntity<>(headers), String.class).getBody();
    }

    @Override
    @Retry(name = "policyRuleRetry")
    public String fetchProductSpecificationById(String productSpecificationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        String productSpecificationQueryUrl = UriComponentsBuilder
                .fromUriString(configurableProperties.getProductSpecificationUrl()).path(PolicyRuleConstants.PATH_VARIABLE_ID)
                .buildAndExpand(productSpecificationId).toUriString().trim();
        try {
            ProductSpecification productSpecification = restTemplate
                    .exchange(productSpecificationQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductSpecification.class)
                    .getBody();
            if (productSpecification != null) {
                return productSpecification.getId();
            }
            return null;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    @Retry(name = "policyRuleRetry")
    public List<ProductOffering> fetchProductOfferingByPolicyRule(String policyRuleRefId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        String productOfferingQueryUrl = UriComponentsBuilder
                .fromUriString(configurableProperties.getProductOfferingUrl()).replaceQuery("policyRuleRef.id=" + policyRuleRefId)
                .toUriString().trim();
        try {
            List<ProductOffering> productOfferings = restTemplate
                    .exchange(productOfferingQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
                    })
                    .getBody();
            return productOfferings;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    @Retry(name = "policyRuleRetry")
    public List<ProductSpecification> fetchProductSpecificationByPolicyRule(String policyRuleRefId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        String productSpecificationQueryUrl = UriComponentsBuilder
                .fromUriString(configurableProperties.getProductSpecificationUrl()).replaceQuery("policyRuleRef.id=" + policyRuleRefId)
                .toUriString().trim();
        try {
            List<ProductSpecification> productSpecifications = restTemplate
                    .exchange(productSpecificationQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductSpecification>>() {
                    })
                    .getBody();
            return productSpecifications;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    @Retry(name = "policyRuleRetry")
    public String fetchProductofferingPriceById(String productOfferingPriceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
        String productOfferingQueryUrl = UriComponentsBuilder
                .fromUriString(configurableProperties.getProductOfferingPriceUrl()).path(PolicyRuleConstants.PATH_VARIABLE_ID)
                .buildAndExpand(productOfferingPriceId).toUriString().trim();
        try {
            ProductOfferingPrice productOfferingPrice = restTemplate
                    .exchange(productOfferingQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductOfferingPrice.class)
                    .getBody();
            if (productOfferingPrice != null) {
                return productOfferingPrice.getId();
            } else {
                return null;
            }
        } catch (Exception exception) {
            return null;
        }
    }
}
