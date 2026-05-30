// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.pojo;


import com.orange.discobole.processflow.annotation.DefaultValue;
import jakarta.validation.constraints.NotNull;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
public class CancelEntityOperation {

	@NotNull
	@DefaultValue("true")
	private Boolean isCancelled;

	public Boolean isIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

}
