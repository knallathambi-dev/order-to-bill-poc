// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {toast} from "react-toastify";
import {Tooltip} from "react-tooltip";
import PlanItem from "./components/PlanItem";
import fetchPlans from "./services/fetchPlans";
import fetchPlansDetailsByIds from "./services/fetchPlansDetailsByIds";
import {toggleLoading} from "../../../../store/actions/loadingActions";
import {useTitlePage} from "../../../../hooks";
import useTranslations from "../../../../utlis/i18n/useTranslations";
import {ReloadButton} from "../../../../components/utils/ReloadButton";

function Plans() {
    useTitlePage("myPlans");

    const [plans, setPlans] = useState([]);
    const [plansDetails, setPlansDetails] = useState({});

    const dispatch = useDispatch();
    const {relatedParty} = useSelector((state) => state.auth);
    const {t, tNotification} = useTranslations();

    const handlePlansError = useCallback((error = "myPlans.fetchPlansFailed") => {
        setPlans([]);
        setPlansDetails({});
        toast.error(tNotification(error));
    }, []);

    const loadPlansDetails = useCallback(async (plansData) => {
        const planIds = plansData.map(plan => plan.id);

        const details = await fetchPlansDetailsByIds(planIds, tNotification);

        if (details) {
            setPlansDetails(details);
        } else {
            setPlansDetails({});
            toast.error(tNotification("myPlans.fetchPlanDetailsFailed"));
        }
    }, []);

    const loadPlans = useCallback(async () => {
        if (!relatedParty.id) return;

        dispatch(toggleLoading(true));
        try {
            const plansData = await fetchPlans(relatedParty.id, tNotification);

            if (!Array.isArray(plansData) || plansData.length === 0) {
                handlePlansError();
                return;
            }

            setPlans(plansData);
            await loadPlansDetails(plansData);

        } catch (error) {
            handlePlansError();
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [dispatch, relatedParty.id, handlePlansError, loadPlansDetails]);

    useEffect(() => {
        loadPlans();
    }, [loadPlans]);

    return (
        <>
            <Tooltip id="tooltip" className="tooltip" place="top"/>

            <div className="row">
                <div className="col-12">
                    <div className="d-flex justify-content-between align-items-center mt-2 mb-3">
                        <h3 className="mb-0">{t('pages.myPlans')}</h3>
                        <ReloadButton onClick={loadPlans}/>
                    </div>
                </div>
            </div>

            <div className="row">
                {plans.map((plan) => (
                    <PlanItem
                        key={plan.id}
                        plan={plan}
                        planDetails={plansDetails[plan.id]}
                    />
                ))}
            </div>
        </>
    );
}

export default Plans;