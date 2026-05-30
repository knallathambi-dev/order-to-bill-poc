// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class BosInvalidEventExceptionTest {

	@Test
	void bosInvalidEventExceptionTest() {
		BosInvalidEventException bosInvalidEventException = new BosInvalidEventException(404, "You got an Not Found Exception",
				"Not Found Exception");
		bosInvalidEventException.setMessage("Not Found Exception");
		Assertions.assertEquals("You got an Not Found Exception", bosInvalidEventException.getReason());
		Assertions.assertEquals("Not Found Exception", bosInvalidEventException.getMessage());
		Assertions.assertEquals(404, bosInvalidEventException.getCode());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, bosInvalidEventException.getStatus());
	}
	@Test
	void testDefaultConstructor() {
		BosInvalidEventException ex = new BosInvalidEventException();
		Assertions.assertNull(ex.getReason());
		Assertions.assertNull(ex.getMessage());
		Assertions.assertNull(ex.getCode());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
	}

	@Test
	void testConstructorWithReason() {
		String reason = "Invalid reason";
		BosInvalidEventException ex = new BosInvalidEventException(reason);
		Assertions.assertEquals(reason, ex.getReason());
		Assertions.assertNull(ex.getMessage());
		Assertions.assertNull(ex.getCode());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
	}

	@Test
	void testConstructorWithReasonAndCause() {
		String reason = "Invalid reason";
		Throwable cause = new RuntimeException("Cause");
		BosInvalidEventException ex = new BosInvalidEventException(reason, cause);
		Assertions.assertEquals(reason, ex.getReason());
		Assertions.assertNull(ex.getMessage()); // message defaults to null
		Assertions.assertNull(ex.getCode());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
		Assertions.assertEquals(cause, ex.getCause());
	}

	@Test
	void testConstructorWithCodeReasonAndMessage() {
		Integer code = 999;
		String reason = "Error reason";
		String message = "Error message";
		BosInvalidEventException ex = new BosInvalidEventException(code, reason, message);
		Assertions.assertEquals(reason, ex.getReason());
		Assertions.assertEquals(message, ex.getMessage());
		Assertions.assertEquals(code, ex.getCode());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
	}

	@Test
	void testConstructorWithNulls() {
		BosInvalidEventException ex = new BosInvalidEventException(null, null, null);
		Assertions.assertNull(ex.getReason());
		Assertions.assertNull(ex.getMessage());
		Assertions.assertNull(ex.getCode());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
	}
}
