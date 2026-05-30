// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.service;

import com.orange.discobole.orderorchestration.exception.model.CoodDBException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanErrorMessage;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.OrchestrationPlanNode;
import com.orange.discobole.orderorchestration.orchestrationdelivery.outbox.records.NodeStateChangeRecord;

import java.util.List;
import java.util.Map;

public interface OrchestrationPlanModificationService {
    int updateNodeStateById(NodeStateChangeRecord stateChangeRecord) throws CoodDBException;

    int updateNodeStateAndAddErrorMessageById(NodeStateChangeRecord stateChangeRecord) throws CoodDBException;

    int updatePlanStateById(String planId, State state) throws CoodDBException;

    int updatePlanStateAndAddErrorMessageById(String nodeId, List<OrchestrationPlanErrorMessage> errorMessage, State previousState, State state) throws CoodDBException;

    void updateNodeDataInOrchestrationPlan(OrchestrationPlanNode updatedNode, Map<String, Object> headers) throws CoodDBException;
}
