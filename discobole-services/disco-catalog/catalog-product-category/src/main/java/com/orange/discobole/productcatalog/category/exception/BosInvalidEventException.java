// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.exception;

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

	private Integer code;
	private String reason;
	private String message;

	/**
	 * Instantiates a new bos exception.
	 */
	public BosInvalidEventException() {
		super();
	}

	/**
	 * Instantiates a new bos exception.
	 *
	 * @param reason the reason for exception
	 */
	public BosInvalidEventException(String reason) {
		super(reason);
	}

	/**
	 * Instantiates a new bos exception.
	 *
	 * @param reason the reason for exception
	 * @param cause  throws exception
	 */
	public BosInvalidEventException(String reason, Throwable cause) {
		super(reason, cause);
	}

	public BosInvalidEventException(Integer code, String reason, String message) {
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
