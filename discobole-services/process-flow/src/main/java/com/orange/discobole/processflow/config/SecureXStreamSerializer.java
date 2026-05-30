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

package com.orange.discobole.processflow.config;

import org.axonframework.serialization.xml.XStreamSerializer;

import com.thoughtworks.xstream.XStream;

public class SecureXStreamSerializer {
private static XStreamSerializer _instance;
	
	public static XStreamSerializer get() {
		if (_instance == null) {
			_instance = secureXStreamSerializer();
		}
		return _instance;
	}
	
	private static XStreamSerializer secureXStreamSerializer() {
		XStream xStream = new XStream();
		xStream.setClassLoader(SecureXStreamSerializer.class.getClassLoader());
		xStream.allowTypesByWildcard(new String[]{
			"org.axonframework.**",
			"com.orange.disco**"
		});
		return XStreamSerializer.builder().xStream(xStream).build();
	}

}
