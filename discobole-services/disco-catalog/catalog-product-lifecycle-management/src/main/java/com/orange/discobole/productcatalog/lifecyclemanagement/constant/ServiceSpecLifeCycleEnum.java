// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * These variables are used to tell the status of the CFS lifeCycle.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public enum ServiceSpecLifeCycleEnum {

	/**
	 * Active cfs life cycle enum.
	 */
	ACTIVE("active"),
	/**
	 * Launched cfs life cycle enum.
	 */
	LAUNCHED("launched"),
	/**
	 * Unavailable cfs life cycle enum.
	 */
	UNAVAILABLE("unavailable"),
	/**
	 * Retired cfs life cycle enum.
	 */
	RETIRED("retired"),
	/**
	 * Obsolete cfs life cycle enum.
	 */
	OBSOLETE("obsolete");

	private final String status;

	ServiceSpecLifeCycleEnum(String status) {
		this.status = status;
	}

	/**
	 * Converts the specified string to enum
	 *
	 * @param text String to be converted to enum
	 * @return converted enum
	 */
	@JsonCreator
	public static ServiceSpecLifeCycleEnum from(String text) {
		for (ServiceSpecLifeCycleEnum b : ServiceSpecLifeCycleEnum.values()) {
			if (String.valueOf(b.status).equals(text)) {
				return b;
			}
		}
		return null;
	}


	@Override
	public String toString() {
		return String.valueOf(status);
	}

	/**
	 * Gets status.
	 *
	 * @return the status
	 */
	@JsonValue
	public String getStatus() {
		return status;
	}

}