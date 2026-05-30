// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering.modify;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
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
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.lifecycle.ProductOfferingBundlingNextStates;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineProductOfferingMarketSegment;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineProductOfferingSaleChannel;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectRelatedParty;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.ModifyDefineContractProductOfferingIdentityData;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.CommonUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

import jakarta.annotation.Resource;

/**
 * The Class DefineContractProductOffDescAction provides the user input to
 * describe Contract product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component("ProductOfferingModification.modifyContractDescriptionData")
public class ModifyContractProductOffDescAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(ModifyContractProductOffDescAction.class);

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ModifyProductOfferingService productOfferingService;

	@Resource
	private QueryService queryService;

	/**
	 * Inits the characteristics with default values.
	 */


	/**
	 * Perform method processes the user input to describe product offering.
	 *
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map of system variables
	 */

	@Override
	public Map<String, Object> perform(final StateMachineTransition stateMachineTransition,
									   final TaskFlowUpdate taskFlowUpdate) throws ParameterException {
		LOGGER.info("DefineContractProductOffDescAction perform method");
		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);
		// get characteristic list as it is dynamic so it is not put in posConstruct
		if (characteristicList == null) {
			characteristicList = getTaskCharacteristics(productOffId, stateMachineTransition.getTaskDefinitionId());
		}
		ModifyDefineContractProductOfferingIdentityData identityData = new ModifyDefineContractProductOfferingIdentityData();
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.CONTRACT_IDENTITY_DATA, characteristicList);
		if (null != characteristic) {
			identityData = (ModifyDefineContractProductOfferingIdentityData) ValidationUtil.validatePojo(
					characteristic.getValue(), "productOffering", "modifyDefineContractProductOfferingIdentityData");
		}

		validateIdentityData(identityData);

		Set<String> channelIds = extractChannelIds(identityData);
		Set<String> marketSegmentIds = extractMarketSegmentIds(identityData);
		Set<RelatedParty> relatedParties = extractRelatedParties(identityData);
		Set<ProductOfferingTerm> productOfferingTerms = extractProductOfferingTerms(identityData);
		// check lifecycle
		if (identityData.getLifecycleStatus() != null) {
			Set<String> nextState = getNextPossibleStates(productOffId);
			if (!nextState.contains(identityData.getLifecycleStatus().getValue())) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_LIFECYCLE_SELECTED,null,nextState.toString());
			}
		}
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		productOfferingService.modifyContractProductOffDesc(productOffId, identityData.getDefineData(), channelIds,
				marketSegmentIds, relatedParties, productOfferingTerms,
				TimePeriodMapper.toGenerated(identityData.getValidityPeriod().getValidFor()),
				ProductOfferingType.fromValue((String) stateMachineTransition.getVariablesFromUserActions()
						.get(ProductOffConstants.PRODUCTOFFERINGTYPE)),
				"Contract Product Offering initial description", lastUpdate,identityData.getLifecycleStatus());
		return new HashMap<>();
	}

	private void validateIdentityData(ModifyDefineContractProductOfferingIdentityData identityData)
			throws DiscoManagedClientException {
		if (identityData.getDefineData() == null) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_IDENTITY_DATA_CANNOT_NULL);
		}
		if (identityData.getValidityPeriod() == null) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_VALIDITY_MUST_NOTNULL);
		}
	}
	private Set<String> extractChannelIds(ModifyDefineContractProductOfferingIdentityData identityData) {
		return identityData.getChannels() != null
				? identityData.getChannels().stream()
				.map(DefineProductOfferingSaleChannel::getChannelId)
				.collect(Collectors.toSet())
				: new HashSet<>();
	}
	private Set<String> extractMarketSegmentIds(ModifyDefineContractProductOfferingIdentityData identityData) {
		return identityData.getMarketSegments() != null
				? identityData.getMarketSegments().stream()
				.map(DefineProductOfferingMarketSegment::getSegmentId)
				.collect(Collectors.toSet())
				: new HashSet<>();
	}

	private Set<RelatedParty> extractRelatedParties(ModifyDefineContractProductOfferingIdentityData identityData)
			throws DiscoManagedClientException {
		if (identityData.getRelatedParties() == null) {
			return new HashSet<>();
		}

		Set<RelatedParty> relatedParties = new HashSet<>();
		for (SelectRelatedParty value : identityData.getRelatedParties()) {
			if (StringUtils.isBlank(value.getId()) && StringUtils.isBlank(value.getName())) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_PROVIDE_IDORNAME);
			}
			relatedParties.add(new RelatedParty()
					.id(value.getId())
					.name(value.getName())
					.role(value.getRole())
					.referredType(value.getReferredType().toString()));
		}
		return relatedParties;
	}

	private Set<ProductOfferingTerm> extractProductOfferingTerms(ModifyDefineContractProductOfferingIdentityData identityData) {
		if (identityData.getPoTerms() == null) {
			return new HashSet<>();
		}

		return identityData.getPoTerms().stream()
				.map(value -> new ProductOfferingTerm()
						.duration(CommonUtil.convertToDuration(value.getDuration()))
						.name(value.getName())
						.description(value.getDescription())
						.validFor(TimePeriodMapper.toGenerated(value.getValidFor())))
				.collect(Collectors.toSet());
	}



	private Set<String> getNextPossibleStates(String productOffId) {
		ProductOffering productOffering = queryService.fetchProductOfferingById(productOffId, null);
		String currentState = productOffering.getLifecycleStatus().toString();

		List<ProductOffering> bundleProductOfferings = queryService
				.fetchBundleAndContractProductOfferingByBundlingProductOfferingId(productOffId);
		ProductOfferingBundlingNextStates productOfferingBundlingNextSates = new ProductOfferingBundlingNextStates();
		return productOfferingBundlingNextSates.getNextPossibleStates(currentState, bundleProductOfferings);

	}

	/**
	 * Required characteristics.
	 *
	 * @param stateMachine the stateMachine
	 * @return the list
	 */
	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		Map<String, Object> variables = stateMachine.getVariablesFromUserActions();
		String productSpecId = (String) variables.get(ProductOffConstants.PRODUCT_OFF_ID);
		getTaskCharacteristics(productSpecId, stateMachine.getTaskDefinitionId());
		return characteristicList;

	}
	
	public List<CharacteristicSpecification> getTaskCharacteristics(String productSpecId, String taskDefinationId) {
		Set<String> nextPossibleStates = getNextPossibleStates(productSpecId);
		int characteristicIndex = 0;
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read("/schemas/productoffering/"
					+ ModifyDefineContractProductOfferingIdentityData.class.getSimpleName() + ".json");
			JSONObject jsonObject = new JSONObject(file);
			JSONArray enumStatesArray = new JSONArray(nextPossibleStates);
			jsonObject.getJSONObject("ModifyDefineContractProductOfferingIdentityData").getJSONObject("properties")
					.getJSONObject("lifecycleStatus").put("enum", enumStatesArray);
			Object identityData = objectMapper.readValue(jsonObject.toString(), Object.class);
			characteristicList.add(new CharacteristicSpecification().name(ProductOffConstants.CONTRACT_IDENTITY_DATA)
					.id(taskDefinationId + "-" + ProductOffConstants.CONTRACT_IDENTITY_DATA + "-" + characteristicIndex)
					.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
					.characteristicValueSpecification(List.of(new ObjectCharacteristicValueSpecification()
							.value(identityData).type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
		return characteristicList;
	}

}
