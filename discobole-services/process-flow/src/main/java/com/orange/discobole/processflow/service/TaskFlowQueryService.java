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

import java.util.List;
import java.util.Map;

import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.processflow.exception.TaskFlowNotFoundException;

public interface TaskFlowQueryService {

    /**
     * method definition to fetch active task flows of a process instance
     *
     * @param processFlowId aka process flow id
     * @return List of {@code TaskFlow}
     */
    List<TaskFlow> findTaskFlowsByProcessFlowId(Map<String, Object> requestParams);

    /**
     * method definition to fetch tasks of process instance by task id
     *
     * @param processFlowId unique process flow id
     * @param taskFlowId    unique task flow id
     * @return {@code TaskFlow}
     * @throws TaskFlowNotFoundException is thrown if task does not exist by task id in the process instance
     */
    TaskFlow findTaskFlowById(Map<String, Object> requestParams) throws TaskFlowNotFoundException;
}
