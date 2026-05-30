// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.constant;

/**
 * This enum represents the lifecycle states of
 * {@code com.orange.bos.catalogconfigurator.dto.generated.productspec.ProductSpecification}
 * and
 * {@code com.orange.bos.catalogconfigurator.dto.generated.servicespec.ServiceSpecification}
 * used in catalog-configurator service.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public enum ResourceState {

	IN_STUDY("inStudy"), IN_DESIGN("inDesign"), IN_TEST("inTest"), REJECTED("rejected"), ACTIVE("active"),
	LAUNCHED("launched"), UNAVAILABLE("unavailable"), RETIRED("retired"), OBSOLETE("obsolete");

	private final String state; // Renamed to avoid conflict with class name

	ResourceState(String state) {
		this.state = state;
	}

	/**
	 * This method is used to get lifecycle state of
	 * {@code com.orange.bos.catalogconfigurator.dto.generated.productspec.ProductSpecification}
	 * and
	 * {@code com.orange.bos.catalogconfigurator.dto.generated.servicespec.ServiceSpecification}
	 * in string format.
	 *
	 * @return returns the string representation of lifecycle state
	 */
	public String getState() {
		return state;
	}

	/**
	 * This method is used to get the {@code ResourceState} associated with the
	 * value provided.
	 *
	 * @param state string representation of value of resource state
	 * @return returns the lifecycle state in {@code ResourceState} enum format
	 */
	public static ResourceState fromValue(String state) {
		for (ResourceState b : ResourceState.values()) {
			if (String.valueOf(b.state).equals(state)) {
				return b;
			}
		}
		return null;
	}

	/**
	 * This method is used to get lifecycle state of
	 * {@code com.orange.bos.catalogconfigurator.dto.generated.productspec.ProductSpecification}
	 * and
	 * {@code com.orange.bos.catalogconfigurator.dto.generated.servicespec.ServiceSpecification}
	 * in string format.
	 *
	 * @return returns the string representation of lifecycle state
	 */
	@Override
	public String toString() {
		return String.valueOf(state);
	}
}