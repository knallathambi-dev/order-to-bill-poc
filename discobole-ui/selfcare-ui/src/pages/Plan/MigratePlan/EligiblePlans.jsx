// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useMemo, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {toast} from "react-toastify";
import Carousel from "react-multi-carousel";
import "react-multi-carousel/lib/styles.css";

import {Breadcrumb} from "../../../components";
import EligiblePlanSkeletonCard from "./compoents/EligiblePlanSkeletonCard";

import apiClient from "../../../services/api/apiClient";
import {fetchDefaultProductConfigForMigration} from "../shared/services/productConfigurationService";
import postProcessFlowService from "../EditPlan/services/postProcessFlowService";
import {
    extractAllEligibleOfferings,
    fetchMigrationEligibility,
    fetchOfferingsFromCatalog,
} from "./services/eligiblePlansService";

import {toggleLoading} from "../../../store/actions/loadingActions";
import {setOrderType} from "../../../store/actions/orderActions";
import {setSelectedOfferName} from "../../../store/actions/offerActions";

import {hasCharges, isFiberOrConvergentOffer, renderPrices} from "../../../utlis/utils";
import {ADDRESS_CHARACTERISTIC_TYPE, ATOMIC_TYPE, BUNDLE_TYPE} from "../../../utlis/constants";
import {calculatePricingForProducts} from "./services/utils";
import {responsive} from "../../../utlis/helpers";
import useTranslations from "../../../utlis/i18n/useTranslations";
import {useTitlePage} from "../../../hooks";

import "./EligiblePlans.css";

const CATALOG_BASE_URL = process.env.REACT_APP_PRODUCT_CATALOG_URL;
const SKELETON_CARD_COUNT = 4;
const MIN_SEARCH_LENGTH = 3;

const hasAddressCharacteristic = (planDetail) =>
    planDetail?.productCharacteristic?.some(
        (char) => char["@type"] === ADDRESS_CHARACTERISTIC_TYPE
    );

const shouldNavigateToEligibility = (sourcePlan, targetPlan, planDetails) => {
    const sourceHasFiberOrConvergent = isFiberOrConvergentOffer(sourcePlan || {});
    const targetHasFiberOrConvergent = isFiberOrConvergentOffer(targetPlan);
    const sourceHasAddress = planDetails?.some(hasAddressCharacteristic);

    const isNewFiberOrConvergent = targetHasFiberOrConvergent && !sourceHasFiberOrConvergent;
    const bothFiberButNoAddress = targetHasFiberOrConvergent && sourceHasFiberOrConvergent && !sourceHasAddress;

    return isNewFiberOrConvergent || bothFiberButNoAddress;
};

const filterPlansBySearchTerm = (plans, searchTerm) => {
    const query = (searchTerm || "").trim().toLowerCase();

    if (query.length < MIN_SEARCH_LENGTH) {
        return plans;
    }

    return plans.filter((plan) =>
        plan.name?.toLowerCase().includes(query) ||
        plan.description?.toLowerCase().includes(query)
    );
};

const sortByName = (items) =>
    [...items].sort((a, b) =>
        (a?.name || a?.productOffering?.name || "").localeCompare(
            b?.name || b?.productOffering?.name || ""
        )
    );

