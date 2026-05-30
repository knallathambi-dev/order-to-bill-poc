// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.exception;

/**
 * BosClientException is thrown if any exception occurs while serving a request
 * originated by RestTemplate
 *
 * @author Prateek Gupta
 * @since 1.1
 */
public class DiscoClientException extends DiscoException {

	/**
	 * Constructs a BosClientException with no detail message.
	 */
	public DiscoClientException() {
		super();
	}

	/**
	 * Constructs a BosClientException with the specified detail message.
	 *
	 * @param reason - the detail message
	 */
	public DiscoClientException(final String reason) {
		super(reason);
	}

}
