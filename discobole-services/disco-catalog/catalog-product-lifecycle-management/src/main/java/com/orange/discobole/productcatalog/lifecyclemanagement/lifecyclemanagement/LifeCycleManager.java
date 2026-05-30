// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement;

import java.util.List;
import java.util.Set;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.EntityType;

/**
 * Interface LifeCycleManager provides methods to handle the state change
 * request for the different entities.
 * 
 * @author Vivek Singh
 *
 */
public interface LifeCycleManager {

	/**
     * Functon to handle the state change request.
     *
     * @param entityId
     * @param entityType
     * @param stateChange
     * @param accessToken
     * @return list of event
     */
	List<Event> changeState(String aggregateId, String entityId, EntityType entityType, String stateChange, String accessToken);

	/**
	 * Function to fetch the next possible state of the entity according to the
	 * current state.
	 * 
	 * @param entityId
	 * @param entityType
	 * @return set of next possible states
	 */
	Set<String> getNextPossibleStates(String entityId, EntityType entityType, String accessToken);
}
