// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useMemo, useState} from "react";
import {useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {toast} from 'react-toastify';

import processConfigurationPlanPreview from "../../shared/services/processConfigurationPlanPreview";
import ConfigurationPlanPreview from "../../shared/components/ConfigurationPlanPreview";
import {useConfiguration} from "../../shared/context/ConfigurationContext";

import {setShippingConfigItems} from "../../../../store/actions/shippingActions";
import {setItemsRequiringShipping} from "../../../../store/actions/itemsRequiringShipping";
import {cancelTaskFlowAction, updateNextTaskAction} from "../../../../store/actions/taskManagerActions";
import {setProductOrderId} from "../../../../store/actions/orderActions";
import {toggleLoading} from "../../../../store/actions/loadingActions";
import {
    createConfirmConfigurationRequest,
    createPartyIdentifierRequest
} from "../../../../utlis/processFlowRequestBodyUtils";
import {patchTaskFlow} from "../../../../services";
import {hasErrorDescription} from "../../../../utlis/utils";
import {UNQUALIFIED_OFFER_MESSAGE} from "../../../../utlis/constants";

const VALIDATE_ORDER_TASK = 'OrderCapture.validateOrder';
const IDENTIFY_PARTY_TASK = 'OrderCapture.identifyParty';

const PlanPreview = ({isUserAtFinalStep}) => {
    const navigate = useNavigate();

    const {configuration, relatedParty, dispatch} = useConfiguration();

    const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
    const [isUnqualifiedOfferModalOpen, setIsUnqualifiedOfferModalOpen] = useState(false);
    const [identifyPartyNextHref, setIdentifyPartyNextHref] = useState(null);

    const {nextTaskToBePerformed} = useSelector((state) => state.taskManager);
    const {orderType} = useSelector((state) => state.order);

    const {
        itemsRequiringShipping: configItemsRequiringShipping,
        shippingConfigItems: configItemsWithShippingDetails
    } = useMemo(
        () => processConfigurationPlanPreview(configuration),
        [configuration]
    );

    const requiresShippingAddress = configItemsRequiringShipping?.length > 0;
    const isAcquisitionOrMigrationOrder = orderType?.includes('Acquisition') || orderType === 'Migration';
    const shouldDisableProceedButton = isAcquisitionOrMigrationOrder ? !isUserAtFinalStep : false;

    const submitConfigurationConfirmation = useCallback(async (configurationId) => {
        const confirmationPayload = createConfirmConfigurationRequest(configurationId, relatedParty);
        return await patchTaskFlow(nextTaskToBePerformed, confirmationPayload);
    }, [nextTaskToBePerformed, relatedParty]);

    const submitPartyIdentifierAtHref = useCallback(async (href) => {
        const partyIdentifierPayload = createPartyIdentifierRequest(relatedParty);
        return await patchTaskFlow(href, partyIdentifierPayload);
    }, [relatedParty]);

    const navigateToOrderSummary = useCallback((taskFlowResponse) => {
        const availableNextTasks = taskFlowResponse.data._links.nextTaskstoBePerformed;
        const createdOrderEntity = taskFlowResponse.data.relatedEntity[0];

        dispatch(updateNextTaskAction(availableNextTasks[1].href));
        dispatch(cancelTaskFlowAction(availableNextTasks[0].href));
        dispatch(setProductOrderId(createdOrderEntity.id));
        navigate('/order-summary');
    }, [dispatch, navigate]);

    const processConfigurationTaskResponse = useCallback(async (taskResponse) => {
        const responseDescription = taskResponse?.data?.description;
        const nextTasksList = taskResponse?.data?._links?.nextTaskstoBePerformed || [];

        if (responseDescription === UNQUALIFIED_OFFER_MESSAGE) {
            setIsUnqualifiedOfferModalOpen(true);
            return;
        }

        if (hasErrorDescription(taskResponse)) {
            toast.error(responseDescription);
            return;
        }

        if (nextTasksList[1]?.title === VALIDATE_ORDER_TASK) {
            navigateToOrderSummary(taskResponse);
            return;
        }

        if (nextTasksList[1]?.title === IDENTIFY_PARTY_TASK) {
            setIdentifyPartyNextHref(nextTasksList[1]?.href || null);
            setIsLoginModalOpen(true);
        }
    }, [navigateToOrderSummary]);

    const executeConfigurationWorkflow = useCallback(async (configurationId) => {
        try {
            dispatch(toggleLoading(true));

            const confirmationResponse = await submitConfigurationConfirmation(configurationId);

            if (confirmationResponse) {
                await processConfigurationTaskResponse(confirmationResponse);
            }
        } catch (error) {
            toast.error(
                error?.serverError || "Unable to process your request. Please try again later."
            );
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [dispatch, submitConfigurationConfirmation, processConfigurationTaskResponse]);

    const redirectToShippingPage = useCallback(() => {
        dispatch(setShippingConfigItems(configItemsWithShippingDetails));
        dispatch(setItemsRequiringShipping([...configItemsRequiringShipping]));
        navigate("/shipping");
    }, [dispatch, navigate, configItemsWithShippingDetails, configItemsRequiringShipping]);

    const processOrderWithoutShipping = useCallback(async () => {
        dispatch(setItemsRequiringShipping([]));
        await executeConfigurationWorkflow(configuration.id);
    }, [dispatch, executeConfigurationWorkflow, configuration.id]);

    const handleProceedToOrder = useCallback(async () => {
        if (requiresShippingAddress) {
            redirectToShippingPage();
        } else {
            await processOrderWithoutShipping();
        }
    }, [requiresShippingAddress, redirectToShippingPage, processOrderWithoutShipping]);

    const handleLoginSuccess = useCallback(async () => {
        try {
            if (!identifyPartyNextHref) return;
            dispatch(toggleLoading(true));

            const verificationResponse = await submitPartyIdentifierAtHref(identifyPartyNextHref);

            if (verificationResponse?.data?.description === UNQUALIFIED_OFFER_MESSAGE) {
                setIsLoginModalOpen(false);
                setIsUnqualifiedOfferModalOpen(true);
                return;
            }

            if (hasErrorDescription(verificationResponse)) {
                toast.error(verificationResponse?.data?.description);
                setIsLoginModalOpen(false);
                return;
            }

            if (verificationResponse) {
                navigateToOrderSummary(verificationResponse);
            }
        } catch (e) {
            toast.error(
                e?.serverError || "Unable to process your request. Please try again later."
            );
        } finally {
            setIsLoginModalOpen(false);
            dispatch(toggleLoading(false));
            setIdentifyPartyNextHref(null);
        }
    }, [dispatch, identifyPartyNextHref, navigateToOrderSummary, submitPartyIdentifierAtHref]);

    const closeLoginModal = useCallback(() => setIsLoginModalOpen(false), []);
    const closeUnqualifiedOfferModal = useCallback(() => setIsUnqualifiedOfferModalOpen(false), []);

    return (
        <ConfigurationPlanPreview
            shouldDisableProceedButton={shouldDisableProceedButton}
            onProceedToOrder={handleProceedToOrder}
            showLoginModal={isLoginModalOpen}
            hideLoginModal={closeLoginModal}
            onLoginSuccess={handleLoginSuccess}
            showUnqualifiedModal={isUnqualifiedOfferModalOpen}
            hideUnqualifiedModal={closeUnqualifiedOfferModal}
            shouldDisplayContract={true}
        />
    );
};

export default PlanPreview;