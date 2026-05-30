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

import com.orange.discobole.processflow.constant.ProcessFlowConstants;

/**
 * An interface for the Event.
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
public interface Event {

	/**
	 * Sets Aggregate name using a constant.
	 *
	 * @return the string
	 */
	default String aggregateName() {
		return ProcessFlowConstants.AGGREGATE_NAME;
	}
}
