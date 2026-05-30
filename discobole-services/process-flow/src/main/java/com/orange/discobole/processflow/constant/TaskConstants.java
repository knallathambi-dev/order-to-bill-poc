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

package com.orange.discobole.processflow.constant;

/**
 * Constants for Task Variable keys
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
public class TaskConstants {

	private TaskConstants() {
	}

	public static final String CHARACTERISTIC = "taskCharacteristic";
	public static final String CHANNEL = "taskChannel";
	public static final String RELATED_PARTY = "taskRelatedParty";
	public static final String RELATED_ENTITY = "relatedEntity";
}
