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
import com.orange.discobole.productinventory.dto.v1.JobSchedulerType;
import com.orange.discobole.productinventory.dto.v1.JobSpecificationType;
import com.orange.discobole.productinventory.dto.v1.JobTypeEnum;
import com.orange.discobole.productinventory.dto.v1.PurgeTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
public final class UnsupportedTypeException extends RuntimeException implements Serializable {

    private final transient Error exceptionResponse;

    public UnsupportedTypeException(String entity, String type) {
        this.exceptionResponse = new Error(
                501,
                HttpStatus.NOT_IMPLEMENTED.getReasonPhrase(),
                "This operation is not Implemented for this " + entity + " Type: " + type,
                HttpStatus.NOT_IMPLEMENTED
        );
    }

    public UnsupportedTypeException(JobSpecificationType jobSpecificationType) {
        this.exceptionResponse = new Error(
                501,
                HttpStatus.NOT_IMPLEMENTED.getReasonPhrase(),
                "This operation is not Implemented for this jobSpecification Type: " + jobSpecificationType.getValue(),
                HttpStatus.NOT_IMPLEMENTED
        );
    }

    public UnsupportedTypeException(JobTypeEnum jobTypeEnum) {
        this.exceptionResponse = new Error(
                501,
                HttpStatus.NOT_IMPLEMENTED.getReasonPhrase(),
                "This operation is not Implemented for this job Type: " + jobTypeEnum.getValue(),
                HttpStatus.NOT_IMPLEMENTED
        );
    }

    public UnsupportedTypeException(JobSchedulerType jobSchedulerType) {
        this.exceptionResponse = new Error(
                501,
                HttpStatus.NOT_IMPLEMENTED.getReasonPhrase(),
                "This operation is not Implemented for this jobSpecification Schedule Type: " + jobSchedulerType.getValue(),
                HttpStatus.NOT_IMPLEMENTED
        );
    }

    public UnsupportedTypeException(@NotNull @Valid PurgeTypeEnum purgeType) {
        this.exceptionResponse = new Error(
                501,
                HttpStatus.NOT_IMPLEMENTED.getReasonPhrase(),
                "This operation is not Implemented for this purge Type: " + purgeType.getValue(),
                HttpStatus.NOT_IMPLEMENTED
        );
    }
}
