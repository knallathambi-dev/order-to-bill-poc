// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.exception.model;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Objects;

public class CoodNonRecoverableAndNonRetryableException extends RuntimeException {

    private CoodException exception;

    public CoodNonRecoverableAndNonRetryableException(CoodException exception) {
        super(exception);
        this.exception = exception;
    }

    public <T extends CoodException> void setException(T exception) {
        this.exception = exception;
    }

    @Override
    public String getMessage() {
        return exception.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return exception.getLocalizedMessage();
    }

    @Override
    public synchronized Throwable getCause() {
        return exception.getCause();
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return super.initCause(exception.getCause());
    }

    @Override
    public String toString() {
        return exception.toString();
    }

    @Override
    public void printStackTrace() {
        exception.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        exception.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        exception.printStackTrace(s);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return Objects.nonNull(exception) ? exception.fillInStackTrace() : null;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return exception.getStackTrace();
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        super.setStackTrace(exception.getStackTrace());
    }

    public static CoodNonRecoverableAndNonRetryableException of(CoodException exception) {
        return new CoodNonRecoverableAndNonRetryableException(exception);
    }
}