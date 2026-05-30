// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.service;

import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.LifecycleState;

/**
 * Contract to process Product lifecycle related Commands.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public interface LifeCycleService {

	/**
	 * Fucntion to handle select entity command.
	 * 
	 * @param id
	 * @param entityType
	 * @return aggregate id
	 */
	String selectEntity(String id, String entityType);

	/**
	 * Function to handle the update lifecycle command.
	 * 
	 * @param entityId
	 * @param state
	 */
	void updateStatus(String entityId, LifecycleState state,String version);

}
