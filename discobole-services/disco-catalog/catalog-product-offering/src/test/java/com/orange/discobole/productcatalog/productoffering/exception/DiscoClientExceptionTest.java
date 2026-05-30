// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.orange.discobole.productcatalog.productoffering.exception.DiscoClientException;

class DiscoClientExceptionTest {

	private final Integer EXPECTED_CODE = 1;
	private final HttpStatus EXPECTED_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

	@Test
	void clientException() {
		try {
			throw new DiscoClientException();
		} catch (DiscoClientException e) {
			assertThat(e.getCode()).isEqualTo(EXPECTED_CODE);
			assertThat(e.getStatus()).isEqualTo(EXPECTED_STATUS);
		}
	}

	@Test
	void clientExceptionWithReason() {
		try {
			throw new DiscoClientException("test_client_exception");
		} catch (DiscoClientException e) {
			assertThat(e.getCode()).isEqualTo(EXPECTED_CODE);
			assertThat(e.getStatus()).isEqualTo(EXPECTED_STATUS);
		}
	}

}
