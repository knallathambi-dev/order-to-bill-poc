// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions.prodspec.modify;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.lifecycle.ProductSpecNextSates;
import com.orange.discobole.productcatalog.productspecification.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectRelatedParty;
import com.orange.discobole.productcatalog.productspecification.pojo.SelectRelatedResource;
import com.orange.discobole.productcatalog.productspecification.pojo.modify.ModifyIdentityData;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;
import com.orange.discobole.productcatalog.productspecification.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productspecification.util.FileUtil;
import com.orange.discobole.productcatalog.productspecification.util.ValidationUtil;

import jakarta.annotation.Resource;

@Component("ProductSpecModification.modifyDefineIdentityData")
public class ModifyDefineIdentityDataAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(ModifyDefineIdentityDataAction.class);

	private static final String DISCO_PS_PROVIDE_IDORNAME = "DISCO_PS_PROVIDE_IDORNAME";

	private static final String DISCO_PS_VALIDITY_MUST_NOTNULL = "DISCO_PS_VALIDITY_MUST_NOTNULL";

	private static final String DISCO_PS_INVALID_PS_STATE = "DISCO_PS_INVALID_PS_STATE";

	private static final String DISCO_PS_IDENTITYDATA_MUST_NOTNULL = "DISCO_PS_IDENTITYDATA_MUST_NOTNULL";

	@Resource
	private ProductSpecService productSpecService;

	@Resource
	private ObjectMapper objectMapper;
	
	@Resource
	private QueryService  queryService;

	private List<CharacteristicSpecification> characteristicList;
	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate)
	        throws ParameterException {
	    ModifyIdentityData identityData = new ModifyIdentityData();
	    final Map<String, Object> variables = new HashMap<>();
	    String productSpecId = getProductSpecId(stateMachineTransition);

	    // Get characteristic list if not already loaded
	    loadCharacteristicsIfNeeded(productSpecId, stateMachineTransition);

	    // Validate and get identity data
	    identityData = getIdentityData(taskFlowUpdate);
	    
	    // Validate identity data fields
	    validateIdentityData(identityData);

	    // Process related resources and related parties
	    List<RelatedResource> relResource = processRelatedResources(identityData);
	    List<RelatedParty> relParty = processRelatedParties(identityData);

	    // Validate validity period
	    validateValidityPeriod(identityData);

	    // Check lifecycle status
	    validateLifecycleStatus(identityData, productSpecId);

	    // Modify product specification
	    modifyProductSpec(productSpecId, identityData, relParty, relResource);

	    return variables;
	}

	private String getProductSpecId(StateMachineTransition stateMachineTransition) {
	    return (String) stateMachineTransition.getVariablesFromUserActions().get(ProductSpecConstants.PRODUCT_SPEC_ID);
	}

	private void loadCharacteristicsIfNeeded(String productSpecId, StateMachineTransition stateMachineTransition) {
	    if (characteristicList == null) {
	        characteristicList = getTaskCharacteristics(productSpecId, stateMachineTransition.getTaskDefinitionId());
	    }
	}

	private ModifyIdentityData getIdentityData(TaskFlowUpdate taskFlowUpdate) throws ParameterException {
	    Characteristic characteristic = CharacteristicUtil.getCharacteristic(
	            taskFlowUpdate.getCharacteristic(), ProductSpecConstants.IDENTITY_DATA, characteristicList);
	    if (characteristic != null) {
	        return (ModifyIdentityData) ValidationUtil.validatePojo(
	                characteristic.getValue(), "productSpecification", "modifyDefineIdentityData");
	    }
	    return new ModifyIdentityData();
	}

	private void validateIdentityData(ModifyIdentityData identityData) throws DiscoManagedClientException {
	    if (identityData.getIdentityData() == null) {
	        throw new DiscoManagedClientException(DISCO_PS_IDENTITYDATA_MUST_NOTNULL);
	    }
	}

	private List<RelatedResource> processRelatedResources(ModifyIdentityData identityData) throws DiscoManagedClientException {
	    List<RelatedResource> relResource = new ArrayList<>();
	    if (identityData.getRelatedResource() != null && !identityData.getRelatedResource().isEmpty()) {
	        for (SelectRelatedResource value : identityData.getRelatedResource()) {
	            validateRelatedResource(value);
	            relResource.add(createRelatedResource(value));
	        }
	    }
	    return relResource;
	}

	private void validateRelatedResource(SelectRelatedResource value) throws DiscoManagedClientException {
	    if ((value.getId() == null || value.getId().isEmpty()) && (value.getName() == null || value.getName().isEmpty())) {
	        throw new DiscoManagedClientException(DISCO_PS_PROVIDE_IDORNAME);
	    }
	}

	private RelatedResource createRelatedResource(SelectRelatedResource value) {
	    return new RelatedResource()
	            .id(value.getId())
	            .name(value.getName())
	            .referredType(value.getReferredType())
	            .role(value.getRole());
	}

	private List<RelatedParty> processRelatedParties(ModifyIdentityData identityData) throws DiscoManagedClientException {
	    List<RelatedParty> relParty = new ArrayList<>();
	    if (identityData.getRelatedParty() != null && !identityData.getRelatedParty().isEmpty()) {
	        for (SelectRelatedParty value : identityData.getRelatedParty()) {
	            validateRelatedParty(value);
	            relParty.add(createRelatedParty(value));
	        }
	    }
	    return relParty;
	}

	private void validateRelatedParty(SelectRelatedParty value) throws DiscoManagedClientException {
	    if ((value.getId() == null || value.getId().isEmpty()) && (value.getName() == null || value.getName().isEmpty())) {
	        throw new DiscoManagedClientException(DISCO_PS_PROVIDE_IDORNAME);
	    }
	}

	private RelatedParty createRelatedParty(SelectRelatedParty value) {
	    return new RelatedParty()
	            .id(value.getId())
	            .name(value.getName())
	            .role(value.getRole())
	            .referredType(value.getReferredType().toString());
	}

	private void validateValidityPeriod(ModifyIdentityData identityData) throws DiscoClientException {
	    if (identityData.getValidityPeriod() == null || identityData.getValidityPeriod().getValidFor() == null) {
	        throw new DiscoClientException(DISCO_PS_VALIDITY_MUST_NOTNULL);
	    }
	}

	private void validateLifecycleStatus(ModifyIdentityData identityData, String productSpecId) throws DiscoManagedClientException {
	    if (identityData.getLifecycleStatus() != null) {
	        Set<String> nextState = getProductSpecificationNextPossibleStates(productSpecId);
	        if (!nextState.contains(identityData.getLifecycleStatus().getValue())) {
	            throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_STATE, null, nextState.toString());
	        }
	    }
	}

	private void modifyProductSpec(String productSpecId, ModifyIdentityData identityData,
	                                List<RelatedParty> relParty, List<RelatedResource> relResource) {
	    productSpecService.modifyProductSpecDefineIdentity(
	            productSpecId, identityData.getIdentityData(),
	            relParty, relResource, TimePeriodMapper.toGenerated(identityData.getValidityPeriod().getValidFor()),
	            EntityType.PRODUCTSPECIFICATION, identityData.getLifecycleStatus());
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		Map<String, Object> variables = stateMachine.getVariablesFromUserActions();
		String productSpecId = (String) variables.get(ProductSpecConstants.PRODUCT_SPEC_ID);
		getTaskCharacteristics(productSpecId,stateMachine.getTaskDefinitionId());
		return characteristicList;

	}

	public List<CharacteristicSpecification> getTaskCharacteristics(String productSpecId, String taskDefinationId) {
		Set<String> nextPossibleStates = getProductSpecificationNextPossibleStates(productSpecId);
		int characteristicIndex = 0;
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/modify/" + ModifyIdentityData.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			JSONArray enumStatesArray = new JSONArray(nextPossibleStates);
			jsonObject.getJSONObject("ModifyIdentityData").getJSONObject("properties").getJSONObject("lifecycleStatus")
					.put("enum", enumStatesArray);
			Object identityData = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductSpecConstants.IDENTITY_DATA)
					.id(taskDefinationId + "-" + ProductSpecConstants.IDENTITY_DATA + "-" + characteristicIndex)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(identityData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
		return characteristicList;
	}

	
	public Set<String> getProductSpecificationNextPossibleStates(String entityId) {
		ProductSpecification productSpecification = queryService.fetchProductSpecById(entityId, accessTokenInterceptor.getToken());
		String currentState = productSpecification.getLifecycleStatus().toString();
		List<ProductOffering> productOfferings = queryService.fetchProductOfferingsByProductSpecId(entityId,accessTokenInterceptor.getToken());
		ProductSpecNextSates productSpecNextSates = new ProductSpecNextSates();
		return productSpecNextSates.getNextPossibleStates(currentState, productOfferings);

	}


}
