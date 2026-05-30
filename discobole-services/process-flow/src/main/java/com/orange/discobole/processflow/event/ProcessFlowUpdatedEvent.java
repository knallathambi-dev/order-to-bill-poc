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

package com.orange.discobole.processflow.event;

import org.axonframework.modelling.command.AggregateIdentifier;

import com.orange.discobole.processflow.dto.generated.ProcessFlow;

public final class ProcessFlowUpdatedEvent implements Event {
	@AggregateIdentifier
	private final String processFlowId;
    private final ProcessFlow processFlow;

    public ProcessFlowUpdatedEvent() {
        this.processFlow = null;
        this.processFlowId=null;
    }

    public ProcessFlowUpdatedEvent(ProcessFlow processFlow,String processFlowId) {
        this.processFlow = processFlow;
        this.processFlowId=processFlowId;
    }

    @Override
	public String toString() {
		return "ProcessFlowUpdatedEvent [processFlowId=" + processFlowId + ", processFlow=" + processFlow + "]";
	}

	public ProcessFlow getProcessFlow() {
        return processFlow;
    }

	public String getProcessFlowId() {
		return processFlowId;
	}
	
}
