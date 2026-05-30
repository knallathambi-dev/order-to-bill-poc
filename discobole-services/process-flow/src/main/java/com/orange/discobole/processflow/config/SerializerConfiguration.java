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

import com.thoughtworks.xstream.XStream;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializerConfiguration {

    // By default, we want the XStreamSerializer
//    @Bean
//    public Serializer defaultSerializer() {
//        // Set the secure types on the xStream instance
//        XStream xStream = new XStream();
//        return XStreamSerializer.builder()
//                .xStream(xStream)
//                .build();
//    }

    // But for all our messages we'd prefer the JacksonSerializer due to JSON's smaller format
    @Bean
    public Serializer messageSerializer() {
        return JacksonSerializer.defaultSerializer();
    }
}