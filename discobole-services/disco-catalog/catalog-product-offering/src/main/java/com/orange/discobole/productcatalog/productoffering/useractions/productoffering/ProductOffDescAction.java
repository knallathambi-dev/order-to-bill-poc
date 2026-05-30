// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.useractions.productoffering;

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
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.RelatedParty;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineProductOfferingMarketSegment;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineProductOfferingSaleChannel;
import com.orange.discobole.productcatalog.productoffering.pojo.ManageProductOfferingTerm;
import com.orange.discobole.productcatalog.productoffering.pojo.SelectRelatedParty;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineProductOfferingIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.Duration;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.util.CharacteristicUtil;
import com.orange.discobole.productcatalog.productoffering.util.CommonUtil;
import com.orange.discobole.productcatalog.productoffering.util.FileUtil;
import com.orange.discobole.productcatalog.productoffering.util.ValidationUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The Class ProductOffDescAction provides the user input to describe product
 * offering.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@Component("ProductOfferingCreation.description")
public class ProductOffDescAction implements UserAction {

	private static final Logger LOGGER = LogManager.getLogger(ProductOffDescAction.class);

	private List<CharacteristicSpecification> characteristicList;

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ProductOfferingService productOfferingService;

	/**
	 * Inits the characteristics with default values.
	 */
	@PostConstruct
	public void init() {
		characteristicList = new ArrayList<>();
		try {
			String file = FileUtil.read(
					"/schemas/productoffering/" + DefineProductOfferingIdentityData.class.getSimpleName() + ".json");
			Object productOfferingIdentityData = null;
			productOfferingIdentityData = objectMapper.readValue(file, Object.class);
			characteristicList
					.add(new CharacteristicSpecification().name(ProductOffConstants.IDENTITY_DATA)
							.valueType(Object.class.getSimpleName()).minCardinality(1).maxCardinality(1)
							.characteristicValueSpecification(List
									.of(new ObjectCharacteristicValueSpecification().value(productOfferingIdentityData)
											.type(ObjectCharacteristicValueSpecification.class.getSimpleName()))));
		} catch (Exception e) {
			throw new DiscoClientException(e.getMessage());
		}
	}

	/**
	 * Perform method processes the user input to describe product offering.
	 *
	 * @param stateMachineTransition the state machine transition
	 * @param taskFlowUpdate         the task flow update
	 * @return the map of system variables
	 */
	public Map<String, Object> perform(final StateMachineTransition stateMachineTransition,
									   final TaskFlowUpdate taskFlowUpdate) throws ParameterException {
		LOGGER.info("ProductOffDescAction perform method");

		DefineProductOfferingIdentityData identityData = extractIdentityData(taskFlowUpdate);
		validateIdentityData(identityData);

		String productOffId = (String) stateMachineTransition.getVariablesFromUserActions()
				.get(ProductOffConstants.PRODUCT_OFF_ID);

		Set<String> channelIds = extractChannelIds(identityData);
		Set<String> marketSegmentIds = extractMarketSegmentIds(identityData);
		Set<RelatedParty> relParty = extractRelatedParties(identityData);
		Set<ProductOfferingTerm> prodOffTerms = extractProductOfferingTerms(identityData);

		productOfferingService.defineProductOffDesc(
				productOffId,
				identityData.getDefineData(),
				channelIds,
				marketSegmentIds,
				relParty,
				prodOffTerms,
				TimePeriodMapper.toGenerated(identityData.getValidityPeriod().getValidFor()),
				ProductOfferingType.fromValue((String) stateMachineTransition.getVariablesFromUserActions()
						.get(ProductOffConstants.PRODUCTOFFERINGTYPE)),
				"Product Offering initial creation"
		);

		return new HashMap<>();
	}

	private DefineProductOfferingIdentityData extractIdentityData(TaskFlowUpdate taskFlowUpdate) {
		Characteristic characteristic = CharacteristicUtil.getCharacteristic(
				taskFlowUpdate.getCharacteristic(),
				ProductOffConstants.IDENTITY_DATA,
				characteristicList
		);
		if (characteristic != null) {
			return (DefineProductOfferingIdentityData) ValidationUtil.validatePojo(
					characteristic.getValue(),
					"productOffering",
					"defineProductOfferingIdentityData"
			);
		}
		return new DefineProductOfferingIdentityData();
	}

	private void validateIdentityData(DefineProductOfferingIdentityData identityData) {
		if (identityData.getDefineData() == null) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_IDENTITY_DATA_CANNOT_NULL);
		}
		if (identityData.getValidityPeriod() == null) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_VALIDITY_MUST_NOTNULL);
		}
	}

	private Set<String> extractChannelIds(DefineProductOfferingIdentityData identityData) {
		return identityData.getChannels() == null
				? new HashSet<>()
				: identityData.getChannels().stream()
				.map(DefineProductOfferingSaleChannel::getChannelId)
				.collect(Collectors.toSet());
	}

	private Set<String> extractMarketSegmentIds(DefineProductOfferingIdentityData identityData) {
		return identityData.getMarketSegments() == null
				? new HashSet<>()
				: identityData.getMarketSegments().stream()
				.map(DefineProductOfferingMarketSegment::getSegmentId)
				.collect(Collectors.toSet());
	}

	private Set<RelatedParty> extractRelatedParties(DefineProductOfferingIdentityData identityData) {
		Set<RelatedParty> relParty = new HashSet<>();
		if (identityData.getRelatedParties() != null) {
			for (SelectRelatedParty value : identityData.getRelatedParties()) {
				if (StringUtils.isBlank(value.getId()) && StringUtils.isBlank(value.getName())) {
					throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_PROVIDE_IDORNAME);
				}
				relParty.add(new RelatedParty()
						.id(value.getId())
						.name(value.getName())
						.role(value.getRole())
						.referredType(value.getReferredType().toString()));
			}
		}
		return relParty;
	}

	private Set<ProductOfferingTerm> extractProductOfferingTerms(DefineProductOfferingIdentityData identityData) {
		Set<ProductOfferingTerm> prodOffTerms = new HashSet<>();
		if (identityData.getPoTerms() != null) {
			Set<String> uniqueDurations = new HashSet<>();
			for (ManageProductOfferingTerm value : identityData.getPoTerms()) {
				Duration duration = value.getDuration();
				if (duration == null) {
					throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_DURATION_ATTRIBUTE_MANDATORY);
				}
				String durationKey = duration.getAmount() + "-" + duration.getUnits().toLowerCase();
				if (!uniqueDurations.add(durationKey)) {
					throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_DURATION_SHOULD_BE_UNIQUE);
				}


				prodOffTerms.add(new ProductOfferingTerm().duration(CommonUtil.convertToDuration(value.getDuration())).name(value.getName())
						.description(value.getDescription()).validFor(null));
			}
		}
		return prodOffTerms;
	}


	/**
	 * Required characteristics.
	 *
	 * @param stateMachine the stateMachine
	 * @return the list
	 */
	@Override
	public List<CharacteristicSpecification> requiredCharacteristics(StateMachineTransition stateMachine, Map<Object, Object> contextVariables) {
		int characteristicIndex = 0;
		characteristicList.get(characteristicIndex).id(stateMachine.getTaskDefinitionId() + "-"
				+ ProductOffConstants.IDENTITY_DATA + "-" + characteristicIndex);
		return characteristicList;

	}
}
