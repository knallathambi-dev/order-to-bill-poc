// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.exception;

import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.ReportType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
public final class UnsupportedTypeException extends RuntimeException implements Serializable {

    private final transient Error exceptionResponse;

    public UnsupportedTypeException(String entity, String type) {
        super("This operation is not Implemented for this " + entity + " Type: " + type);
        this.exceptionResponse = new Error(
                501,
                HttpStatus.NOT_IMPLEMENTED.getReasonPhrase(),
                "This operation is not Implemented for this " + entity + " Type: " + type,
                HttpStatus.NOT_IMPLEMENTED
        );
    }

    public UnsupportedTypeException(ReportType reportType) {
        super("This operation is not Implemented for this Report Type: " + reportType.getValue());
        this.exceptionResponse = new Error(
                501,
                HttpStatus.NOT_IMPLEMENTED.getReasonPhrase(),
                "This operation is not Implemented for this Report Type: " + reportType.getValue(),
                HttpStatus.NOT_IMPLEMENTED
        );
    }
}
