// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo;


import com.orange.discobole.productcatalog.productoffering.dto.generated.common.LifecycleState;

import jakarta.validation.constraints.NotNull;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
public class DefineLifecycleState {

	@NotNull
	private LifecycleState state;

	public LifecycleState getState() {
		return state;
	}

	public void setState(LifecycleState state) {
		this.state = state;
	}

}
