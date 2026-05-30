// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.exception;

import org.springframework.http.HttpStatus;

import com.orange.discobole.processflow.exception.DiscoException;

/**
 * This exception is raised when any invalid event occurs while serving
 * commands.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public class BosInvalidEventException extends DiscoException {

	private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

	private final Integer code;
	private final String reason;
	private final String message;

	/**
	 * Default constructor.
	 */
	public BosInvalidEventException() {
		super();
		this.code = null;
		this.reason = null;
		this.message = null;
	}

	/**
	 * Constructor with reason.
	 * @param reason the reason for the exception
	 */
	public BosInvalidEventException(String reason) {
		super(reason);
		this.reason = reason;
		this.code = null;
		this.message = null;
	}

	/**
	 * Constructor with reason and cause.
	 * @param reason the reason for the exception
	 * @param cause the underlying cause
	 */
	public BosInvalidEventException(String reason, Throwable cause) {
		super(reason, cause);
		this.reason = reason;
		this.code = null;
		this.message = null;
	}

	/**
	 * Constructor with code, reason, and message.
	 * @param code the error code
	 * @param reason the reason message
	 * @param message the detailed message
	 */
	public BosInvalidEventException(Integer code, String reason, String message) {
		super(message);
		this.code = code;
		this.reason = reason;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return STATUS;
	}

	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public String getReason() {
		return reason;
	}

	@Override
	public String getMessage() {
		return message;
	}
}

