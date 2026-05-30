// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service.impl;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.Resource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.category.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.category.service.QueryService;

import java.util.Collections;
import java.util.List;

/**
 * This class corresponds to define the operations related to
 * {@code ServiceSpecification}.
 *
 * @author Piyush Goel
 * @since 1.0
 */
@Service
public class QueryServiceImpl implements QueryService {

	@Resource
	private RestTemplate restTemplate;

	@Resource
	private ConfigurableProperties configurableProperties;

	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;
	
	private static final String ID = "/{id}";

	@Override
	@Retry(name = "categoryRetry")
	public Category fetchCategoryById(String categoryId, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		String categoryQueryUrl = UriComponentsBuilder
				.fromUriString(configurableProperties.getCategoryUrl()).path(ID)
				.buildAndExpand(categoryId).toUriString();
		return restTemplate.exchange(categoryQueryUrl, HttpMethod.GET,  new HttpEntity<>(headers), Category.class)
				.getBody();
	}

	@Override
	@Retry(name = "categoryRetry")
	public CategoryEntityRelationship fetchCategoryEntityById(String categoryEntityId, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		String categoryQueryUrl = UriComponentsBuilder
				.fromUriString(configurableProperties.getCategoryEntityUrl()).path(ID)
				.buildAndExpand(categoryEntityId).toUriString();
		return restTemplate.exchange(categoryQueryUrl, HttpMethod.GET,  new HttpEntity<>(headers), CategoryEntityRelationship.class)
				.getBody();
	}
	
	@Override
	@Retry(name = "categoryRetry")
	public List<Category> fetchCategoryEntityBySubCategoryId(String categoryEntityId, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		String categoryQueryUrl = UriComponentsBuilder
				.fromUriString(configurableProperties.getCategoryUrl()).queryParam("subCategory.id", categoryEntityId)
				.toUriString();
		return restTemplate.exchange(categoryQueryUrl, HttpMethod.GET,  new HttpEntity<>(headers),new ParameterizedTypeReference<List<Category>>(){
		}).getBody();
	}
	@Override
	@Retry(name = "categoryRetry")
	public ProductOffering fetchProductOfferingById(String productOfferingId, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		String productOfferingQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.path(ID).buildAndExpand(productOfferingId).toUriString();
		return restTemplate.exchange(productOfferingQueryUrl, HttpMethod.GET,  new HttpEntity<>(headers), ProductOffering.class).getBody();
	}
}
