// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import * as Yup from "yup";

// ============================================
// VALIDATION SCHEMA
// ============================================

/**
 * Validation schema for the Products Exporting form.
 * Content Type is the only required field.
 */
export const productsExportingValidationSchema = Yup.object().shape({
    contentType: Yup.string().required("Content Type is required"),
});

// ============================================
// VALIDATION HELPERS
// ============================================

/**
 * Checks whether a given field currently has a validation error.
 *
 * This helper intentionally does NOT gate on `formik.touched` —
 * errors are shown immediately (validate-on-mount behaviour).
 *
 * @param {object} formik - The Formik bag
 * @param {string} fieldName - The field key to check
 * @returns {boolean}
 */
export const hasFieldError = (formik, fieldName) => !!formik.errors[fieldName];

/**
 * Returns the error message string for a field, or null if the field is valid.
 *
 * @param {object} formik - The Formik bag
 * @param {string} fieldName - The field key to check
 * @returns {string|null}
 */
export const getFieldError = (formik, fieldName) =>
    formik.errors[fieldName] || null;
