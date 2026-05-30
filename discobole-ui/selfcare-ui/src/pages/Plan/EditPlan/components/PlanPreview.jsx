// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useCallback, useMemo, useState} from "react";
import {useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {toast} from "react-toastify";

import ConfigurationPlanPreview from "../../shared/components/ConfigurationPlanPreview";
import processConfigurationPlanPreview from "../../shared/services/processConfigurationPlanPreview";
import {processOrderConfiguration} from "../services/editPlanService";
import {useConfiguration} from "../../shared/context/ConfigurationContext";

import {toggleLoading} from "../../../../store/actions/loadingActions";
import {setOrderType} from "../../../../store/actions/orderActions";
import {setShippingConfigItems} from "../../../../store/actions/shippingActions";
import {setItemsRequiringShipping} from "../../../../store/actions/itemsRequiringShipping";

const PlanPreview = () => {
    const navigate = useNavigate();

    const {configuration, dispatch, tNotification} = useConfiguration();

    const {planId, relatedParty, nextTaskToBePerformed} = useSelector((state) => ({
        planId: state.plan?.planId,
        relatedParty: state.auth?.relatedParty,
        nextTaskToBePerformed: state.taskManager?.nextTaskToBePerformed,
    }));

    const [isOrderConfirmationModalVisible, setIsOrderConfirmationModalVisible] = useState(false);

    const {itemsRequiringShipping, shippingConfigItems} = useMemo(
        () => processConfigurationPlanPreview(configuration),
        [configuration]
    );

    const showOrderConfirmationModal = useCallback(
        () => setIsOrderConfirmationModalVisible(true),
        []
    );

    const hideOrderConfirmationModal = useCallback(
        () => setIsOrderConfirmationModalVisible(false),
        []
    );

    const navigateToShippingPage = useCallback(() => {
        dispatch(setOrderType("Modification"));
        dispatch(setShippingConfigItems(shippingConfigItems));
        dispatch(setItemsRequiringShipping([...itemsRequiringShipping]));
        navigate("/shipping");
    }, [dispatch, navigate, shippingConfigItems, itemsRequiringShipping]);

    const handleProceedToOrderClick = useCallback(() => {
        if (itemsRequiringShipping?.length > 0) {
            navigateToShippingPage();
        } else {
            showOrderConfirmationModal();
        }
    }, [itemsRequiringShipping, navigateToShippingPage, showOrderConfirmationModal]);

    const handleOrderConfirmation = useCallback(async () => {
        dispatch(toggleLoading(true));
        try {
            const result = await processOrderConfiguration(
                planId,
                relatedParty,
                configuration,
                nextTaskToBePerformed,
                navigate,
                dispatch,
                tNotification
            );
            hideOrderConfirmationModal();
            return result;
        } catch (error) {
            toast.error(tNotification("messages.error.requestProcessingError"));
            throw error;
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [
        dispatch,
        planId,
        relatedParty,
        configuration,
        nextTaskToBePerformed,
        navigate,
        tNotification,
        hideOrderConfirmationModal,
    ]);

    return (
        <ConfigurationPlanPreview
            onProceedToOrder={handleProceedToOrderClick}
            showConfirmModal={isOrderConfirmationModalVisible}
            handleConfirmOrder={handleOrderConfirmation}
            hideConfirmModal={hideOrderConfirmationModal}
        />
    );
};

export default PlanPreview;