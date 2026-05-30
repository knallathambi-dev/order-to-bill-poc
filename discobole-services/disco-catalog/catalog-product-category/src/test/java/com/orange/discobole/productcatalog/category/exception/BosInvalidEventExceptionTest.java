// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.orange.discobole.productcatalog.category.exception.BosInvalidEventException;

class BosInvalidEventExceptionTest {

	private BosInvalidEventException bosInvalidEventException;

	@Test
	void bosInvalidEventExceptionTest() {
		bosInvalidEventException = new BosInvalidEventException(404, "You got an Not Found Exception",
				"Not Found Exception");
		BosInvalidEventException exception=new BosInvalidEventException("invalid Parameter");
		BosInvalidEventException exception1=new BosInvalidEventException("message", exception);
		bosInvalidEventException.setMessage("Not Found Exception");
		Assertions.assertEquals("You got an Not Found Exception", bosInvalidEventException.getReason());
		Assertions.assertEquals("Not Found Exception", bosInvalidEventException.getMessage());
		Assertions.assertEquals(404, bosInvalidEventException.getCode());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, bosInvalidEventException.getStatus());
		assertEquals(exception.getReason(),exception.getReason());
		assertEquals(exception1.getReason(),exception1.getReason());
	}
}
