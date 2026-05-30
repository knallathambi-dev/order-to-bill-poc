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
// VALIDATION SCHEMA BUILDER
// ============================================

/**
 * Builds a Yup validation schema based on the current job type and occurrence.
 *
 * The schema is dynamic — different fields become required depending on
 * which job specification type and schedule occurrence are selected.
 *
 * @param {string} jobType - The selected job specification type (e.g. "ExportJobSpecification")
 * @param {string} occurrence - The selected schedule occurrence (e.g. "RecurringJobScheduler")
 * @returns {Yup.ObjectSchema} The assembled Yup validation schema
 */
export const buildValidationSchema = (jobType, occurrence) => {
    const shape = {
        // --- Always-required base fields ---
        jobSpecificationType: Yup.string().required("Job Specification Type is required"),
        name: Yup.string().required("Name is required"),
        occurrence: Yup.string().required("Occurrence is required"),
        repeatEvery: Yup.number().min(1, "Must be at least 1"),
        repeatInterval: Yup.string(),
    };

    // --- Schedule fields (conditional on occurrence) ---
    if (occurrence === "OneTimeJobScheduler") {
        shape.plannedDate = Yup.date()
            .nullable()
            .required("Planned Date is required");
    }

    if (occurrence === "RecurringJobScheduler") {
        shape.repeatFrom = Yup.date()
            .nullable()
            .required("Repeat From date is required");

        shape.repeatTo = Yup.date()
            .nullable()
            .test(
                "repeat-to-after-from",
                "Repeat To must be after Repeat From",
                function (value) {
                    const {repeatFrom} = this.parent;
                    return !value || !repeatFrom || new Date(value) > new Date(repeatFrom);
                },
            );

        shape.executionTime = Yup.date()
            .nullable()
            .required("Execution Time is required");
    }

    // --- Job-type-specific fields ---
    if (jobType === "ExportJobSpecification") {
        shape.contentType = Yup.string().required("Content Type is required");
    }

    if (jobType === "PurgeJobSpecification") {
        shape.purgeType = Yup.string().required("Purge Type is required");

        shape.productStatus = Yup.array().when("purgeType", {
            is: "PurgeProduct",
            then: (s) => s.min(1, "Product Status is required").required(),
            otherwise: (s) => s.nullable(),
        });

        shape.lifecycleStatus = Yup.array().when("purgeType", {
            is: "PurgeJob",
            then: (s) => s.min(1, "Job Specification Status is required").required(),
            otherwise: (s) => s.nullable(),
        });
    }

    if (jobType === "ImportJobSpecification") {
        shape.contentType = Yup.string().required("Content Type is required");
        shape.importType = Yup.string().required("Import Type is required");
        shape.selectedFile = Yup.mixed().required("Import file is required");
    }

    return Yup.object().shape(shape);
};

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