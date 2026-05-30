// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useMemo, useState} from "react";
import {useNavigate} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {toast} from "react-toastify";

import {Breadcrumb, Modal} from "../../../components";
import {useTitlePage} from "../../../hooks";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {toggleLoading} from "../../../store/actions/loadingActions";
import {setOrderType, setProductOrderId} from "../../../store/actions/orderActions";
import {cancelTaskFlowAction, updateNextTaskAction} from "../../../store/actions/taskManagerActions";
import {getProductConfiguration} from "../shared/services/productConfigurationService";
import {terminatePlanService} from "./services/terminatePlanService";
import {ConfigurationProvider} from "../shared/context/ConfigurationContext";

import PlanPreview from "./components/PlanPreview";
import PlanEditor from "./components/PlanEditor/PlanEditor";
import TerminatePlanModal from "./components/PlanEditor/Modals/TerminatePlanModal";

function hasConfigurationTerm(queryConfiguration) {
    const items = queryConfiguration?.computedProductConfigurationItem;

    if (!Array.isArray(items)) return false;

    return items.some(
        (item) => item.productConfiguration?.configurationTerm?.length > 0
    );
}

export default function EditPlan() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const {t, tNotification} = useTranslations();

    const {selectedOfferName} = useSelector((state) => state.offer);
    const {isEdit} = useSelector((state) => state.edit);
    const {relatedParty} = useSelector((state) => state.auth);
    const {planId} = useSelector((state) => state.plan);
    const {configurationId} = useSelector((state) => state.configuration);

    const [configuration, setConfiguration] = useState(null);
    const [isTerminateModalVisible, setIsTerminateModalVisible] = useState(false);

    useTitlePage(isEdit ? "editPlan" : "terminatePlan", [
        selectedOfferName,
        isEdit ? "editPlan" : "terminatePlan",
    ]);

    useEffect(() => {
        if (!configurationId) return;

        const loadConfiguration = async () => {
            const data = await getProductConfiguration(
                configurationId,
                dispatch,
                tNotification
            );
            if (data) setConfiguration(data);
        };

        loadConfiguration();
    }, [configurationId]);

    const handleConfigurationChange = useCallback((updatedConfiguration) => {
        setConfiguration(updatedConfiguration);
    }, []);

    const toggleTerminateModal = useCallback(() => {
        setIsTerminateModalVisible((prev) => !prev);
    }, []);

    const hasConfigTerm = useMemo(() => {
        return configuration ? hasConfigurationTerm(configuration) : false;
    }, [configuration]);

    const handleTerminate = useCallback(async () => {
        try {
            dispatch(toggleLoading(true));

            const result = await terminatePlanService(
                planId,
                relatedParty,
                dispatch,
                tNotification
            );

            const {nextTaskUrl, cancelTaskUrl, productOrderId} = result;

            dispatch(updateNextTaskAction(nextTaskUrl));
            dispatch(cancelTaskFlowAction(cancelTaskUrl));
            dispatch(setProductOrderId(productOrderId));
            dispatch(setOrderType("Modification"));

            navigate("/order-summary");
        } catch {
            toast.error(tNotification("plan.terminateFailed"));
        } finally {
            toggleTerminateModal();
            dispatch(toggleLoading(false));
        }
    }, [planId, relatedParty, dispatch, tNotification, navigate, toggleTerminateModal]);

    if (!configuration) return null;

    return (
        <ConfigurationProvider
            configuration={configuration}
            onConfigurationChange={handleConfigurationChange}
        >
            <main>
                <div className="container">
                    <Breadcrumb/>
                    <div className="row">
                        <div className="col-md-12">
                            <h1 className="banner-title mb-0">
                                {isEdit ? t("pages.editPlan") : t("pages.terminatePlan")}
                            </h1>
                        </div>
                    </div>

                    {!isEdit && (
                        <div className="container py-4">
                            <div className="row">
                                <div className="col-md-8">
                                    <h4 className="banner-title mb-0">
                                        {t("plan.modify.modificationsNotAuthorized")}
                                    </h4>
                                </div>
                                <div className="col-md-4">
                                    <button
                                        className="btn btn-outline-secondary"
                                        onClick={toggleTerminateModal}
                                    >
                                        {t("actions.terminatePlan")}
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {isEdit && (
                    <div className="container py-4">
                        <div className="row">
                            <div className="col-md-8">
                                <PlanEditor/>
                                <div className="mt-5">
                                    <hr/>
                                    <button
                                        className="btn btn-outline-secondary"
                                        onClick={toggleTerminateModal}
                                    >
                                        {t("actions.terminatePlan")}
                                    </button>
                                </div>
                            </div>
                            <div className="col-md-4">
                                <h5>{t("plan.selectionPreview")}</h5>
                                <PlanPreview/>
                            </div>
                        </div>
                    </div>
                )}

                {isTerminateModalVisible && (
                    <Modal
                        show={isTerminateModalVisible}
                        title={t("plan.confirmations.terminate.plan")}
                        body={
                            <TerminatePlanModal
                                hideModal={toggleTerminateModal}
                                onConfirm={handleTerminate}
                                hasConfigTerm={hasConfigTerm}
                            />
                        }
                        onClose={toggleTerminateModal}
                        className="TerminatePlanModal"
                        hideFooter
                    />
                )}
            </main>
        </ConfigurationProvider>
    );
}