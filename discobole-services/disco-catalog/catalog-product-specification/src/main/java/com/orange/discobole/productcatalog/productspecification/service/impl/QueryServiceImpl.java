// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.*;
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

import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;

import java.util.List;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



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

	// Inside your class
	private static final Logger logger = LoggerFactory.getLogger(QueryServiceImpl.class);


	/**
     * This method is used to get the {@code ServiceSpecification} from the read
     * model repository.
     *
     * @param serviceSpecId unique identifier of {@code ServiceSpecification}
     * @param accessToken
     * @return fetched object of {@code ServiceSpecification} based on serviceSpecId
     */
	@Override
	@Retry(name = "psRetry")
	public ServiceSpecification getServiceSpecById(final String serviceSpecId, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String serviceSpecQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getCfsQuery())
				.path(ID).buildAndExpand(serviceSpecId).toUriString();
		return restTemplate
				.exchange(serviceSpecQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), ServiceSpecification.class)
				.getBody();
	}

	/**
	 * This method is used to get the {@code StockItem} from the read
	 * model repository.
	 *
	 * @param stockItemId unique identifier of {@code StockItem}
	 * @return fetched object of {@code StockItem} based on StockItemId
	 */
	@Override
	@Retry(name = "psRetry")
	public StockItem getStockItemById(final String stockItemId,String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String serviceSpecQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getStockItemUrl())
				.path(ID).buildAndExpand(stockItemId).toUriString();
		return restTemplate
				.exchange(serviceSpecQueryUrl, HttpMethod.GET, new HttpEntity<>(headers), StockItem.class)
				.getBody();
	}

	/**
	 * This method is used to get the {@code StockItemType} from the read
	 * model repository.
	 *
	 * @param stockItemTypeId unique identifier of {@code StockItem}
	 * @param accessToken
	 * @return fetched object of {@code StockItemType} based on StockItemTypeId
	 */
	@Override
	@Retry(name = "psRetry")
	public StockItemType getStockItemTypeById(final String stockItemTypeId, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String stockItemTypeUrl = UriComponentsBuilder.fromUriString(configurableProperties.getStockItemTypeUrl())
				.path(ID).buildAndExpand(stockItemTypeId).toUriString();
		logger.info("Product Spec test: {}", stockItemTypeUrl);
		return restTemplate
				.exchange(stockItemTypeUrl, HttpMethod.GET, new HttpEntity<>(headers), StockItemType.class)
				.getBody();
	}

	@Override
	@Retry(name = "psRetry")
	public List<StockItem> getStockItemByStockItemTypeId(final String stockItemTypeId, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String stockItemUrl = UriComponentsBuilder.fromUriString(configurableProperties.getStockItemUrl())
				.path("/stockItemType").path(ID).buildAndExpand(stockItemTypeId).toUriString();
		return restTemplate
				.exchange(stockItemUrl, HttpMethod.GET, new HttpEntity<>(headers),
						new ParameterizedTypeReference<List<StockItem>>() {
				}).getBody();
	}

	/**
	 * This method is used to get the {@code ProductSpecification} from the read
	 * model repository.
	 *
	 * @param productSpecId unique identifier of {@code ProductSpecification}
	 * @param accessToken
	 * @return fetched object of {@code ProductSpecification} based on serviceSpecId
	 */
	@Override
	@Retry(name = "psRetry")
	public ProductSpecification fetchProductSpecById(final String productSpecId, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String productSpecQueryUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductSpecQuery())
				.path(ID).buildAndExpand(productSpecId).toUriString();
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
	 * @param accessToken
	 * @return fetched object of {@code ProductSpecification} based on
	 * productSpecIds
	 */
	@Override
	@Retry(name = "psRetry")
	public List<ProductSpecification> fetchProductSpecifications(final String productSpecIds, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductSpecQuery())
				.queryParam("id", productSpecIds).toUriString();
		return restTemplate.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers),
				new ParameterizedTypeReference<List<ProductSpecification>>() {
				}).getBody();
	}
	@Override
	@Retry(name = "psRetry")
	public List<ProductSpecification> fetchProductSpecificationsByLifeCycleStatus(final String status,String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductSpecQuery())
				.queryParam("lifecycleStatus", status).toUriString();
		return restTemplate.exchange(channelUrl, HttpMethod.GET,new HttpEntity<>(headers),
				new ParameterizedTypeReference<List<ProductSpecification>>() {
				}).getBody();
	}
	

	/**
	 * This method is used to fetch the category.
	 *
	 * @return categories
	 */

	/**
	 * Fetch market segments.
	 *
	 * @author Diksha Srivastava
	 * @return the market segment ref
	 */
	@Override
	@Retry(name = "psRetry")
	public List<MarketSegmentRef> fetchMarketSegments(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String marketUrl = UriComponentsBuilder.fromUriString(configurableProperties.getMarketUrl()).toUriString();
		return restTemplate
				.exchange(marketUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<MarketSegmentRef>>() {
				}).getBody();
	}

	/**
	 * Fetch channels.
	 *
	 * @return the list
	 */
	@Override
	@Retry(name = "psRetry")
	public List<ChannelRef> fetchChannels(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getChannelUrl()).toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ChannelRef>>() {
				}).getBody();
	}

	@Override
	@Retry(name = "psRetry")
	public List<CFSRelationshipRestriction> fetchCFSRelationship(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String cfsrelationshipUrl = UriComponentsBuilder.fromUriString(configurableProperties.getCfsrelationshipUrl()).toUriString();
		return restTemplate
				.exchange(cfsrelationshipUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<CFSRelationshipRestriction>>() {
				}).getBody();
	}


	@Override
	@Retry(name = "psRetry")
	public List<ProductOffering> fetchProductOfferingsByProductSpecId(String productSpecId, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		if(accessToken == null){
			accessToken = accessTokenInterceptor.getToken();
		}
		headers.add(HttpHeaders.AUTHORIZATION, accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		String channelUrl = UriComponentsBuilder.fromUriString(configurableProperties.getProductOfferingUrl())
				.queryParam("ProductSpecification.id", productSpecId).toUriString();
		return restTemplate
				.exchange(channelUrl, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<ProductOffering>>() {
				}).getBody();
	}
}
