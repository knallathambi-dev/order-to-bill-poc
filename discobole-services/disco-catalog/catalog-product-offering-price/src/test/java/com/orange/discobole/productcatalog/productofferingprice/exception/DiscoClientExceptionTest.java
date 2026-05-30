// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class DiscoClientExceptionTest {

	private final Integer expectedCode = 1;
	private final HttpStatus expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;

	@Test
	void clientException() {
		try {
			throw new DiscoClientException();
		} catch (DiscoClientException e) {
			assertThat(e.getCode()).isEqualTo(expectedCode);
			assertThat(e.getStatus()).isEqualTo(expectedStatus);
		}
	}

	@Test
	void clientExceptionWithReason() {
		try {
			throw new DiscoClientException("test_client_exception");
		} catch (DiscoClientException e) {
			assertThat(e.getCode()).isEqualTo(expectedCode);
			assertThat(e.getStatus()).isEqualTo(expectedStatus);
		}
	}

}
