// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {toggleLoading} from "../../../store/actions/loadingActions";
import {updateNextTaskAction} from "../../../store/actions/taskManagerActions";
import {toast} from "react-toastify";
import {createPostProcessFlowRequest} from "../../../utlis/processFlowRequestBodyUtils";
import {postProcessFlow} from "../../../services";
import {setDescription, setSelectedOfferId, setSelectedOfferName} from "../../../store/actions/offerActions";
import {hasErrorDescription} from "../../../utlis/utils";

const orderCaptureURL = process.env.REACT_APP_ORDER_CAPTURE_URL;

const postProcessFlowService = async (offerId, offerName, description, relatedParty, dispatch, tNotification) => {
    dispatch(toggleLoading(true));

    try {
        const requestBody = createPostProcessFlowRequest(offerId, true, relatedParty);
        const response = await postProcessFlow(orderCaptureURL, requestBody);

        if (response && response.data) {
            if (hasErrorDescription(response)) {
                toast.error(tNotification("common.processFlowCreateFailed"));
                return null;
            }

            const nextTaskHref = response.data?._links?.nextTaskstoBePerformed?.[1]?.href;
            if (nextTaskHref) {
                dispatch(updateNextTaskAction(nextTaskHref));
            }
            dispatch(setSelectedOfferName(offerName));
            dispatch(setSelectedOfferId(offerId));
            dispatch(setDescription(description));
            return response;
        }

        return null;
    } catch {
        toast.error(tNotification("common.processFlowCreateFailed"));
        return null;
    } finally {
        dispatch(toggleLoading(false));
    }
};

export default postProcessFlowService;