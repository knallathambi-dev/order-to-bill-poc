// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.exception.InvalidParameterException;

/**
 * The Class CharacteristicUtil to get characteristics entered by user.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public class CharacteristicUtil {

	private CharacteristicUtil() {
	}

	private static final Logger LOGGER = LogManager.getLogger(CharacteristicUtil.class);

	/**
	 * Gets the characteristic.
	 *
	 * @param characteristics    the characteristics
	 * @param name               the name of characteristic to be fetched
	 * @param characteristicList the characteristic list
	 * @return the characteristic
	 */
	public static Characteristic getCharacteristic(final List<Characteristic> characteristics, final String name,
			List<CharacteristicSpecification> characteristicList) {
		List<Characteristic> characteristic = new ArrayList<>();
		if (null == name) {
			final String message = "name cannot be null";
			LOGGER.error(message);
			throw new IllegalArgumentException(message);
		}
		CharacteristicSpecification characteristicSpec = characteristicList.stream()
				.filter(c -> name.equalsIgnoreCase(c.getName())).findAny().orElse(null);
		if (characteristicSpec != null) {
			int minCardinality = characteristicSpec.getMinCardinality();
			int maxCardinality = characteristicSpec.getMaxCardinality();
			if (null == characteristics && minCardinality > 0) {
				throw new InvalidParameterException(name + " characteristic cannot be null");
			} else if (null != characteristics) {
				characteristic = characteristics.stream().filter(c -> name.equalsIgnoreCase(c.getName()))
						.collect(Collectors.toList());
				checkCardinality(characteristic, minCardinality, maxCardinality, name);
			}
		}
		return characteristic.stream().filter(c -> name.equalsIgnoreCase(c.getName())).findAny().orElse(null);
	}

	/**
	 * Check cardinality.
	 *
	 * @param characteristic the characteristic
	 * @param minCardinality the min cardinality
	 * @param maxCardinality the max cardinality
	 * @param name           the name
	 */
	private static void checkCardinality(final List<Characteristic> characteristic, int minCardinality,
			int maxCardinality, String name) {
		if (characteristic instanceof List) {
			if (characteristic.isEmpty() && minCardinality > 0) {
				throw new InvalidParameterException(name + " characteristic cannot be empty");
			} else if (characteristic.size() < minCardinality || characteristic.size() > maxCardinality) {
				throw new InvalidParameterException("Minimum " + minCardinality + " and maximum " + maxCardinality + " "
						+ name + " characteristic can be defined");
			}
		} else if (null == characteristic && minCardinality == 1) {
			throw new InvalidParameterException("Atleast one characteristic is mandatory");
		}
	}

}
