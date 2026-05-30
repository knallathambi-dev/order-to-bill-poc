// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.category.constant.CategoryConstants;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryAssociateEntity;
import com.orange.discobole.productcatalog.category.pojo.category.DefineEntity;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;
import com.orange.discobole.productcatalog.category.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.category.util.FileUtil;
import com.orange.discobole.productcatalog.category.util.ValidationUtil;

@Component("CategoryModification.modifyAssociatedEntity")
public class ModifyAssociatedEntityAction implements UserAction{

	private static final String DISCO_CATEGORY_ENTITY_TYPE_NOT_MATCHING_WITH_CATEGORYTYPE = "DISCO_CATEGORY_ENTITY_TYPE_NOT_MATCHING_WITH_CATEGORYTYPE";

	private static final String DISCO_CATEGORY_INVALID_INPUT_PARAMETERS = "DISCO_CATEGORY_INVALID_INPUT_PARAMETERS";

	@Resource
	private ObjectMapper objectMapper;
	
	@Resource 
	private ModifyCategoryService modifyCategoryService;

	private List<CharacteristicSpecification> characteristicList;
	@PostConstruct
	public void init() throws JSONException, JsonMappingException, JsonProcessingException  {
		characteristicList = new ArrayList<>();

			String file = FileUtil.read("/schemas/category/" + DefineEntity.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			JSONArray enumEntityArray = new JSONArray(List.of(CategoryAssociateEntity.PRODUCTSPECIFICATION.toString(),
					CategoryAssociateEntity.PRODUCTOFFERING.toString(),CategoryAssociateEntity.PRODUCTOFFERINGPRICE.toString()));
			jsonObject.getJSONObject("DefineEntity").getJSONObject("items").getJSONObject("properties")
					.getJSONObject("entityType").put("enum", enumEntityArray);
			Object defineEntityData = null;
			defineEntityData = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.DEFINE_ENTITY)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(
							List.of(new ObjectCharacteristicValueSpecification().value(defineEntityData)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		List<String> productOfferingIds=new ArrayList<>();
		List<DefineEntity> defineEntities=new ArrayList<>();
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				CategoryConstants.DEFINE_ENTITY, characteristicList);
		String categoryId = (String) stateMachine.getVariablesFromUserActions()
				.get(CategoryConstants.CATEGORY_ID);
		String categoryType = "ProductOffering";
		if (null != characteristics) {
			defineEntities = (List<DefineEntity>) (Object) ValidationUtil.validateArrayOfPojo(characteristics.getValue(),
					"category", "defineEntity");
		}
			if(null!=defineEntities) {
			for (DefineEntity defineEntity : defineEntities) {
				
				if(null==defineEntity.getEntityType()) {
					throw new DiscoManagedClientException(DISCO_CATEGORY_INVALID_INPUT_PARAMETERS);

			}
				if(!categoryType.contains(defineEntity.getEntityType().getValue())) {
					throw new DiscoManagedClientException(DISCO_CATEGORY_ENTITY_TYPE_NOT_MATCHING_WITH_CATEGORYTYPE);
				}
				productOfferingIds.add(defineEntity.getEntityId());
			
			}
		}
		final Map<String, Object> variables = new HashMap<>();
		modifyCategoryService.modifyAssociatedEntity(categoryId,productOfferingIds);
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ CategoryConstants.DEFINE_ENTITY + "-" + characteristicIndex);
		return characteristicList;
	
	}

}
