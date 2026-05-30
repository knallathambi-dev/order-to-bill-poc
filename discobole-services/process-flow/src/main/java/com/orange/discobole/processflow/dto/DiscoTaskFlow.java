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

package com.orange.discobole.processflow.dto;

import org.springframework.data.annotation.Id;

import com.orange.discobole.processflow.dto.generated.TaskFlow;

public class DiscoTaskFlow {

    @Id
    private String processFLowId;
    private TaskFlow taskFlow;

    public DiscoTaskFlow() {
    }

    public DiscoTaskFlow(String processFLowId, TaskFlow taskFlow) {
        this.processFLowId = processFLowId;
        this.taskFlow = taskFlow;
    }

    @Override
    public String toString() {
        return "DiscoTaskFlow{" +
                "processFLowId='" + processFLowId + '\'' +
                ", taskFlow=" + taskFlow +
                '}';
    }

    public String getProcessFLowId() {
        return processFLowId;
    }

    public void setProcessFLowId(String processFLowId) {
        this.processFLowId = processFLowId;
    }

    public TaskFlow getTaskFlow() {
        return taskFlow;
    }

    public void setTaskFlow(TaskFlow taskFlow) {
        this.taskFlow = taskFlow;
    }
}
