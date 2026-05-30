// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from "react";
import {BootstrapTooltip, LoadingIndicator, Modal, StatusPanel, useSideMenu} from "@discobole/common-ui";
import api from "../service/OrderInventoryAPI.js";

const TASK_SETTINGS = [
    {
        key: "checkCommercialEligibilityEnabled",
        label: "Check commercial eligibility",
        description: "Validate offer eligibility for acquisition use case",
    },
    {
        key: "reserveLogicalResourceEnabled",
        label: "Reserve logical resources",
        description: "Reserve logical resources such as MSISDN, IMSI ... that are needed to fulfill the order",
    },
    {
        key: "reservePhysicalResourceEnabled",
        label: "Reserve physical resources",
        description: "Reserve physical resources such as handset, SIM Card ... that are needed to fulfill the order",
    },
    {
        key: "checkPaymentRefEnabled",
        label: "Check payment reference",
        description: "Check the validity of payment reference(s) provided during complete order step",
    },
    {
        key: "checkBillingAccountRefEnabled",
        label: "Check billing account reference",
        description: "Check the validity of billing account reference(s) provided during complete order step",
    },
    {
        key: "checkAndSetBillCycleDateEnabled",
        label: "Check and set bill cycle",
        description: "Once enabled, the process will set the delivery date for modification and termination orders to the end of the bill cycle if recurring charges are already installed",
    },
    {
        key: "checkPartyManagementEnabled",
        label: "Check related party information",
        description: "Check related party identifier and role",
    },
    {
        key: "checkAppointmentRefEnabled",
        label: "Check and set appointment",
        description: "Check the validity of appointment reference(s) provided during complete order step",
    },
    {
        key: "checkTechnicalEligibilityEnabled",
        label: "Check technical eligibility",
        description: "Check the technical eligibility of the selected fiber offer based on the provided installation address",
    },
    {
        key: "checkFinancialEligibilityEnabled",
        label: "Check Financial Eligibility",
        description: "Verify whether the user is eligible to acquire the selected offer with installment options",
    },
];

const DEFAULT_SETTINGS = Object.fromEntries(
    TASK_SETTINGS.map(({key}) => [key, false])
);

const ProcessTasksSettings = () => {
    const {isActiveNav} = useSideMenu();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [taskSettings, setTaskSettings] = useState(DEFAULT_SETTINGS);
    const [isModified, setIsModified] = useState(false);
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [showSuccessModal, setShowSuccessModal] = useState(false);

    const fetchSettings = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await api.getTaskSettings();
            setTaskSettings(data);
            setIsModified(false);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSettings();
    }, []);

    const handleToggle = (key) => (e) => {
        setTaskSettings((prev) => ({...prev, [key]: e.target.checked}));
        setIsModified(true);
    };

    const handleSave = async () => {
        try {
            await api.saveTaskSettings(taskSettings);
            setIsModified(false);
            setShowConfirmModal(false);
            setShowSuccessModal(true);
        } catch {
            setShowConfirmModal(false);
        }
    };

    if (loading) return <LoadingIndicator/>;

    if (error) {
        return (
            <StatusPanel
                variant="error"
                title="Something went wrong"
                message={error?.message}
                onAction={fetchSettings}
                actionLabel="Retry"
            />
        );
    }

    return (
        <>
            <div className={`py-1 content-wrapper ${isActiveNav ? "active-cont" : ""}`}>
                <div className="mt-3 mb-5">
                    <div className="alert alert-info alert-dismissible fade show" role="alert">
                        <span className="alert-icon">
                            <span className="visually-hidden">Warning</span>
                        </span>
                        <p>
                            Any saved change you make will affect the process definition, but
                            you can change it anytime from the settings page.
                        </p>
                        <BootstrapTooltip title="Close" placement="top" disposeOnAlertClose>
                            <button
                                type="button"
                                className="btn-close"
                                data-bs-dismiss="alert"
                                aria-label="Close"
                            >
                                <span className="visually-hidden">Close</span>
                            </button>
                        </BootstrapTooltip>
                    </div>

                    <div className="row">
                        <div className="col-md-12">
                            <h1 className="display-3 mb-4">Process tasks</h1>
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-md-8">
                            <ul className="list-group list-group-flush">
                                {TASK_SETTINGS.map(({key, label, description}) => (
                                    <li
                                        key={key}
                                        className="list-group-item d-flex justify-content-between align-items-center px-0 ps-1"
                                    >
                                        <div className="me-auto">
                                            <h6 className="mb-0">{label}</h6>
                                            <div className="fw-normal mt-1 lh-base text-muted">
                                                {description}
                                            </div>
                                        </div>
                                        <div className="form-check form-switch">
                                            <input
                                                className="form-check-input"
                                                type="checkbox"
                                                role="switch"
                                                id={`switch-${key}`}
                                                checked={taskSettings[key]}
                                                onChange={handleToggle(key)}
                                            />
                                        </div>
                                    </li>
                                ))}
                            </ul>

                            <div className="d-grid gap-3 d-flex justify-content-end mt-4">
                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={fetchSettings}
                                >
                                    Reset all changes
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-primary"
                                    onClick={() => setShowConfirmModal(true)}
                                    disabled={!isModified}
                                >
                                    Save changes
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Confirm Modal */}
            <Modal open={showConfirmModal} onClose={() => setShowConfirmModal(false)}>
                <div className="modal-header">
                    <h5 className="modal-title">Are you sure you want to make this change?</h5>
                    <BootstrapTooltip title="Close" placement="top">
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"/>
                    </BootstrapTooltip>
                </div>
                <div className="modal-body">
                    This change will affect all the future results, but you can change it
                    anytime from the settings page.
                </div>
                <div className="modal-footer">
                    <button
                        type="button"
                        className="btn btn-outline-secondary"
                        data-bs-dismiss="modal"
                    >
                        Cancel
                    </button>
                    <button type="button" className="btn btn-primary" onClick={handleSave}>
                        Save changes
                    </button>
                </div>
            </Modal>

            {/* Success Modal */}
            <Modal open={showSuccessModal} onClose={() => setShowSuccessModal(false)}>
                <div className="modal-header">
                    <BootstrapTooltip title="Close" placement="top">
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"/>
                    </BootstrapTooltip>
                </div>
                <div className="modal-body">
                    <div className="text-center">
                        <div className="success-circle mb-4">
                            <em className="icon-checkbox_tick"/>
                        </div>
                        <h6>The changes saved successfully</h6>
                    </div>
                </div>
            </Modal>
        </>
    );
};

export default ProcessTasksSettings;