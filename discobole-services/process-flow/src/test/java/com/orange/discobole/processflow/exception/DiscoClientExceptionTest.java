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
//import com.orange.disco.processflow.exception.DiscoClientException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class DiscoClientExceptionTest {
//
//	private final Integer EXPECTED_CODE = 1;
//	private final HttpStatus EXPECTED_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
//
//	@Test
//	public void clientException() {
//		try {
//			throw new DiscoClientException();
//		} catch (DiscoClientException e) {
//			assertThat(e.getCode()).isEqualTo(EXPECTED_CODE);
//			assertThat(e.getStatus()).isEqualTo(EXPECTED_STATUS);
//		}
//	}
//
//	@Test
//	public void clientExceptionWithReason() {
//		try {
//			throw new DiscoClientException("test_client_exception");
//		} catch (DiscoClientException e) {
//			assertThat(e.getCode()).isEqualTo(EXPECTED_CODE);
//			assertThat(e.getStatus()).isEqualTo(EXPECTED_STATUS);
//		}
//	}
//
//}



