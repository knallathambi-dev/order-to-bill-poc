// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemangement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement.ProductOfferingBundlingNextStates;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;
class BundleOrContractNextStatesTest extends ManageLifeCycleApplicationTests {

	private ProductOfferingBundlingNextStates productOfferingNextStates;

	@BeforeEach
	void setUp() {
		productOfferingNextStates = new ProductOfferingBundlingNextStates();
	}

	static Stream<Arguments> provideTestCases() {
		return Stream.of(
				Arguments.of("inTest", "prodOff1", ProductOfferingLifecycle.LAUNCHED, 2, "rejected"),
				Arguments.of("active", "prodOff1", ProductOfferingLifecycle.RETIRED, 2, "retired"),
				Arguments.of("active", "prodOff1", ProductOfferingLifecycle.LAUNCHED, 1, "launched"),
				Arguments.of("launched", "prodOff1", ProductOfferingLifecycle.LAUNCHED, 2, "unavailable"),
				Arguments.of("launched", "prodOff1", ProductOfferingLifecycle.RETIRED, 2, "retired"),
				Arguments.of("unavailable", "prodOff1", ProductOfferingLifecycle.LAUNCHED, 0, null),
				Arguments.of("unavailable", "prodOff1", ProductOfferingLifecycle.RETIRED, 1, "retired"),
				Arguments.of("retired", "prodOff1", ProductOfferingLifecycle.RETIRED, 1, "obsolete"),
				Arguments.of("rejected", "prodOff1", ProductOfferingLifecycle.RETIRED, 0, null)
		);
	}

	@ParameterizedTest
	@MethodSource("provideTestCases")
	void testGetNextPossibleStates(String currentState, String productId, ProductOfferingLifecycle lifecycleStatus, int expectedSize, String expectedState) {
		List<ProductOffering> productOfferings = new ArrayList<>();
		productOfferings.add(new ProductOffering().id(productId).lifecycleStatus(lifecycleStatus));
		Set<String> possibleStates = productOfferingNextStates.getNextPossibleStates(currentState, productOfferings);
		assertEquals(expectedSize, possibleStates.size());
		if (expectedState != null) {
			assertTrue(possibleStates.contains(expectedState));
		} else {
			assertTrue(possibleStates.isEmpty());
		}
	}
}