const EligiblePlans = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const {t, tNotification} = useTranslations();

    const {selectedOfferName} = useSelector((state) => state.offer);
    const {planId, planProductOfferingId, planDetails} = useSelector((state) => state.plan);
    const {relatedParty} = useSelector((state) => state.auth);

    const [eligiblePlans, setEligiblePlans] = useState([]);
    const [selectedPlan, setSelectedPlan] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState("");
    const [currentPlanOffering, setCurrentPlanOffering] = useState(null);

    const hasLoadedRef = useRef(false);

    useTitlePage("eligiblePlans", [selectedOfferName, "eligiblePlans"]);

    const currentPlanPricing = useMemo(
        () => calculatePricingForProducts(planDetails),
        [planDetails]
    );

    const filteredPlans = useMemo(
        () => filterPlansBySearchTerm(eligiblePlans, searchTerm),
        [eligiblePlans, searchTerm]
    );

    const resolveAtomicItems = useCallback(async (offerings) => {
        if (!Array.isArray(offerings) || offerings.length === 0) {
            return [];
        }

        const resolveOffering = async (offering) => {
            try {
                const {data} = await apiClient.get(`${CATALOG_BASE_URL}/${offering.id}`);

                if (offering["@type"] === ATOMIC_TYPE) {
                    return data?.isVisible !== false ? [data] : [];
                }

                if (offering["@type"] === BUNDLE_TYPE && Array.isArray(data?.bundledProductOffering)) {
                    return resolveAtomicItems(data.bundledProductOffering);
                }

                return [];
            } catch {
                return [];
            }
        };

        const results = await Promise.all(offerings.map(resolveOffering));
        return results.flat();
    }, []);

    const loadEligiblePlans = useCallback(async () => {
        if (!selectedOfferName) {
            setEligiblePlans([]);
            setCurrentPlanOffering(null);
            setIsLoading(false);
            return;
        }

        setIsLoading(true);

        try {
            const {data: currentPlanData} = await apiClient.get(
                `${CATALOG_BASE_URL}/${planProductOfferingId}`
            );
            setCurrentPlanOffering(currentPlanData);

            const hasValidPlanId = planId && String(planId).trim() !== '';
            const hasValidRelatedParty =
                relatedParty?.id &&
                relatedParty?.name &&
                relatedParty?.role;

            if (!hasValidPlanId || !hasValidRelatedParty) {
                setEligiblePlans([]);
                return;
            }

            const poqResponse = await fetchMigrationEligibility(planId, relatedParty);
            const offeringIds = extractAllEligibleOfferings(poqResponse);

            const offerings = offeringIds.length > 0
                ? await fetchOfferingsFromCatalog(offeringIds)
                : [];

            setEligiblePlans(offerings);

        } catch (error) {
            console.error('Failed to load eligible plans:', error);
            toast.error(tNotification("eligiblePlans.fetchEligiblePlansFailed"));
            setEligiblePlans([]);
            setCurrentPlanOffering(null);
        } finally {
            setIsLoading(false);
        }
    }, [selectedOfferName, planProductOfferingId, planId, relatedParty, tNotification]);

    const handlePlanSelect = useCallback(async (plan) => {
        if (!plan) return;

        try {
            dispatch(toggleLoading(true));

            const fullOffering = plan.bundledProductOffering
                ? plan
                : await apiClient.get(`${CATALOG_BASE_URL}/${plan.id}`).then((res) => res.data);

            const atomicItems = await resolveAtomicItems(
                fullOffering.bundledProductOffering || []
            );

            setSelectedPlan({
                ...fullOffering,
                atomicItems: atomicItems ?? [],
            });

        } catch {
            toast.error(tNotification("eligiblePlans.loadPlanDetailsFailed"));
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [dispatch, resolveAtomicItems, tNotification]);

    const handleProceedWithMigration = useCallback(async () => {
        if (!selectedPlan?.id) return;

        try {
            dispatch(toggleLoading(true));

            await Promise.all([
                postProcessFlowService(planId, relatedParty, dispatch),
                fetchDefaultProductConfigForMigration(planId, selectedPlan.id, relatedParty, dispatch),
            ]);

            const goToEligibility = shouldNavigateToEligibility(
                currentPlanOffering,
                selectedPlan,
                planDetails
            );

            dispatch(setOrderType("Migration"));
            dispatch(setSelectedOfferName(selectedPlan.name));
            navigate(goToEligibility ? "/eligibility" : "/migrate-plan");

        } catch {
            toast.error(tNotification("eligiblePlans.proceedChangeFailed"));
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [
        dispatch,
        navigate,
        planId,
        planDetails,
        relatedParty,
        selectedPlan,
        currentPlanOffering,
        tNotification,
    ]);

    const clearSearch = useCallback(() => setSearchTerm(""), []);

    useEffect(() => {
        if (!hasLoadedRef.current) {
            loadEligiblePlans();
            hasLoadedRef.current = true;
        }
    }, []);

    const renderPlanCard = useCallback((plan) => {
        const isSelected = selectedPlan?.id === plan.id;

        return (
            <div
                key={plan.id || plan.name}
                className="plan-slide px-2"
                onClick={() => handlePlanSelect(plan)}
            >
                <div
                    className={`card plan-card card-with-label position-relative ${isSelected ? "is-selected" : ""}`}
                    style={{height: 300}}
                >
                    {isSelected && (
                        <em className="benefit-icon icon-done_modifier selected-plan-icon"/>
                    )}
                    <div className="card-body pt-4 plan-body">
                        <h5 className="card-title">{plan.name}</h5>
                        <p className="card-text plan-text">{plan.description}</p>
                    </div>
                </div>
            </div>
        );
    }, [selectedPlan?.id, handlePlanSelect]);

    const renderBenefitItem = useCallback((item) => (
        <li key={item.name || item.id} className="list-group-item benefit-item">
            <em className="benefit-icon icon-done_modifier"/>
            {item.name || item?.productOffering?.name}
        </li>
    ), []);

    const renderSkeletonCards = () =>
        Array.from({length: SKELETON_CARD_COUNT}).map((_, index) => (
            <div className="plan-slide px-2" key={`skeleton-${index}`}>
                <EligiblePlanSkeletonCard/>
            </div>
        ));

    return (
        <main>
            <div className="container">
                <div className="row">
                    <div className="col-md-12">
                        <Breadcrumb/>
                    </div>
                </div>

                <div className="row align-items-center">
                    <div className="col-md-6">
                        <h1 className="banner-title">{t("pages.eligiblePlans")}</h1>
                    </div>

                    <div className="col-md-6">
                        <div className="d-flex justify-content-md-end justify-content-start">
                            <div className="search-container">
                                <input
                                    type="text"
                                    className="form-control search-input"
                                    placeholder={t("forms.placeholders.searchPlans")}
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                                {searchTerm && (
                                    <button
                                        type="button"
                                        className="btn btn-icon dismiss-btn"
                                        onClick={clearSearch}
                                        aria-label={t("actions.clearSearch")}
                                        title={t("actions.clearSearch")}
                                    >
                                        ✕
                                    </button>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="container py-4">
                {!isLoading && filteredPlans.length === 0 ? (
                    <div className="text-center py-5">
                        <h4>{t("eligiblePlans.noMatchesFound", "No matches found for this search.")}</h4>
                    </div>
                ) : (
                    <Carousel responsive={responsive} autoPlay={false} infinite>
                        {isLoading
                            ? renderSkeletonCards()
                            : filteredPlans.map(renderPlanCard)
                        }
                    </Carousel>
                )}

                <hr className="my-4"/>

                <div className="row">
                    {(planDetails || selectedOfferName) && (
                        <div className="col-md-4">
                            <div className="card h-100">
                                <div className="card-body">
                                    <h5 className="card-title">{t("plan.migrate.currentPlan")}</h5>
                                    <p className="fs-5 mb-3">{selectedOfferName}</p>

                                    {hasCharges(currentPlanPricing.current) && (
                                        <p className="fs-3 text-primary fw-bold mb-0">
                                            {renderPrices(currentPlanPricing.current, currentPlanPricing.currency)}
                                        </p>
                                    )}

                                    {planDetails?.length > 0 && (
                                        <>
                                            <hr/>
                                            <ul className="list-group list-group-flush plan-benefits-wrapper">
                                                <h6 className="mb-2">{t("plan.migrate.yourPlanBenefits")}</h6>
                                                {sortByName(planDetails).map(renderBenefitItem)}
                                            </ul>
                                        </>
                                    )}
                                </div>
                            </div>
                        </div>
                    )}

                    <div className="col-md-4">
                        <div className="card border-secondary h-100">
                            <div className="card-body">
                                <h5 className="card-title">{t("plan.migrate.planSelection")}</h5>
                                <p className="fs-5 mb-3">
                                    {selectedPlan?.name || (
                                        <span className="text-muted">
                                            {t("plan.migrate.selectDescription")}
                                        </span>
                                    )}
                                </p>

                                {selectedPlan?.atomicItems?.length > 0 && (
                                    <>
                                        <hr/>
                                        <ul className="list-group list-group-flush plan-benefits-wrapper">
                                            <h6 className="mb-2">{t("plan.migrate.yourPlanBenefits")}</h6>
                                            {sortByName(selectedPlan.atomicItems).map(renderBenefitItem)}
                                        </ul>
                                    </>
                                )}
                            </div>

                            <div className="card-footer">
                                <div className="d-grid">
                                    <button
                                        onClick={handleProceedWithMigration}
                                        className="btn btn-primary"
                                        disabled={!selectedPlan}
                                    >
                                        {t("actions.proceedToChange")}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    );
};

export default EligiblePlans;