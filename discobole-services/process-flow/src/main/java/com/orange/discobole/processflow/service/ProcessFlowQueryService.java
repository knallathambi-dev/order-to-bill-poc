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

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.exception.ProcessFlowNotFoundException;

public interface ProcessFlowQueryService {

    /**
     * method definition to fetch all active process flows.
     *
     * @return List of {@code ProcessFlow}
     */
    List<ProcessFlow> findProcessFlows(Map<String, Object> requestParams);

    /**
     * method definition to fetch process flow by id.
     *
     * @param processFlowId unique process flow id
     * @return {@code ProcessFlow}
     * @throws ProcessFlowNotFoundException is thrown if process does not exist
     *                                      by process flow id
     */
    ProcessFlow findProcessFlowById(Map<String, Object> requestParams) throws ProcessFlowNotFoundException;

}
