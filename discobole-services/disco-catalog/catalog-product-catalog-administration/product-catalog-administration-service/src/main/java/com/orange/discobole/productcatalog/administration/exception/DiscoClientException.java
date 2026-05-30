// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.exception;


/**
 * DiscoClientException is thrown if any exception occurs while serving a request
 * originated by RestTemplate
 *
 * @author Piyush Goel
 * @since 1.0
 */
public class DiscoClientException extends DiscoException {

    /**
     * Constructs a DiscoClientException with no detail message.
     */
    public DiscoClientException() {
        super();
    }

    /**
     * Constructs a DiscoClientException with the specified detail message.
     *
     * @param reason - the detail message
     */
    public DiscoClientException(String reason) {
        super(reason);
    }

}


