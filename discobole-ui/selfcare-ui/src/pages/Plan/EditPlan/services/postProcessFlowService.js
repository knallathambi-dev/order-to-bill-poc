// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {toggleLoading} from "../../../../store/actions/loadingActions";
import {updateNextTaskAction} from "../../../../store/actions/taskManagerActions";
import {toast} from "react-toastify";
import {createPostProcessFlowRequest} from "../../../../utlis/processFlowRequestBodyUtils";
import {postProcessFlow} from "../../../../services";
import {hasErrorDescription} from "../../../../utlis/utils";

const orderCaptureURL = process.env.REACT_APP_ORDER_CAPTURE_URL;

const postProcessFlowService = async (productId, relatedParty, dispatch, tNotification) => {
    dispatch(toggleLoading(true));
    try {
        const requestBody = createPostProcessFlowRequest(productId, false, relatedParty);
        const response = await postProcessFlow(orderCaptureURL, requestBody);
        if (response) {
            if (hasErrorDescription(response)) {
                toast.error(tNotification("common.processFlowCreateFailed"));
                return null;
            }

            const nextTasks = response.data._links.nextTaskstoBePerformed;
            if (nextTasks && nextTasks.length > 1) {
                dispatch(updateNextTaskAction(nextTasks[1].href));
            } else {
                toast.error(tNotification("common.processFlowCreateFailed"));
                return null;
            }
            return response;
        } else {
            toast.error(tNotification("common.processFlowCreateFailed"));
            return null;
        }
    } catch (error) {
        toast.error(tNotification("common.processFlowCreateFailed"));
        return null;
    } finally {
        dispatch(toggleLoading(false));
    }
};

export default postProcessFlowService;