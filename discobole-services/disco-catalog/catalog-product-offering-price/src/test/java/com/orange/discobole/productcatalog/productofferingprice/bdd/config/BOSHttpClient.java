// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.bdd.config;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineRelationship;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;


@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class BOSHttpClient {

	private final String serverUrl = "http://localhost";
	private final String endpointPath = "/processManagement/v1/processFlow";
	private final String catalogEndpoint = "/productCatalogManagement/v1/productOfferingPrice/";

	@LocalServerPort
	private int port;
	private String catalogPort = "8087";

	private final RestTemplate restTemplate = new RestTemplate();

	private String processFlowEndpoint() {
		return serverUrl + ":8089" + endpointPath;
	}

	private String catalogEndpoint() {
		return serverUrl + ":" + catalogPort + catalogEndpoint;
	}


	public ProductOfferingPrice getPOP(String popId) {
		String url = UriComponentsBuilder.fromUriString(catalogEndpoint()).path("/{processFlowId}")
				.buildAndExpand(popId).toUriString();
		return restTemplate.getForEntity(url, ProductOfferingPrice.class).getBody();
	}

	/**
	 * 
	 * This method is used to create process flow
	 * 
	 * @param something
	 * @return
	 * @throws JSONException
	 */
	public ProcessFlow createProductOfferingPriceProcessFlow(final String processName) throws JSONException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject personJsonObject = new JSONObject();
		personJsonObject.put("processFlowSpecification", processName);
		HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
		return restTemplate.postForEntity(processFlowEndpoint(), request, ProcessFlow.class).getBody();

	}

	public TaskFlow selectPOPTypeRequest(String processFlowId, String id, String popType) throws JSONException {
		// ✅ Define request configuration (Fix for setReadTimeout issue)
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofSeconds(10))// Equivalent to setConnectTimeout()
				.setResponseTimeout(Timeout.ofSeconds(10)) // Replacement for setReadTimeout()
				.build();

		// ✅ Create HttpClient with request config
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("pOPType", popType);
		characterstic.put("name", "selectPOPType");
		characterstic.put("valueType", "Object");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public TaskFlow selectPOPRequest(String processFlowId, String id, String popId) throws JSONException {
		// ✅ Define request configuration (Fix for setReadTimeout issue)
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofSeconds(10))// Equivalent to setConnectTimeout()
				.setResponseTimeout(Timeout.ofSeconds(10)) // Replacement for setReadTimeout()
				.build();

		// ✅ Create HttpClient with request config
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.build();



		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("productOfferingPrice.id", popId);
		characterstic.put("name", "selectPOP");
		characterstic.put("valueType", "Object");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public TaskFlow selectPOPVersionRequest(String processFlowId, String id, String versionType) throws JSONException {
		// ✅ Define request configuration (Fix for setReadTimeout issue)
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofSeconds(10))// Equivalent to setConnectTimeout()
				.setResponseTimeout(Timeout.ofSeconds(10)) // Replacement for setReadTimeout()
				.build();

		// ✅ Create HttpClient with request config
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.build();




		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("versionType", versionType);
		characterstic.put("name", "SelectProductOfferingPriceVersion");
		characterstic.put("valueType", "Object");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public TaskFlow selectPOPCRequest(String processFlowId, String id,
			DefineProductOfferingPriceChargeIdentityData popc) throws JSONException {

		// ✅ Define request configuration (Fix for setReadTimeout issue)
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofSeconds(10))// Equivalent to setConnectTimeout()
				.setResponseTimeout(Timeout.ofSeconds(10)) // Replacement for setReadTimeout()
				.build();

		// ✅ Create HttpClient with request config
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.build();


		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("name", popc.getName());
		value.put("description", popc.getDescription());
		JSONObject price = new JSONObject();
		price.put("unit", popc.getPrice().getUnit());
		price.put("value", popc.getPrice().getValue());
		value.put("price", price);

		value.put("priceType", popc.getPriceType());
		characterstic.put("name", "DefineProductOfferingPriceChargeIdentityData");
		characterstic.put("valueType", "Object");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public TaskFlow selectPOPRelationshipRequest(String processFlowId, String id, List<DefineRelationship> relationshipList)
			throws JSONException {
		// ✅ Define request configuration (Fix for setReadTimeout issue)
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofSeconds(10))// Equivalent to setConnectTimeout()
				.setResponseTimeout(Timeout.ofSeconds(10)) // Replacement for setReadTimeout()
				.build();

		// ✅ Create HttpClient with request config
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
	
		JSONArray list = new JSONArray();
		for(DefineRelationship relationship:relationshipList){
			JSONObject value = new JSONObject();
			value.put("id", relationship.getId());
			value.put("relationshipType", relationship.getRelationshipType());
			value.put("validFor", relationship.getValidFor());
			list.put(value);
		}

		characterstic.put("name", "DefineRelationship");
		characterstic.put("valueType", "Object");
		characterstic.put("value", list);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public TaskFlow selectPOPValidityRequest(String processFlowId, String id, String lifecycleStatus
			) throws JSONException {
		// ✅ Define request configuration (Fix for setReadTimeout issue)
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofSeconds(10))// Equivalent to setConnectTimeout()
				.setResponseTimeout(Timeout.ofSeconds(10)) // Replacement for setReadTimeout()
				.build();

		// ✅ Create HttpClient with request config
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.build();


		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		JSONObject validForJson = new JSONObject();
		validForJson.put("startDateTime", "2023-01-12T23:20:50.520Z");
		validForJson.put("endDateTime", "2023-02-12T23:20:50.520Z");
		value.put("lifecycleStatus", lifecycleStatus);
		value.put("validFor", validForJson);
		characterstic.put("name", "DefinePOPStatusValidityPeriod");
		characterstic.put("valueType", "Object");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public TaskFlow selectPOPValidateRequest(String processFlowId, String id, boolean isValidated)
			throws JSONException {
		// ✅ Define request configuration (Fix for setReadTimeout issue)
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofSeconds(10))// Equivalent to setConnectTimeout()
				.setResponseTimeout(Timeout.ofSeconds(10)) // Replacement for setReadTimeout()
				.build();

		// ✅ Create HttpClient with request config
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("isValidated", isValidated);
		characterstic.put("name", "ValidateEntityOperation");
		characterstic.put("valueType", "Object");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public void clean() {
		restTemplate.delete(processFlowEndpoint());
	}



}