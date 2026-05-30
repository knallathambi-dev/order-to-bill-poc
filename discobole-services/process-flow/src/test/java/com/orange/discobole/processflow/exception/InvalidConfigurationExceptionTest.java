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

//package com.orange.disco.processflow.exception;
//
//import com.fasterxml.jackson.databind.exc.MismatchedInputException;
//import com.orange.disco.processflow.exception.InvalidConfigurationException;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//
//import static org.assertj.core.api.Assertions.as;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertTrue;
//
//public class InvalidConfigurationExceptionTest {
//
//    private static final HttpStatus EXPECTED_STATUS = HttpStatus.BAD_REQUEST;
//
//    private static final Integer EXPECTED_CODE = 60;
//
//    @Test
//    public void invalidConfigurationTest() {
//        try {
//            throw new InvalidConfigurationException("invalid configuration");
//        } catch (InvalidConfigurationException e) {
//            assertThat(e.getCode()).isEqualTo(EXPECTED_CODE);
//            assertThat(e.getStatus()).isEqualTo(EXPECTED_STATUS);
//            assertThat(e.getReason()).isEqualTo("invalid configuration");
//        }
//    }
//
//    @Test
//    public void invalidConfigurationWithCauseTest() {
//        try {
//            throw new InvalidConfigurationException("invalid configuration", new NullPointerException());
//
//        } catch (InvalidConfigurationException e) {
//            assertTrue(e.getCause() instanceof NullPointerException);
//        }
//    }
//}


