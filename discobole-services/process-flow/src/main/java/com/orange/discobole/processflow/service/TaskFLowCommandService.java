// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.service;

import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.TaskFlowNotFoundException;

public interface TaskFLowCommandService {

    /**
     * method definition to update/complete the task by passing required parameters
     *
     * @param processFlowId  unique process flow id
     * @param taskFlowId     unique task flow id
     * @param taskFlowUpdate task flow update request with Channel, System, Related Party, Characteristic, Related Entity, Task Relationship and Correlation id
     * @return {@code TaskFlow}
     * @throws TaskFlowNotFoundException is thrown if task does not exist by task flow id in the process flow
     */
    TaskFlow updateTaskFlow(String processFlowId, String taskFlowId, TaskFlowUpdate taskFlowUpdate) throws TaskFlowNotFoundException;

}
