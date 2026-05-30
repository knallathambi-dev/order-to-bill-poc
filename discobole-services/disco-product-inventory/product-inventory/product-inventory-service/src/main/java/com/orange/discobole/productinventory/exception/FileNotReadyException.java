// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.exception;

import java.io.Serializable;

public final class FileNotReadyException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L; // Explicitly define serialVersionUID

    public FileNotReadyException(String message) {
        super(message);
    }

    public FileNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotReadyException(Throwable cause) {
        super(cause);
    }
}
