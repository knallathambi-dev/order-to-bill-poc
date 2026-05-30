// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.orange.discobole.processflow.dto.generated.Error;
import com.orange.discobole.processflow.dto.generated.ErrorRepresentation;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.DiscoManagedException;

import jakarta.annotation.Resource;

/**
 * Global Exception Handler to catch the exception and convert into proper
 * response with respective http status.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	@Resource
	private ErrorService service;

	private static final Logger LOGGER = LogManager.getLogger(GlobalExceptionHandler.class);

	/**
	 * Handle missing request body exception.
	 *
	 * @param exception the exception for the invalid request
	 * @return the response entity showing error message
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorRepresentation> handleMissingBodyException(
			final HttpMessageNotReadableException exception) {
		final ErrorRepresentation error = new ErrorRepresentation();
		error.setCode(21);
		error.setReason("missing request body");
		error.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
		if (exception.getCause() instanceof JsonParseException
				|| exception.getCause() instanceof MismatchedInputException
				|| exception.getCause() instanceof JsonMappingException) {
			error.code(22);
			error.setReason("invalid request body");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	/**
	 * Handle method argument not valid exception.
	 *
	 * @param exception the exception for invalid argument
	 * @return the response entity showing error message
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorRepresentation> handleMethodArgumentNotValidException(
			final MethodArgumentNotValidException exception) {
		final ErrorRepresentation error = new ErrorRepresentation();
		error.setCode(23);
		final Map<String, String> errors = new LinkedHashMap<>();
		exception.getBindingResult().getAllErrors().forEach(err -> {
			if (err instanceof FieldError) {
				final FieldError fieldErr = (FieldError) err;
				errors.put(fieldErr.getField(), fieldErr.getDefaultMessage());
			}
		});
		error.setReason(errors.toString());
		error.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	/**
	 * Handle http media type not supported exception.
	 *
	 * @param exception the exception for invalid content type
	 * @return the response entity showing error message
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorRepresentation> handleHttpMediaTypeNotSupportedException(
			final HttpMediaTypeNotSupportedException exception) {
		final ErrorRepresentation error = new ErrorRepresentation();
		error.setCode(25);
		error.setReason("mismatch request content-type");
		error.setMessage("content type 'application/json' expected");
		error.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	/**
	 * Handle processflow exception.
	 *
	 * @param exception the exception to handle processflow exception
	 * @return the response entity showing error message
	 */

	@ExceptionHandler(DiscoException.class)
	public ResponseEntity<ErrorRepresentation> handleDiscoException(final DiscoException exception) {
		LOGGER.error("handlling DiscoException", exception);
		final ErrorRepresentation error = new ErrorRepresentation();
		error.setCode(exception.getCode());
		error.setReason(exception.getReason());
		error.setMessage(exception.getMessage());
		error.setStatus(String.valueOf(exception.getStatus().value()));
		return ResponseEntity.status(exception.getStatus()).body(error);
	}

	/**
	 * Handle managed disco exception.
	 *
	 * @param exception the exception to handle managed disco exception
	 * @return the response entity showing error message
	 */
	@ExceptionHandler(DiscoManagedException.class)
	public ResponseEntity<ErrorRepresentation> handleManagedDiscoException(final DiscoManagedException exception) {
		LOGGER.error("handling Managed DiscoException", exception.getReason());
		final ErrorRepresentation error = new ErrorRepresentation();
		Error errorsave = new Error();

		errorsave = service.fetchErrorById(exception.getReason());
		if (errorsave != null) {
			error.setCode(errorsave.getCode());
			if (exception.getReasonAppender() != null) {
				error.setReason(errorsave.getReason() + " " + exception.getReasonAppender());
			} else {
				error.setReason(errorsave.getReason());
			}

			if (exception.getMessageAppender() != null) {
				error.setMessage(errorsave.getMessage() + " " + exception.getMessageAppender());
			} else {
				error.setMessage(errorsave.getMessage());
			}
			error.setStatus(errorsave.getStatus());
			error.setReferenceError(errorsave.getReferenceError());
			error.setBaseType(errorsave.getBaseType());
			error.baseType(errorsave.getType());
			error.setSchemaLocation(errorsave.getSchemaLocation());
		} else {
			error.setCode(exception.getCode());
			error.setReason(exception.getReason());
			error.setMessage(exception.getMessage());
			error.setStatus(String.valueOf(exception.getStatus().value()));
			error.setReferenceError(exception.getReferenceError());
			error.baseType(exception.getBaseType());
			error.setType(exception.getType());
			error.setSchemaLocation(exception.getSchemaLocation());
			return ResponseEntity.status(exception.getStatus()).body(error);
		}

		return ResponseEntity.status(Integer.parseInt(error.getStatus())).body(error);
	}

	/**
	 * Handle other exception.
	 *
	 * @param exception the exception to handle other exceptions
	 * @return the response entity showing error message
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorRepresentation> handleOtherException(final Exception exception) {
		LOGGER.error("handleOtherException", exception);
		final ErrorRepresentation error = new ErrorRepresentation();
		error.setCode(1);
		error.setReason(exception.getMessage());
		error.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

}
