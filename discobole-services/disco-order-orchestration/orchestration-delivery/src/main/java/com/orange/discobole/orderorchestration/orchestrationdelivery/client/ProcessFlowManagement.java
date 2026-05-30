// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.client;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.ResolutionState;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;

import java.util.List;

public interface ProcessFlowManagement {
    ProcessFlow getProcessFlowById(String flowId);

    List<ProcessFlow> getProcessFlowByRelatedEntityId(String relatedEntityId);

    void createProcessFlow(ProcessFlowCreate processFlowCreate);

    void submitResolutionSateWithReason(String url, ResolutionState resolutionState, String reason);
}
