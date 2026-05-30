// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering.modify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.delegate.UserAction;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristicValueSpecification;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.ParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.BundledProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.BundledProductOfferingOption;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.BundledProductOfferings;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.ManageProductOfferingBundling;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class DefineProductOfferingBundlingAction provides the user input to
 * define Bundling for product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component("ProductOfferingModification.modifyBundledPO")
public class ModifyProductOfferingBundlingAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingBundlingAction.class);

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ModifyProductOfferingService productOfferingService;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private QueryService queryService;
	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil
					.read("/schemas/productoffering/" + ManageProductOfferingBundling.class.getSimpleName() + ".json");
			Object operationSpec = null;
			operationSpec = objectMapper.readValue(file, Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.PRODUCT_OFFERING_BUNDLING)
					.valueType(List.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(operationSpec).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> perform(StateMachineTransition stateMachine, TaskFlowUpdate taskFlowUpdate)
			throws ParameterException {
		LOGGER.info("In perform method of DefineProductOfferingBundlingAction");
		String productOffId = (String) stateMachine.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		ProductOffering productOffering=queryService.fetchProductOfferingById(productOffId, null);
		// only lifecycle status can be modified in the launched state
		if(productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.LAUNCHED.getValue()) ||
				productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.RETIRED.getValue())){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_LIFECYCLE, "you can not modify entity with launched or retired status", "");
		}
		Characteristic bundling = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.PRODUCT_OFFERING_BUNDLING, characteristicList);
		List<BundledProductOffering> productOfferBundlings = new ArrayList<>();
		boolean cardinalitieCheck = false;
		if (bundling != null) {
			ManageProductOfferingBundling manageProductBundling = (ManageProductOfferingBundling)ValidationUtil
					.validatePojo(bundling.getValue(), "productOffering", "defineProductOfferingBundling");
				for(BundledProductOfferings bundledProductOffering : manageProductBundling.getBundledProductOffering()) {
					BundledProductOffering bundlePo = new BundledProductOffering();
					bundlePo.setId(bundledProductOffering.getId());
					BundledProductOfferingOption bundledProductOfferingOption = new BundledProductOfferingOption();
					bundledProductOfferingOption.setNumberRelOfferDefault(bundledProductOffering.getBundledProductOfferingOption().getNumberRelOfferDefault());
					bundledProductOfferingOption.setNumberRelOfferLowerLimit(bundledProductOffering.getBundledProductOfferingOption().getNumberRelOfferLowerLimit());
					bundledProductOfferingOption.setNumberRelOfferUpperLimit(bundledProductOffering.getBundledProductOfferingOption().getNumberRelOfferUpperLimit());
					bundlePo.setBundledProductOfferingOption(bundledProductOfferingOption);
					if(bundledProductOfferingOption.getNumberRelOfferDefault()!= 0 ||  bundledProductOfferingOption.getNumberRelOfferLowerLimit()!=0
							|| bundledProductOfferingOption.getNumberRelOfferUpperLimit()!=0 ) {
						cardinalitieCheck = true;						
					}
					productOfferBundlings.add(bundlePo);
				}
				if(productOfferBundlings.isEmpty()) {
					throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_MUST_HAVE_ONE_CHILD);
					
				}
				if (!cardinalitieCheck) {
					throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_CARDINALITY_FOR_SIGLE_CHILD);
				}
			productOfferingService.modifyBundleProductOfferings(productOffId, productOfferBundlings, manageProductBundling.getGlobalMinCardinality(), manageProductBundling.getGlobalMaxCardinality());
		}
		return new HashMap<>();
	}

	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachineTransition, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachineTransition.getTaskDefinitionId() + "-"
				+ ProductOffConstants.PRODUCT_OFFERING_BUNDLING + "-" + characteristicIndex);
		return characteristicList;
	}

}
