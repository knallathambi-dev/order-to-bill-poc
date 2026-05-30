// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.handler;

import com.orange.discobole.productcatalog.policyrule.exception.DiscoException;
import org.springframework.http.HttpStatus;

/**
 * The Class AuthenticationException.
 * 
 * @author Sunny Srivastava
 * @since 1.1
 */
public class AuthenticationException extends DiscoException {

	private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

	private static final Integer CODE = 40;

	/**
	 * Instantiates a new authentication exception.
	 *
	 * @param reason the reason for exception
	 */
	public AuthenticationException(String reason) {
		super(reason);
	}

	/**
	 * Instantiates a new authentication exception.
	 *
	 * @param reason the reason for exception
	 * @param cause throws exception
	 */
	public AuthenticationException(String reason, Throwable cause) {
		super(reason, cause);
	}

	@Override
	public HttpStatus getStatus() {
		return STATUS;
	}

	@Override
	public Integer getCode() {
		return CODE;
	}

}
