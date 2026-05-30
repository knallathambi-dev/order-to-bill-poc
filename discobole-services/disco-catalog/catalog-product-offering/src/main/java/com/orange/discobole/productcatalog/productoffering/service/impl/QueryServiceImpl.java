// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productcatalogadministration.FrequencyTypes;
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

import com.orange.discobole.productcatalog.productoffering.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
import com.orange.discobole.productcatalog.productoffering.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;

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
	
	private static final String ID_PATH_VARIABLE="/{id}";

	/**
	 * This method is used to get the {@code ProductSpecification} from the read
	 * model repository.
	 *
	 * @param productSpecId unique identifier of {@code ProductSpecification}
	 * @return fetched object of {@code ProductSpecification} based on serviceSpecId
	 */
	@Override
	@Retry(name = "poRetry")
	public ProductSpecification fetchProductSpecById(final String productSpecId, String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String productSpecQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductSpecQuery())
				.path(ID_PATH_VARIABLE).buildAndExpand(productSpecId).toUriString();
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
	@Retry(name = "poRetry")
	public List<ProductSpecification> fetchProductSpecifications(final String productSpecIds, String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductSpecQuery())
				.queryParam("id", productSpecIds).toUriString();
		return restTemplate.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers),
				new ParameterizedTypeReference<List<ProductSpecification>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public List<ProductSpecification> fetchProductSpecificationsByLifeCycleStatus(final String status, String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductSpecQuery())
				.queryParam("lifecycleStatus", status).toUriString();
		return restTemplate.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers),
				new ParameterizedTypeReference<List<ProductSpecification>>() {
				}).getBody();
	}

	/**
	 * This method is used to fetch the category.
	 *
	 * @return categories
	 */
	@Override
	@Retry(name = "poRetry")
	public List<Category> fetchCategory(String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String categoryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getCategoryUrl()).toUriString();
		return restTemplate
				.exchange(categoryUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Category>>() {
				}).getBody();
	}

	/**
	 * Fetch market segments.
	 *
	 * @author Diksha Srivastava
	 * @return the market segment ref
	 */
	@Override
	@Retry(name = "poRetry")
	public List<MarketSegmentAdmin> fetchMarketSegments(String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String marketUrl = UriComponentsBuilder.fromUriString(configurableProperties.getMarketUrl()).toUriString();
		return restTemplate
				.exchange(marketUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<MarketSegmentAdmin>>() {
				}).getBody();
	}

	/**
	 * Fetch channels.
	 *
	 * @return the list
	 */
	@Override
	@Retry(name = "poRetry")
	public List<ChannelAdmin> fetchChannels(String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getChannelUrl()).toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ChannelAdmin>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public List<ProductOffering> fetchProductOffering(String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public ProductOffering fetchProductOfferingById(String productOfferingId, String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String productSpecQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.path(ID_PATH_VARIABLE).buildAndExpand(productOfferingId).toUriString();
		return restTemplate.exchange(productSpecQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductOffering.class).getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public List<ProductOffering> fetchProductOfferingsByProductSpecId(String productSpecId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.queryParam("ProductSpecification.id", productSpecId).toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();
	}

	@Override
	public List<ProductOffering> fetchBundleProductOfferingByAtomicProductOfferingId(String entityId) {
		return new ArrayList<>();
	}

	@Override
	@Retry(name = "poRetry")
	public List<ProductOffering> fetchProductOfferingsByProductOfferingPriceId(String productOfferingPriceId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.queryParam("ProductOfferingPrice.id", productOfferingPriceId).toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public List<ProductOffering> fetchProductOfferingsByLifeCycleStatus(String status) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.queryParam("lifecycleStatus", status).toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();

	}

	@Override
	@Retry(name = "poRetry")
	public Category fetchCategoryById(String categoryId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String categoryQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getCategoryUrl())
				.path(ID_PATH_VARIABLE).buildAndExpand(categoryId).toUriString();
		return restTemplate.exchange(categoryQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), Category.class).getBody();
	}

	/**
	 * Fetch categories by list of categoryIds.
	 *
	 * @return the list
	 */
	@Override
	@Retry(name = "poRetry")
	public List<Category> fetchCategoryByIds(List<String> categoryIds, String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String categoryQueryUrl = UriComponentsBuilder
				.fromUriString(configurableProperties.getCategoryUrl()).queryParam("id", categoryIds.stream().collect(Collectors.joining(","))).toUriString().trim();
		return restTemplate
				.exchange(categoryQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Category>>() {})
				.getBody();
	}


	@Override
	@Retry(name = "poRetry")
	public CategoryEntityRelationship fetchCategoryEntityById(String categoryEntityId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String categoryQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getCategoryEntityUrl())
				.path(ID_PATH_VARIABLE).buildAndExpand(categoryEntityId).toUriString();
		return restTemplate.exchange(categoryQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), CategoryEntityRelationship.class)
				.getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public List<Category> fetchCategoryEntityBySubCategoryId(String categoryEntityId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String categoryQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getCategoryUrl())
				.queryParam("subCategory.id", categoryEntityId).toUriString();
		return restTemplate
				.exchange(categoryQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Category>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public ProductOfferingPrice getProductOfferingPrice(String productOfferingPriceId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String productOffPriceQueryUrl = UriComponentsBuilder
				.fromUriString(configurableProperties.getProductOfferingPriceUrl()).path(ID_PATH_VARIABLE)
				.buildAndExpand(productOfferingPriceId).toUriString();
		return restTemplate.exchange(productOffPriceQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ProductOfferingPrice.class)
				.getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public List<ProductOffering> fetchBundleAndContractProductOfferingByBundlingProductOfferingId(String entityId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.queryParam("BundledProductOffering.id", entityId).toUriString();
		return restTemplate.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers),
				new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();

	}

	@Override
	@Retry(name = "poRetry")
	public List<ProductOffering> fetchProductOfferingsByIds(List<String> productOfferingIds, String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String productOfferingQueryUrl = UriComponentsBuilder
				.fromUriString(configurableProperties.getProductOfferingUrl()).queryParam("id", productOfferingIds.stream().collect(Collectors.joining(","))).toUriString().trim();
		return restTemplate
				.exchange(productOfferingQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				})
				.getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public List<ProductOfferingPrice> fetchProductOfferingPriceByIds(List<String> productOfferingPriceIds, String token) {
		HttpHeaders headers = new HttpHeaders();
		if(token == null){
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String productOfferingPriceQueryUrl = UriComponentsBuilder
				.fromUriString(configurableProperties.getProductOfferingPriceUrl()).queryParam("id", productOfferingPriceIds.stream().collect(Collectors.joining(","))).toUriString().trim();
		return restTemplate
				.exchange(productOfferingPriceQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOfferingPrice>>() {})
				.getBody();
	}


	@Override
	@Retry(name = "poRetry")
	public List<FrequencyTypes> fetchFrequency(String token) {
		HttpHeaders headers = new HttpHeaders();
		if (token == null) {
			token = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, token);
		String marketUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductCatalogAdministrationUrl()).path("/productCatalogAdministration/v5/frequencyTypes").toUriString();
		return restTemplate
				.exchange(marketUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<FrequencyTypes>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "poRetry")
	public List<ProductOffering> fetchProductOfferingsByPolicyRule(String policyRuleId) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, accessTokenInterceptor.getToken());
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			String offeringUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
					.queryParam("policyRuleRef.id", policyRuleId).toUriString();
			return restTemplate
					.exchange(offeringUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
					}).getBody();
	}
}
