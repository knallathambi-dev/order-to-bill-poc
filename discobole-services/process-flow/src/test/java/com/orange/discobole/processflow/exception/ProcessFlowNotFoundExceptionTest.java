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
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//
//import com.orange.disco.processflow.exception.ProcessFlowNotFoundException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertTrue;
//
//public class ProcessFlowNotFoundExceptionTest {
//
//    private static final HttpStatus EXPECTED_STATUS = HttpStatus.NOT_FOUND;
//    private static final Integer EXPECTED_CODE = 60;
//
//    @Test
//    public void processFlowNotFoundExceptionTest() {
//        try {
//            throw new ProcessFlowNotFoundException("process not found");
//        } catch (ProcessFlowNotFoundException e) {
//
//            assertThat(e.getCode()).isEqualTo(EXPECTED_CODE);
//            assertThat(e.getStatus()).isEqualTo(EXPECTED_STATUS);
//            assertThat(e.getReason()).isEqualTo("process not found");
//        }
//    }
//
//    @Test
//    public void processFlowNotFoundExceptionWithCauseTest() {
//        try{
//            throw new ProcessFlowNotFoundException("process not found", new NullPointerException());
//        }catch (ProcessFlowNotFoundException e){
//            assertTrue(e.getCause() instanceof NullPointerException);
//        }
//    }
//}


