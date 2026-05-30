// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristicRelationship;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristicValue;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productspecification.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productspecification.pojo.*;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;
import com.orange.discobole.productcatalog.productspecification.util.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Component("ProductSpecCreation.defCharacteristics")
public class DefCharacteristicsAction implements UserAction {


	private static final String DISCO_PS_MINTOMAX_CARDINALITY_LESSOREQUAL = "DISCO_PS_MINTOMAX_CARDINALITY_LESSOREQUAL";
	private static final String DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY = "DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY";
	private static final String DISCO_PS_DEFAULTTOMAX_CARDINALITY_LESSOREQUAL = "DISCO_PS_DEFAULTTOMAX_CARDINALITY_LESSOREQUAL";

	@Resource
	private ProductSpecService productSpecService;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private QueryService queryService;

	@Resource
	private ConfigurableProperties configurableProperties;

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;


	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/" + PickCharacteristicSpecification.class.getSimpleName() + ".json");
			Object charSpec = null;
			charSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification()
					.name(ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTICS).valueType(List.class.getSimpleName())
					.minCardinality(0).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(charSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachineTransition, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		PickCharacteristicSpecification  pickCharacteristicSpecifications=new PickCharacteristicSpecification();
		String productSpecId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductSpecConstants.PRODUCT_SPEC_ID);
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTICS, characteristicList);
		List<ProductSpecificationCharacteristic> productSpecCharacteristics = new ArrayList<>();
		
		if (null != characteristic) {
			pickCharacteristicSpecifications=(PickCharacteristicSpecification) (Object) ValidationUtil
					.validatePojo(characteristic.getValue(), "productSpecification","pickCharacteristicSpecification");
		}else{
			throw new DiscoManagedClientException(DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY);
		}
		
		if(pickCharacteristicSpecifications.getCharacteristicSpecification()!=null && !pickCharacteristicSpecifications.getCharacteristicSpecification().isEmpty()) {
			createCharacteristicSpecification(pickCharacteristicSpecifications.getCharacteristicSpecification(), productSpecCharacteristics);
		}
		List<UsageSpecification> usageSpecifications = new ArrayList<>();
		if (pickCharacteristicSpecifications.getUsageSpecification() != null
				&& !pickCharacteristicSpecifications.getUsageSpecification().isEmpty()) {
			for (ProductSpecUsageSpecification value : pickCharacteristicSpecifications.getUsageSpecification()) {
				UsageSpecification usageSpecValue = new UsageSpecification();
				usageSpecValue.id(value.getId()).name(value.getName())
						.validFor(TimePeriodMapper.toGenerated(value.getValidFor()));
				usageSpecifications.add(usageSpecValue);
			}
		}

		productSpecService.updateProductSpecCharacteristics(productSpecId, productSpecCharacteristics,usageSpecifications);
		final Map<String, Object> variables = new HashMap<>();
		String serviceSpecId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductSpecConstants.SERVICE_SPEC_ID);
			CommonUtil.setRelationshipLink(queryService, configurableProperties, serviceSpecId, variables,accessTokenInterceptor.getToken());
			
		return variables;
	}

	@SuppressWarnings("unchecked")
	private void createCharacteristicSpecification(
	        List<ProductSpecCharacteristicSpecification> charSpec,
	        List<ProductSpecificationCharacteristic> productSpecCharacteristics) {

	    for (ProductSpecCharacteristicSpecification value : charSpec) {
	        ProductSpecificationCharacteristic charSpecValue = new ProductSpecificationCharacteristic();
	        List<ProductSpecificationCharacteristicValue> characteristicValueList = new ArrayList<>();
	        int defaultCount = processCharacteristicValues(value, characteristicValueList);
	        List<ProductSpecificationCharacteristicRelationship> characteristicRelList = processCharacteristicRelationships(value);

	        validateCardinality(value, defaultCount);

	        populateCharacteristic(charSpecValue, value, characteristicValueList, characteristicRelList);
	        productSpecCharacteristics.add(charSpecValue);
	    }
	}

	private int processCharacteristicValues(
	        ProductSpecCharacteristicSpecification value,
	        List<ProductSpecificationCharacteristicValue> characteristicValueList) {

	    int defaultCount = 0;

	    if (value.getCharacteristicValueSpecification() != null) {
	        for (ProductCharValue charValue : value.getCharacteristicValueSpecification()) {
	            defaultCount += Boolean.TRUE.equals(charValue.isIsDefault()) ? 1 : 0;
	            charValue.setIsSelectable(charValue.getIsSelectable() != null ? charValue.getIsSelectable() : true);
	            charValue.setIsDefault(charValue.isIsDefault() != null ? charValue.isIsDefault() : false);
	            characteristicValueList.add(ConverterUtil.convert(charValue));
	        }
	    }

	    return defaultCount; // Return the count instead of trying to store it in 'value'
	}

	private List<ProductSpecificationCharacteristicRelationship> processCharacteristicRelationships(
	        ProductSpecCharacteristicSpecification value) {

	    List<ProductSpecificationCharacteristicRelationship> characteristicRelList = new ArrayList<>();

	    if (value.getInternalCharSpecRelationship() != null) {
	        for (ServiceSpecificationCharRelationship serviceSpecRel : value.getInternalCharSpecRelationship()) {
	            characteristicRelList.add(ConverterUtil.convert(serviceSpecRel));
	        }
	    }

	    return characteristicRelList;
	}

	private void validateCardinality(ProductSpecCharacteristicSpecification value, int defaultCount) {
	    if (value.getMinCardinality() > value.getMaxCardinality()) {
	        throw new DiscoManagedClientException(DISCO_PS_MINTOMAX_CARDINALITY_LESSOREQUAL);
	    }

	    if (defaultCount > value.getMaxCardinality()) {
	        throw new DiscoManagedClientException(DISCO_PS_DEFAULTTOMAX_CARDINALITY_LESSOREQUAL);
	    }
	}

	private void populateCharacteristic(
	        ProductSpecificationCharacteristic charSpecValue,
	        ProductSpecCharacteristicSpecification value,
	        List<ProductSpecificationCharacteristicValue> characteristicValueList,
	        List<ProductSpecificationCharacteristicRelationship> characteristicRelList) {

	    charSpecValue.id(value.getId())
	            .name(value.getName())
	            .minCardinality(value.getMinCardinality())
	            .configurable(value.getConfigurable())
	            .isUnique(value.getIsUnique())
	            .extensible(value.getExtensible())
	            .maxCardinality(value.getMaxCardinality())
	            .description(value.getDescription())
	            .validFor(TimePeriodMapper.toGenerated(value.getValidFor()))
	            .productSpecCharRelationship(characteristicRelList)
	            .productSpecCharacteristicValue(characteristicValueList);
	}



	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTICS + "-" + characteristicIndex);
		return characteristicList;
	}

}
