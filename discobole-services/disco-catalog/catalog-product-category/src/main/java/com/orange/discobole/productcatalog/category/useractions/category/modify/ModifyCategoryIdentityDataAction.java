// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.useractions.category.modify;

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
import com.orange.discobole.productcatalog.category.pojo.category.DefineCategoryIdentityData;
import com.orange.discobole.productcatalog.category.pojo.category.DefineEntity;
import com.orange.discobole.productcatalog.category.pojo.category.DefineSubcategory;
import com.orange.discobole.productcatalog.category.service.ModifyCategoryService;
import com.orange.discobole.productcatalog.category.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.category.util.FileUtil;
import com.orange.discobole.productcatalog.category.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component("CategoryModification.modifyDefineCategoryIdentityData")
public class ModifyCategoryIdentityDataAction implements UserAction {

	private static final String DISCO_CATEGORY_INVALID_INPUT_PARAMETERS = "DISCO_CATEGORY_INVALID_INPUT_PARAMETERS";

	private static final String DISCO_CATEGORY_ENTITY_TYPE_NOT_MATCHING_WITH_CATEGORYTYPE = "DISCO_CATEGORY_ENTITY_TYPE_NOT_MATCHING_WITH_CATEGORYTYPE";

	@Resource
	private ObjectMapper objectMapper;
	
	@Resource 
	private ModifyCategoryService modifyCategoryService;

	private List<CharacteristicSpecification> characteristicList;

	@PostConstruct
	public void init() throws JSONException, JsonMappingException, JsonProcessingException,Exception {
		characteristicList = new ArrayList<>();

			String file = FileUtil.read("/schemas/category/" + DefineCategoryIdentityData.class.getSimpleName() + ".json");
			Object categoryIdentityData = null;
			categoryIdentityData = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(CategoryConstants.CATEGORY_IDENTITY_DATA)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(
							List.of(new ObjectCharacteristicValueSpecification().value(categoryIdentityData)
									.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		
	}
	
	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		List<String> subCategoryIds=new ArrayList<>();
		List<String> productOfferingIds=new ArrayList<>();
		DefineCategoryIdentityData defineCategoryIdentityData=new DefineCategoryIdentityData();
		Characteristic characteristics = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				CategoryConstants.CATEGORY_IDENTITY_DATA, characteristicList);
		String categoryId = (String) stateMachine.getVariablesFromUserActions()
				.get(CategoryConstants.CATEGORY_ID);
		String categoryType = "ProductOfferingCategory";
		if (null != characteristics) {
			defineCategoryIdentityData = (DefineCategoryIdentityData) ValidationUtil.validatePojo(characteristics.getValue(),
					"category", "defineCategoryIdentityData");
		}
		final Map<String, Object> variables = new HashMap<>();
		String name=defineCategoryIdentityData.getCategoryIdentityData().getName();
		String description=defineCategoryIdentityData.getCategoryIdentityData().getDescription();
		Boolean isRoot=defineCategoryIdentityData.getCategoryIdentityData().getIsRoot();
		String parentId=defineCategoryIdentityData.getCategoryIdentityData().getParentId();

		if (Boolean.TRUE.equals(isRoot)) {
			parentId = null;
		}


		int subcategoryCount = (defineCategoryIdentityData.getDefineSubcategory() != null)
				? defineCategoryIdentityData.getDefineSubcategory().size()
				: 0;

		int entityCount = (defineCategoryIdentityData.getAssociateProductOffering() != null)
				? defineCategoryIdentityData.getAssociateProductOffering().size()
				: 0;


		if (subcategoryCount > 0 && entityCount > 0) {
			throw new DiscoManagedClientException(DISCO_CATEGORY_INVALID_INPUT_PARAMETERS);
		}



		if(null!=defineCategoryIdentityData.getDefineSubcategory()) {
			for (DefineSubcategory defineSubcategory : defineCategoryIdentityData.getDefineSubcategory()) {
	    if(null==defineSubcategory.getCategoryType()) {
	    	throw new DiscoManagedClientException(DISCO_CATEGORY_INVALID_INPUT_PARAMETERS);
			}
				if(!categoryType.equals(defineSubcategory.getCategoryType().getValue())) {
					throw new DiscoManagedClientException(DISCO_CATEGORY_ENTITY_TYPE_NOT_MATCHING_WITH_CATEGORYTYPE);
				}
				subCategoryIds.add(defineSubcategory.getCategoryId());
			
			}
		}

		if(null!=defineCategoryIdentityData.getAssociateProductOffering()) {
			for (DefineEntity defineEntity : defineCategoryIdentityData.getAssociateProductOffering()) {

				if(null==defineEntity.getEntityType()) {
					throw new DiscoManagedClientException(DISCO_CATEGORY_INVALID_INPUT_PARAMETERS);

				}
				if(!categoryType.contains(defineEntity.getEntityType().getValue())) {
					throw new DiscoManagedClientException(DISCO_CATEGORY_ENTITY_TYPE_NOT_MATCHING_WITH_CATEGORYTYPE);
				}
				productOfferingIds.add(defineEntity.getEntityId());

			}
		}
		
		
		modifyCategoryService.modifyCategoryIdentityData(categoryId,name,description,isRoot,parentId,subCategoryIds, productOfferingIds);
		return variables;
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ CategoryConstants.CATEGORY_IDENTITY_DATA + "-" + characteristicIndex);
		return characteristicList;
	
	}

}
