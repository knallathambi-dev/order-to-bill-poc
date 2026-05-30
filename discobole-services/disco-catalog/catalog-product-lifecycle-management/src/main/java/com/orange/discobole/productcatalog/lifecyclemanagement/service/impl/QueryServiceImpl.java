// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.service.impl;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.Resource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.orange.discobole.productcatalog.lifecyclemanagement.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.lifecyclemanagement.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.QueryService;

import java.util.ArrayList;
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
	private static final String ID_PATH="/{id}";

	/**
     * This method is used to get the {@code ProductSpecification} from the read
     * model repository.
     *
     * @param productSpecId unique identifier of {@code ProductSpecification}
     * @param accessToken
     * @return fetched object of {@code ProductSpecification} based on serviceSpecId
     */
	@Override
	@Retry(name = "lifecycleRetry")
	public ProductSpecification fetchProductSpecById(final String productSpecId, String accessToken) {
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		String productSpecQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductSpecQuery())
				.path(ID_PATH).buildAndExpand(productSpecId).toUriString();
		return restTemplate
				.exchange(productSpecQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductSpecification.class)
				.getBody();
	}

	/**
	 * This method is used to get the list of {@code ProductSpecification} from the
	 * read model repository.
	 *
	 * @param productSpecIds unique identifier of {@code ProductSpecification},
	 *                       productSpecIds can be multiple and will be separated by
	 *                       comma.
	 * @return fetched object of {@code ProductSpecification} based on
	 *         productSpecIds
	 */
	@Override
	@Retry(name = "lifecycleRetry")
	public List<ProductSpecification> fetchProductSpecifications(final String productSpecIds, String accessToken) {
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductSpecQuery())
				.queryParam("id", productSpecIds).toUriString();
		return restTemplate.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers),
				new ParameterizedTypeReference<List<ProductSpecification>>() {
				}).getBody();
	}
	
	

	

	

	

	@Override
	@Retry(name = "lifecycleRetry")
	public List<ProductOffering> fetchProductOffering(String accessToken) {
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "lifecycleRetry")
	public ProductOffering fetchProductOfferingById(String productOfferingId, String accessToken) {
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		String productSpecQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.path(ID_PATH).buildAndExpand(productOfferingId).toUriString();
		return restTemplate.exchange(productSpecQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductOffering.class).getBody();
	}

	@Override
	@Retry(name = "lifecycleRetry")
	public List<ProductOffering> fetchProductOfferingsByProductSpecId(String productSpecId, String accessToken) {
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.queryParam("ProductSpecification.id", productSpecId).toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "lifecycleRetry")
	public ProductOfferingPrice getProductOfferingPrice(String productOfferingPriceId, String accessToken) {
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		String productOffPriceQueryUrl = UriComponentsBuilder
				.fromUriString(configurableProperties.getProductOfferingPriceUrl()).path(ID_PATH)
				.buildAndExpand(productOfferingPriceId).toUriString();
		return restTemplate.exchange(productOffPriceQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductOfferingPrice.class)
				.getBody();
	}

	@Override
	public List<ProductOffering> fetchBundleProductOfferingByAtomicProductOfferingId(String entityId, String accessToken) {
		return new ArrayList<>();
	}

	@Override
	@Retry(name = "lifecycleRetry")
	public List<ProductOffering> fetchProductOfferingsByProductOfferingPriceId(String productOfferingPriceId, String accessToken) {
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.queryParam("ProductOfferingPrice.id", productOfferingPriceId).toUriString();
		return  restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();
	}
	 

	@Override
	@Retry(name = "lifecycleRetry")
	public List<ProductOfferingPrice> getProductOfferingPricesByProductOfferingPriceId(String entityId, String accessToken) {
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingPriceUrl())
				.queryParam("productOfferingPriceRelationship._id", entityId).toUriString();
		return  restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOfferingPrice>>() {
				}).getBody();
	
	}

	@Override
	@Retry(name = "lifecycleRetry")
	public List<ProductOffering> fetchBundleAndContractProductOfferingByBundlingProductOfferingId(String entityId, String accessToken) {
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.queryParam("BundledProductOffering.id", entityId).toUriString();
		 return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();
	
	}
 
	
	

	
	

	
	
	
}
