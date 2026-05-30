// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.bdd.config;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingPrice;

@Component

@Scope(SCOPE_CUCUMBER_GLUE)
public class BOSHttpClient {

	private final String SERVER_URL = "http://localhost";
	private final String ENDPOINT = "/processManagement/v4/processFlow";
	private final String CATALOGENDPOINT = "/productCatalogManagement/v1/productOfferingPrice/";

	@LocalServerPort
	private int port;
	private String catalogPort = "8087";

	private final RestTemplate restTemplate = new RestTemplate();

	private String processFlowEndpoint1() {
		return SERVER_URL + ":8888" + ENDPOINT;
	}

	private String catalogEndpoint() {
		return SERVER_URL + ":" + catalogPort + CATALOGENDPOINT;
	}

	/**
	 * This method is used to create process flow
	 * 
	 * @param something
	 * @return
	 * @throws JSONException
	 */

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
	public ResponseEntity<ProcessFlow> createProductOfferingProcessFlow(final String processName) throws JSONException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject personJsonObject = new JSONObject();
		personJsonObject.put("processFlowSpecification", processName);
		HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
		return restTemplate.postForEntity(processFlowEndpoint1(), request, ProcessFlow.class);
	}

	public TaskFlow selectPOTypeRequest(String processFlowId, String id, String poType) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(10000);
		requestFactory.setReadTimeout(10000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("productOfferingType", poType);
		characterstic.put("name", "SelectProductOfferingType");
		characterstic.put("valueType", "Object");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public TaskFlow selectPOPRequest(String processFlowId, String id, String popId) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
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
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public TaskFlow selectPOPVersionRequest(String processFlowId, String id, String versionType) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
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
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}

	public TaskFlow defineProductOfferingCategoryRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONArray value = new JSONArray();
		characterstic.put("name", "DefineProductOfferingCategory");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	
	public TaskFlow defineProductOfferingIdentityDataORequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("name","poName");
		value.put("description","poDescription");
		value.put("brand","poBrand");
		value.put("isInstallable",true);
		characterstic.put("name", "DefineProductOfferingIdentityData");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	public TaskFlow defineProductOfferingSaleChannelRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONArray value = new JSONArray();
		JSONObject channelInfo = new JSONObject();
		channelInfo.put("channelId","web");
		value.put(channelInfo);
		characterstic.put("name", "DefineProductOfferingSaleChannel");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	public TaskFlow defineProductOfferingMarketSegmentRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONArray value = new JSONArray();
		JSONObject channelInfo = new JSONObject();
		channelInfo.put("segmentId","B2C");
		value.put(channelInfo);
		characterstic.put("name", "DefineProductOfferingMarketSegment");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	public TaskFlow defineDefineBundledOperationSpecificationRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONArray value = new JSONArray();
		JSONObject SpecInfo = new JSONObject();
		JSONObject validFor = new JSONObject();
		SpecInfo.put("productOffering.commercialOperationSpecification.id","1");
		SpecInfo.put("name","Add");
		SpecInfo.put("description","Provision a new mobile access b01 on Orange Network");
		validFor.put("endDateTime",null);
		validFor.put("startDateTime","2021-06-14T06:52:08.619Z");
		
		SpecInfo.put("validFor",validFor);
		value.put(SpecInfo);
		characterstic.put("name", "DefineBundledOperationSpecification");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	public TaskFlow associatePOPtoOperationSpecificationRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONArray value = new JSONArray();
		characterstic.put("name", "AssociatePOPtoOperationSpecification");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	public TaskFlow manageProductOfferingTermRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONArray value = new JSONArray();
		JSONObject poTerm= new JSONObject();
		JSONObject duration = new JSONObject();
		JSONObject validFor = new JSONObject();
		validFor.put("endDateTime","1985-04-15T23:20:50.52Z");
		validFor.put("startDateTime","1985-04-15T23:20:50.52Z");
		duration.put("amount",12);
		duration.put("units","Day");
		poTerm.put("duration",duration);
		poTerm.put("name","cs");
		poTerm.put("description","cs");
		poTerm.put("validFor",validFor);
		value.put(poTerm);
		characterstic.put("name", "ManageProductOfferingTerm");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	public TaskFlow manageProductOfferingBundlingRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("globalMinCardinality",0);
		value.put("globalMaxCardinality",0);
		JSONArray pos=new JSONArray();
		value.put("bundledProductOffering",pos);
		characterstic.put("name", "ManageProductOfferingBundling");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	public TaskFlow defineRelationshipRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONArray value = new JSONArray();
		characterstic.put("name", "DefineRelationship");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	public TaskFlow defineRelatedPartyRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONArray value = new JSONArray();
		JSONObject relatedParty=new JSONObject();
		relatedParty.put("role","abc");
		relatedParty.put("@referredType","individual");
		value.put(relatedParty);
		characterstic.put("name", "relatedParty");
		characterstic.put("valueType", "List");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	
	public TaskFlow defineEntityValidityPeriodRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		JSONObject validFor=new JSONObject();
		validFor.put("endDateTime", null);
		validFor.put("startDateTime","2021-05-18T22:00:00.000Z");
		characterstic.put("name", "DefineEntityValidityPeriod");
		characterstic.put("valueType", "Object");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	public TaskFlow defineValidateEntityOperationRequest(String processFlowId, String id) throws JSONException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000000);
		requestFactory.setReadTimeout(1000000);
		restTemplate.setRequestFactory(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		JSONArray charactersticList = new JSONArray();
		JSONObject characterstic = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("isValidated",true);
		characterstic.put("name", "ValidateEntityOperation");
		characterstic.put("valueType", "Object");
		characterstic.put("value", value);
		characterstic.put("@baseType", null);
		characterstic.put("@schemaLocation", null);
		characterstic.put("@type", "ObjectCharacteristic");
		charactersticList.put(0, characterstic);
		json.put("characteristic", charactersticList);
		HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
		String url = UriComponentsBuilder.fromUriString(processFlowEndpoint1()).path("/{processFlowId}/taskFlow/{id}")
				.buildAndExpand(processFlowId, id).toUriString();
		return restTemplate.patchForObject(url, request, TaskFlow.class);
	}
	

	public void clean() {
		restTemplate.delete(processFlowEndpoint1());
	}
}