// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {toast} from "react-toastify";
import {toggleLoading} from "../../../../store/actions/loadingActions";
import {getDefaultProductConfiguration} from "../../shared/services/productConfigurationService";
import {
    createConfirmConfigurationRequest,
    createPostProcessFlowRequest
} from "../../../../utlis/processFlowRequestBodyUtils";
import {patchTaskFlow, postProcessFlow} from "../../../../services";
import {removeBaseUrl} from "../../../../utlis/helpers";
import {hasErrorDescription} from "../../../../utlis/utils";

const orderCaptureURL = process.env.REACT_APP_ORDER_CAPTURE_URL;

export const terminatePlanService = async (
    planId,
    relatedParty,
    dispatch,
    tNotification
) => {
    if (!planId) {
        toast.error(tNotification("plan.terminateFailed"));
        return null;
    }

    dispatch(toggleLoading(true));

    const resultConfiguration = await getDefaultProductConfiguration(
        planId,
        relatedParty,
        "terminate",
        dispatch,
        tNotification
    );

    if (!resultConfiguration?.id) {
        toast.error(tNotification("plan.terminateFailed"));
        dispatch(toggleLoading(false));
        return null;
    }

    const requestBody = createPostProcessFlowRequest(planId, false, relatedParty);
    const response = await postProcessFlow(orderCaptureURL, requestBody).catch(() => null);

    if (hasErrorDescription(response)) {
        toast.error(tNotification("common.processFlowCreateFailed"));
        return null;
    }

    const status = response?.status;
    const nextTasks = response?.data?._links?.nextTaskstoBePerformed ?? [];

    if (status !== 201 || nextTasks.length === 0) {
        toast.error(tNotification("common.processFlowCreateFailed"));
        dispatch(toggleLoading(false));
        return null;
    }

    const confirmTaskHref =
        nextTasks.find(t => t?.title === "OrderCapture.confirmConfiguration")?.href ??
        nextTasks.find(t => t?.title?.toLowerCase?.().includes("confirm"))?.href ??
        nextTasks?.[1]?.href ??
        nextTasks?.[0]?.href;

    if (!confirmTaskHref) {
        toast.error(tNotification("plan.terminateFailed"));
        dispatch(toggleLoading(false));
        return null;
    }

    const requestBodyPatch = createConfirmConfigurationRequest(resultConfiguration.id, relatedParty);
    const responsePatchRequest = await patchTaskFlow(removeBaseUrl(confirmTaskHref), requestBodyPatch).catch(() => null);

    const patchNextTasks = responsePatchRequest?.data?._links?.nextTaskstoBePerformed ?? [];
    const productOrderId = responsePatchRequest?.data?.relatedEntity?.[0]?.id;

    if (patchNextTasks.length === 0 || !productOrderId) {
        toast.error(tNotification("plan.terminateFailed"));
        dispatch(toggleLoading(false));
        return null;
    }

    const validateTaskHref = patchNextTasks.find(t => t?.title === "OrderCapture.validateOrder")?.href;
    const cancelTaskHref =
        (patchNextTasks.find(t => t?.title?.toLowerCase?.().includes("cancel")) ?? patchNextTasks?.[0])?.href || null;

    if (!validateTaskHref) {
        toast.error(tNotification("plan.terminateFailed"));
        dispatch(toggleLoading(false));
        return null;
    }

    dispatch(toggleLoading(false));

    return {
        nextTaskUrl: validateTaskHref,
        cancelTaskUrl: cancelTaskHref,
        productOrderId
    };
};