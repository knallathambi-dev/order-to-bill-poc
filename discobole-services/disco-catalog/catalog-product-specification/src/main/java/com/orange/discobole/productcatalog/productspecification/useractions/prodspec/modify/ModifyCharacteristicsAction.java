// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.useractions.prodspec.modify;

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
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.*;
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
@Component("ProductSpecModification.modifyCharacteristics")
public class ModifyCharacteristicsAction implements UserAction {

	private static final String DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY = "DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY";

	private static final String DISCO_PS_MINTOMAX_CARDINALITY_LESSOREQUAL = "DISCO_PS_MINTOMAX_CARDINALITY_LESSOREQUAL";

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
		String productSpecId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductSpecConstants.PRODUCT_SPEC_ID);
		ProductSpecification productSpec=queryService.fetchProductSpecById(productSpecId, accessTokenInterceptor.getToken());
		// only lifecycle status can be modified in launched state
		if(productSpec.getLifecycleStatus().getValue().equals(ProductSpecificationLifecycle.LAUNCHED.getValue()) ||
		productSpec.getLifecycleStatus().getValue().equals(ProductSpecificationLifecycle.RETIRED.getValue())){
			throw new DiscoManagedClientException(ProductSpecConstants.DISCO_PS_INVALID_PS_LIFECYCLE, "you can not modify entity with launched status", "");
		}
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTICS, characteristicList);
		List<ProductSpecificationCharacteristic> productSpecCharacteristics = new ArrayList<>();
		List<ProductSpecUsageSpecification> usageSpec=new ArrayList<>();
		if (null != characteristic) {
			PickCharacteristicSpecification charSpec = (PickCharacteristicSpecification) (Object) ValidationUtil
					.validatePojo(characteristic.getValue(), "productSpecification",
							"pickCharacteristicSpecification");
			createCharacteristicSpecification(charSpec.getCharacteristicSpecification(), productSpecCharacteristics);
			usageSpec=charSpec.getUsageSpecification();
		}else{
			throw new DiscoManagedClientException(DISCO_PS_PICKCHATACTERISTICSPEC_CANNOT_EMPTY);
		}

		List<UsageSpecification> usageSpecifications = new ArrayList<>();
			for (ProductSpecUsageSpecification value : usageSpec) {
				UsageSpecification usageSpecValue = new UsageSpecification();
				usageSpecValue.id(value.getId()).name(value.getName())
						.validFor(TimePeriodMapper.toGenerated(value.getValidFor()));
				usageSpecifications.add(usageSpecValue);
			}

		productSpecService.modifyProductSpecCharacteristics(productSpecId, productSpecCharacteristics,usageSpecifications);
		final Map<String, Object> variables = new HashMap<>();
		CommonUtil.setUsageModificationRelationshipLink(queryService, configurableProperties, productSpecId, variables, accessTokenInterceptor.getToken());
		return variables;
	}
	
	@SuppressWarnings("unchecked")
	private void createCharacteristicSpecification(List<ProductSpecCharacteristicSpecification> charSpec,
	                                              List<ProductSpecificationCharacteristic> productSpecCharacteristics) {
	    for (ProductSpecCharacteristicSpecification value : charSpec) {
	        ProductSpecificationCharacteristic charSpecValue = new ProductSpecificationCharacteristic();
	        
	        List<ProductSpecificationCharacteristicValue> characteristicValueList = processCharacteristicValues(value);
	        List<ProductSpecificationCharacteristicRelationship> characteristicRelList = processCharacteristicRelationships(value);
	        
	        validateCardinality(value, characteristicValueList);

	        charSpecValue.id(value.getId())
	                .name(value.getName())
	                .minCardinality(value.getMinCardinality())
	                .maxCardinality(value.getMaxCardinality())
	                .description(value.getDescription())
	                .configurable(value.getConfigurable())
	                .isUnique(value.getIsUnique())
	                .extensible(value.getExtensible())
	                .validFor(TimePeriodMapper.toGenerated(value.getValidFor()))
	                .productSpecCharRelationship(characteristicRelList)
	                .productSpecCharacteristicValue(characteristicValueList);

	        productSpecCharacteristics.add(charSpecValue);
	    }
	}

	private List<ProductSpecificationCharacteristicValue> processCharacteristicValues(ProductSpecCharacteristicSpecification value) {
	    List<ProductSpecificationCharacteristicValue> characteristicValueList = new ArrayList<>();
	    if (value.getCharacteristicValueSpecification() != null) {
	        for (ProductCharValue charValueSpec : value.getCharacteristicValueSpecification()) {
	            if (charValueSpec.getIsSelectable() == null) {
	                charValueSpec.setIsSelectable(true);
	            }
	            characteristicValueList.add(ConverterUtil.convert(charValueSpec));
	        }
	    }
	    return characteristicValueList;
	}

	private List<ProductSpecificationCharacteristicRelationship> processCharacteristicRelationships(ProductSpecCharacteristicSpecification value) {
	    List<ProductSpecificationCharacteristicRelationship> characteristicRelList = new ArrayList<>();
	    if (value.getInternalCharSpecRelationship() != null) {
	        for (ServiceSpecificationCharRelationship serviceSpecRel : value.getInternalCharSpecRelationship()) {
	            characteristicRelList.add(ConverterUtil.convert(serviceSpecRel));
	        }
	    }
	    return characteristicRelList;
	}

	private void validateCardinality(ProductSpecCharacteristicSpecification value, List<ProductSpecificationCharacteristicValue> characteristicValueList) {
	    int defaultCount = (int) characteristicValueList.stream()
	            .filter(ProductSpecificationCharacteristicValue::isIsDefault)
	            .count();

	    if (value.getMinCardinality() > value.getMaxCardinality()) {
	        throw new DiscoManagedClientException(DISCO_PS_MINTOMAX_CARDINALITY_LESSOREQUAL);
	    }

	    if (defaultCount > value.getMaxCardinality()) {
	        throw new DiscoManagedClientException(DISCO_PS_DEFAULTTOMAX_CARDINALITY_LESSOREQUAL);
	    }
	}


	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTICS + "-" + characteristicIndex);
		return characteristicList;
	}

}
