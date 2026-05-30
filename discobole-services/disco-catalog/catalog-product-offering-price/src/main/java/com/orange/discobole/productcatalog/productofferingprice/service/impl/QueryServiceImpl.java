// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Currency;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Frequency;
import java.util.Collections;

import io.github.resilience4j.retry.annotation.Retry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.orange.discobole.productcatalog.productofferingprice.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingType;
import com.orange.discobole.productcatalog.productofferingprice.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productofferingprice.service.QueryService;

import jakarta.annotation.Resource;

/**
 * This class corresponds to define the operations related to
 * {@code ServiceSpecification}.
 *
 * @author Piyush Goel
 * @since 1.0
 */
@Service
public class QueryServiceImpl implements QueryService {

	private static final Logger LOGGER = LogManager.getLogger(QueryServiceImpl.class);
	
	@Resource
	private RestTemplate restTemplate;

	@Resource
	private ConfigurableProperties configurableProperties;
	
	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;

	/**
	 * This method get the Product Offering Price By Id.
	 * 
	 * @param productOfferingPriceId
	 * @return ProductOfferingPrice
	 */
	@Override
	@Retry(name = "popRetry")
	public ProductOfferingPrice getProductOfferingPrice(String productOfferingPriceId) {
		LOGGER.info("Get Product Offering Price By Id from Catalog: {}",productOfferingPriceId);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String productOffPriceQueryUrl = UriComponentsBuilder
				.fromUriString(configurableProperties.getProductOfferingPriceUrl()).path("/{id}")
				.buildAndExpand(productOfferingPriceId).toUriString();
		return restTemplate.exchange(productOffPriceQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductOfferingPrice.class)
				.getBody();
	}      
	
	@Override
	@Retry(name = "popRetry")
	public List<ProductOffering> fetchProductOfferingsByProductOfferingPriceId(String productOfferingPriceId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.queryParam("@type", ProductOfferingType.CONTRACT.toString()).toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "popRetry")
	public List<Frequency> fetchFrequency() {

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		String frequencyUrl = UriComponentsBuilder.fromUriString(configurableProperties.getFrequencyUrl()).toUriString();
		return restTemplate
				.exchange(frequencyUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Frequency>>() {
				}).getBody();

	}

	@Override
	@Retry(name = "popRetry")
	public List<Currency> fetchCurrency() {

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		String currencyUrl = UriComponentsBuilder.fromUriString(configurableProperties.getCurrencyUrl()).toUriString();
		return restTemplate
				.exchange(currencyUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Currency>>() {
				}).getBody();


	}


}
  