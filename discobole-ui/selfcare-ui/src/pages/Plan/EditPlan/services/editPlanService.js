// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useCallback} from "react";
import {toast} from "react-toastify";
import {performActionAndGetCharacteristics} from "../../shared/services/productConfigurationService";
import {cancelTaskFlowAction, updateNextTaskAction} from "../../../../store/actions/taskManagerActions";
import {setOrderType, setProductOrderId} from "../../../../store/actions/orderActions";
import {toggleLoading} from "../../../../store/actions/loadingActions";
import postProcessFlowService from "./postProcessFlowService";
import {createConfirmConfigurationRequest} from "../../../../utlis/processFlowRequestBodyUtils";
import {patchTaskFlow} from "../../../../services";
import {removeBaseUrl} from "../../../../utlis/helpers";
import {useConfiguration} from "../../shared/context/ConfigurationContext";
import {hasErrorDescription} from "../../../../utlis/utils";

export const useToggleConfigurationAction = (
    openSections,
    setOpenSections,
    configurationId,
    relatedParty,
    dispatch,
    executeOnOpen = true
) => {
    const {onConfigurationChange, tNotification} = useConfiguration();

    return useCallback(
        async (configurationItemId, actionType) => {
            const wasPreviouslyOpen = openSections[configurationItemId];
            const newSelectionState = !wasPreviouslyOpen;

            setOpenSections(prev => ({
                ...prev,
                [configurationItemId]: newSelectionState,
            }));

            const shouldSkipAction = newSelectionState && !executeOnOpen;
            if (shouldSkipAction) return;

            if (!newSelectionState && !wasPreviouslyOpen) return;

            const result = await performActionAndGetCharacteristics({
                configurationId,
                configurationItemId,
                characteristic: null,
                isSelected: newSelectionState,
                relatedParty,
                actionType,
                dispatch,
                tNotification,
                onConfigurationChange,
            });

            if (!result) {
                setOpenSections(prev => ({
                    ...prev,
                    [configurationItemId]: wasPreviouslyOpen,
                }));
                toast.error(tNotification("plan.updateCharacteristicFailed"));
            }
        },
        [openSections, setOpenSections, configurationId, relatedParty, dispatch, executeOnOpen, tNotification, onConfigurationChange]
    );
};

const resolveNextTaskUrl = (nextTasks, fallbackUrl) => {
    if (!Array.isArray(nextTasks)) return fallbackUrl;
    if (nextTasks.length > 1) return nextTasks[1].href;
    if (nextTasks.length === 1) return nextTasks[0].href;
    return fallbackUrl;
};

export const processOrderConfiguration = async (
    planId,
    relatedParty,
    configuration,
    nextTaskToBePerformed,
    navigate,
    dispatch,
    tNotification
) => {
    const postProcessResponse = await postProcessFlowService(planId, relatedParty, dispatch).catch(() => null);

    if (hasErrorDescription(postProcessResponse)) {
        toast.error(tNotification("common.processFlowCreateFailed"));
        return null;
    }

    const nextTasks = postProcessResponse.data._links.nextTaskstoBePerformed;
    const taskUrl = resolveNextTaskUrl(nextTasks, nextTaskToBePerformed);

    const requestBody = createConfirmConfigurationRequest(configuration.id, relatedParty);
    const patchResponse = await patchTaskFlow(removeBaseUrl(taskUrl), requestBody).catch(() => null);

    if (!patchResponse) {
        toast.error(tNotification("plan.processFailed"));
        return null;
    }

    const responseLinks = patchResponse.data._links.nextTaskstoBePerformed;
    const responseEntity = patchResponse.data.relatedEntity[0];

    dispatch(updateNextTaskAction(responseLinks[1].href));
    dispatch(cancelTaskFlowAction(responseLinks[0].href));
    dispatch(setProductOrderId(responseEntity.id));
    navigate('/order-summary');
    dispatch(setOrderType("Modification"));
    dispatch(toggleLoading(false));

    return patchResponse;
};