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
 * Constants for Process Variable keys
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
public class ProcessConstants {

	private ProcessConstants() {
	}

	public static final String CHARACTERISTIC = "processCharacteristic";
	public static final String CHANNEL = "processChannel";
	public static final String RELATED_PARTY = "processRelatedParty";
	public static final String RELATED_ENTITY = "processRelatedEntity";
	public static final String ON_METHOD_VARIABLE = "onMethod";
	public static final String DESCRIPTION_VARIABLE = "message";
	public static final String INITIAL_AUTOMATIC_STATE_VARIABLE = "InitialAutomatedTask";
	public static final String INITIAL_AUTOMATIC_EVENT_VARIABLE = "InitialAutomatedEvent";
	public static final String HIDDEN_STATES = "hiddenStates";



}
