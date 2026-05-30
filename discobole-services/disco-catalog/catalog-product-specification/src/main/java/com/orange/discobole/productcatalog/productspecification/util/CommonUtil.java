// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.orange.discobole.processflow.constant.TaskConstants;
import com.orange.discobole.processflow.dto.generated.StringCharacteristic;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecRelationship;
import com.orange.discobole.productcatalog.productspecification.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;

/**
 * The Class CommonUtil.
 *
 * @since 1.0
 */
public class CommonUtil {

	private CommonUtil() {

	}

	/**
	 * Sets the relationship link.
	 *
	 * @param queryService           the query service
	 * @param configurableProperties the configurable properties
	 * @param serviceSpecId          the service spec id
	 * @param variables              the variables
	 * @param accessToken
	 */
	public static void setRelationshipLink(QueryService queryService, ConfigurableProperties configurableProperties,
										   String serviceSpecId, final Map<String, Object> variables, String accessToken) {
		List<ServiceSpecRelationship> serviceSpecRelationship = queryService.getServiceSpecById(serviceSpecId, accessToken)
				.getServiceSpecRelationship();
		if (serviceSpecRelationship != null) {
			List<String> serviceSpecRelationshipsId = serviceSpecRelationship.stream()
					.map(ServiceSpecRelationship::getId).collect(Collectors.toList());
			String findServiceSpecsSource = configurableProperties.getProductSpecQuery() + "?ServiceSpecification.id="
					+ String.join(",", serviceSpecRelationshipsId);
			variables.put(TaskConstants.CHARACTERISTIC, List.of(new StringCharacteristic().value(findServiceSpecsSource)
					.name("productSpecsUrl").valueType("Source").type(StringCharacteristic.class.getSimpleName())));
		} else {
			variables.put(TaskConstants.CHARACTERISTIC, List.of(new StringCharacteristic().value(null)
					.name("productSpecsUrl").valueType("Source").type(StringCharacteristic.class.getSimpleName())));
		}
	}
	
	public static void setUsageModificationRelationshipLink(QueryService queryService,
															ConfigurableProperties configurableProperties, String productSpecId, final Map<String, Object> variables, String accessToken) {
		ProductSpecification productSpec = queryService.fetchProductSpecById(productSpecId, accessToken);
		if (!(productSpec == null || productSpec.getServiceSpecification().isEmpty())) {
			String serviceSpecId = productSpec.getServiceSpecification().get(0).getId();
			setRelationshipLink(queryService, configurableProperties, serviceSpecId, variables,accessToken);
		}
	}

	public static boolean checkCharacteristicValueRange(List<ProductCharValue> productSpecCharacteristicValue,
			Integer minCardinality, Integer maxCardinality) {
		boolean check = true;
		int size = productSpecCharacteristicValue.size();
		if (size < minCardinality || size > maxCardinality) {
			check = false;
		}
		return check;
	}

}
