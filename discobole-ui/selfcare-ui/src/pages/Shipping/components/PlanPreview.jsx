// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from "prop-types";
import React, {useCallback, useEffect, useMemo, useState} from "react";
import {useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import {AuthenticationTabs, Modal} from "../../../components";
import {toggleLoading} from "../../../store/actions/loadingActions";
import {setProductOrderId} from "../../../store/actions/orderActions";
import {cancelTaskFlowAction, updateNextTaskAction} from "../../../store/actions/taskManagerActions";
import OfferIncompatibilityModal from "../../Plan/SetUpPlan/components/Modals/OfferIncompatibilityModal";
import dayjs from "../../../utlis/dayjs-setup";
import createRequestBodyShipping from "../services/createRequestBodyShipping";
import ModifyPlanModal from "../../Plan/EditPlan/components/PlanEditor/Modals/ModifyPlanModal";
import {processOrderConfiguration} from "../../Plan/EditPlan/services/editPlanService";
import {
    createConfirmConfigurationRequest,
    createPartyIdentifierRequest
} from "../../../utlis/processFlowRequestBodyUtils";
import {calculateItemPricing} from "../../../utlis/helpers";
import processConfigurationPlanPreview from "../../Plan/shared/services/processConfigurationPlanPreview";
import NotEligibleAddress from "../../Eligibility/modal/NotEligibleAddress";
import PriceSummaryCard from "../../../components/utils/PriceSummaryCard";
import {patchTaskFlow} from "../../../services";
import apiClient from "../../../services/api/apiClient";
import {useConfiguration} from "../../Plan/shared/context/ConfigurationContext";
import {UNQUALIFIED_OFFER_MESSAGE} from "../../../utlis/constants";

const SHIPPING_TYPE = {
    HOME_DELIVERY: 1,
    IN_STORE: 2
};

const TASK_TITLES = {
    IDENTIFY_PARTY: 'OrderCapture.identifyParty',
    VALIDATE_ORDER: 'OrderCapture.validateOrder',
    SELECT_OFFER: 'OrderCapture.selectOfferOrContract'
};

const SHIPPING_CONFIG = {
    HOME_DELIVERY_MODE: "Home delivery",
    IN_STORE_MODE: "Instore",
    DEFAULT_SHOP_ADDRESS: "Orange Canebière Shop"
};

function PlanPreview({isShippingFormValid}) {
    const {configuration, relatedParty, dispatch, t, tNotification} = useConfiguration();

    const navigate = useNavigate();

    const {shippingType, address, deliveryDate} = useSelector((state) => state.shipping);
    const {orderType} = useSelector(state => state.order);
    const {nextTaskToBePerformed} = useSelector((state) => state.taskManager);
    const {pricing} = useSelector((state) => state.pricing);
    const {isAuthenticated} = useSelector((state) => state.auth);
    const {planId} = useSelector(state => state.plan);

    const [shouldShowLoginModal, setShouldShowLoginModal] = useState(false);
    const [shouldShowConfirmModal, setShouldShowConfirmModal] = useState(false);
    const [shouldShowUnqualifiedOfferModal, setShouldShowUnqualifiedOfferModal] = useState(false);
    const [shouldShowNotEligibleAddressModal, setShouldShowNotEligibleAddressModal] = useState(false);

    const [shouldTriggerPartyIdentification, setShouldTriggerPartyIdentification] = useState(false);

    const {shippingConfigItems} = useMemo(
        () => processConfigurationPlanPreview(configuration),
        [configuration]
    );

    const shippingPrice = useMemo(
        () => calculateItemPricing(shippingConfigItems[0]?.productConfiguration?.configurationPrice),
        [shippingConfigItems]
    );

    const totalPriceWithShipping = useMemo(() => {
        const shippingNrc = shippingPrice?.nrc ?? 0;
        return {
            current: {...pricing.current, nrc: pricing.current.nrc + shippingNrc},
            beforeDiscount: {...pricing.beforeDiscount, nrc: pricing.beforeDiscount.nrc + shippingNrc},
            discount: pricing.discount,
            currency: pricing.currency,
        };
    }, [pricing, shippingPrice?.nrc]);

    const isFormValid = shippingType === SHIPPING_TYPE.IN_STORE ? true : isShippingFormValid;

    const formatDateWithCurrentTime = useCallback((originalDate) => {
        const currentTime = dayjs();
        const userTimezone = dayjs.tz.guess();
        const combinedDateTime = dayjs()
            .year(originalDate.year())
            .month(originalDate.month())
            .date(originalDate.date())
            .hour(currentTime.hour())
            .minute(currentTime.minute())
            .second(currentTime.second());
        return combinedDateTime.tz(userTimezone).format("YYYY-MM-DDTHH:mm:ssZZ");
    }, []);

    const createShippingConfiguration = useCallback(async (requestBody) => {
        const productConfiguratorUrl = process.env.REACT_APP_PRODUCT_CONFIGURATOR_URL;
        dispatch(toggleLoading(true));
        try {
            const {data: createdConfig} = await apiClient.post(productConfiguratorUrl, requestBody);
            return createdConfig.id;
        } catch (error) {
            toast.error(tNotification("shipping.createFailed"));
            return null;
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [dispatch, tNotification]);

    const prepareShippingConfiguration = useCallback(async () => {
        const isInStore = shippingType === SHIPPING_TYPE.IN_STORE;
        const shippingMode = isInStore ? SHIPPING_CONFIG.IN_STORE_MODE : SHIPPING_CONFIG.HOME_DELIVERY_MODE;
        const shippingAddress = isInStore ? SHIPPING_CONFIG.DEFAULT_SHOP_ADDRESS : address || null;

        let shippingDate = null;
        if (isInStore) {
            const nextDay = dayjs().add(1, 'day');
            shippingDate = formatDateWithCurrentTime(nextDay);
        } else if (deliveryDate) {
            const selectedDate = dayjs(deliveryDate);
            shippingDate = formatDateWithCurrentTime(selectedDate);
        }

        const shippingRequestBody = createRequestBodyShipping(
            configuration?.id,
            relatedParty,
            shippingConfigItems,
            shippingMode,
            shippingAddress,
            shippingDate
        );

        return await createShippingConfiguration(shippingRequestBody);
    }, [shippingType, address, deliveryDate, configuration?.id, relatedParty, shippingConfigItems, formatDateWithCurrentTime, createShippingConfiguration]);

    const navigateToOrderSummaryWithTaskUpdate = useCallback((taskResponse) => {
        const nextTasks = taskResponse.data?._links?.nextTaskstoBePerformed || [];
        const nextTask = nextTasks[1];
        const previousTask = nextTasks[0];
        const orderId = taskResponse.data?.relatedEntity?.[0]?.id;

        dispatch(updateNextTaskAction(nextTask?.href));
        if (previousTask?.href) {
            dispatch(cancelTaskFlowAction(previousTask.href));
        }
        dispatch(setProductOrderId(orderId));
        navigate('/order-summary');
    }, [dispatch, navigate]);

    const handleTaskFlowResponse = useCallback((taskResponse, onUnqualified, onIdentifyParty, onNotEligible) => {
        const nextTasks = taskResponse.data?._links?.nextTaskstoBePerformed || [];
        const nextTask = nextTasks[1];
        const responseDescription = (taskResponse.data?.description || '').trim();

        if (responseDescription) {
            if (taskResponse.data?.description === UNQUALIFIED_OFFER_MESSAGE) {
                onUnqualified();
                return false;
            }

            if (nextTask?.title === TASK_TITLES.SELECT_OFFER && responseDescription.toLowerCase().includes('fiber')) {
                dispatch(updateNextTaskAction(nextTask.href));
                onNotEligible();
                return false;
            }

            toast.error(responseDescription);
            return false;
        }

        if (nextTask?.title === TASK_TITLES.IDENTIFY_PARTY) {
            dispatch(updateNextTaskAction(nextTask.href));
            onIdentifyParty();
            return false;
        }

        if (nextTask?.title === TASK_TITLES.VALIDATE_ORDER) {
            navigateToOrderSummaryWithTaskUpdate(taskResponse);
            return true;
        }

        return false;
    }, [dispatch, navigateToOrderSummaryWithTaskUpdate]);

    const submitConfigurationConfirmation = useCallback(async (configurationId) => {
        try {
            dispatch(toggleLoading(true));
            const confirmationPayload = createConfirmConfigurationRequest(configurationId, relatedParty);
            const confirmationResponse = await patchTaskFlow(nextTaskToBePerformed, confirmationPayload);

            if (!confirmationResponse) return;

            handleTaskFlowResponse(
                confirmationResponse,
                () => setShouldShowUnqualifiedOfferModal(true),
                () => setShouldShowLoginModal(true),
                () => setShouldShowNotEligibleAddressModal(true)
            );
        } catch (error) {
            toast.error(error.message);
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [dispatch, relatedParty, nextTaskToBePerformed, handleTaskFlowResponse]);

    const submitPartyIdentifierAfterAuth = useCallback(async (rp) => {
        try {
            dispatch(toggleLoading(true));
            const partyIdentifierPayload = createPartyIdentifierRequest(rp);
            const partyResponse = await patchTaskFlow(nextTaskToBePerformed, partyIdentifierPayload);

            if (!partyResponse) return;

            const wasSuccessful = handleTaskFlowResponse(
                partyResponse,
                () => {
                    setShouldShowLoginModal(false);
                    setShouldShowUnqualifiedOfferModal(true);
                },
                () => {
                },
                () => {
                    setShouldShowLoginModal(false);
                    setShouldShowNotEligibleAddressModal(true);
                }
            );

            if (wasSuccessful) {
                setShouldShowLoginModal(false);
            }
        } catch (error) {
            toast.error(error.message);
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [dispatch, nextTaskToBePerformed, handleTaskFlowResponse]);

    const processShippingAndOrderCreation = useCallback(async () => {
        const shippingConfigId = await prepareShippingConfiguration();
        if (shippingConfigId) {
            await submitConfigurationConfirmation(shippingConfigId);
        }
    }, [prepareShippingConfiguration, submitConfigurationConfirmation]);

    const confirmModificationOrder = useCallback(async () => {
        dispatch(toggleLoading(true));
        try {
            await prepareShippingConfiguration();
            const result = await processOrderConfiguration(
                planId,
                relatedParty,
                configuration,
                nextTaskToBePerformed,
                navigate,
                dispatch,
                tNotification
            );
            setShouldShowConfirmModal(false);
            return result;
        } catch (error) {
            toast.error(tNotification("shipping.createFailed"));
            return null;
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [dispatch, prepareShippingConfiguration, planId, relatedParty, configuration, nextTaskToBePerformed, navigate, tNotification]);

    const handleAddToOrderButtonClick = useCallback(async () => {
        if (orderType === 'Modification') {
            setShouldShowConfirmModal(true);
        } else {
            await processShippingAndOrderCreation();
        }
    }, [orderType, processShippingAndOrderCreation]);

    const onLoginSuccess = useCallback(() => {
        setShouldTriggerPartyIdentification(true);
    }, []);

    useEffect(() => {
        if (shouldTriggerPartyIdentification && isAuthenticated && relatedParty) {
            submitPartyIdentifierAfterAuth(relatedParty);
            setShouldTriggerPartyIdentification(false);
        }
    }, [shouldTriggerPartyIdentification, isAuthenticated, relatedParty, submitPartyIdentifierAfterAuth]);

    const closeLoginModal = useCallback(() => setShouldShowLoginModal(false), []);
    const closeConfirmModal = useCallback(() => setShouldShowConfirmModal(false), []);
    const closeUnqualifiedOfferModal = useCallback(() => setShouldShowUnqualifiedOfferModal(false), []);
    const closeNotEligibleAddressModal = useCallback(() => setShouldShowNotEligibleAddressModal(false), []);

    return (
        <div className="card">
            <div className="card-body p-6">
                <h2 className="h5 mb-3">{t("common.planPreview")}</h2>
                <PriceSummaryCard
                    pricing={totalPriceWithShipping}
                    subtotalPrice={pricing}
                    shippingPrice={shippingPrice}
                    showShipping={true}
                />
                <div className="d-grid mb-2 mt-4">
                    <button
                        disabled={shippingType === SHIPPING_TYPE.HOME_DELIVERY && !isFormValid}
                        onClick={handleAddToOrderButtonClick}
                        className="btn btn-primary btn-lg"
                        type="button"
                    >
                        {t("actions.addToOrder")}
                    </button>
                </div>
            </div>

            {shouldShowLoginModal && (
                <Modal
                    show={shouldShowLoginModal}
                    body={
                        <AuthenticationTabs
                            onLoginSuccess={onLoginSuccess}
                        />
                    }
                    onClose={closeLoginModal}
                    hideFooter={true}
                    className="login-modal"
                />
            )}

            {shouldShowConfirmModal && (
                <Modal
                    show={shouldShowConfirmModal}
                    title={t("plan.confirmations.modify.proceed")}
                    body={<ModifyPlanModal hideModal={closeConfirmModal} onConfirm={confirmModificationOrder}/>}
                    onClose={closeConfirmModal}
                    hideFooter={true}
                    className="ConfirmOrderModal"
                />
            )}

            {shouldShowUnqualifiedOfferModal && (
                <Modal
                    show={shouldShowUnqualifiedOfferModal}
                    body={<OfferIncompatibilityModal/>}
                    onClose={closeUnqualifiedOfferModal}
                    hideFooter={true}
                    className="offerIncompatible"
                />
            )}

            {shouldShowNotEligibleAddressModal && (
                <Modal
                    show={shouldShowNotEligibleAddressModal}
                    body={<NotEligibleAddress hideModal={closeNotEligibleAddressModal}/>}
                    onClose={closeNotEligibleAddressModal}
                    hideFooter={true}
                />
            )}
        </div>
    );
}

PlanPreview.propTypes = {
    isShippingFormValid: PropTypes.bool
};

export default PlanPreview;