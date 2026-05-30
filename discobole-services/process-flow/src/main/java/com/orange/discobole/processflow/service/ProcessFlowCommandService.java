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

import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.ProcessFlowCreate;

public interface ProcessFlowCommandService {

    /**
     * method definition to create new {@code ProcessFlow} instance.
     *
     * @param processFlowCreate process flow create request with Process Type, Channel, System,
     *                          Related Party, Characteristic and Correlation id
     * @return {@code ProcessFlow}
     */
    ProcessFlow createProcessFlow(ProcessFlowCreate processFlowCreate);

    /**
     * * method definition to delete process by id.
     *
     * @param processFlowId unique process flow id
     */
    void deleteProcessFlow(String processFlowId);
}
