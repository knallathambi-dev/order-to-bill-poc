// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useRef, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {useDispatch, useSelector} from 'react-redux';

import {Breadcrumb} from '../../../components';
import {useTitlePage} from '../../../hooks';
import {getBreadcrumbsByOrderType} from '../../../components/Breadcrumb/utils/breadcrumbHelpers';
import {getDefaultProductConfiguration, getProductConfiguration,} from '../shared/services/productConfigurationService';
import {setSelectedOfferName} from '../../../store/actions/offerActions';
import {setConfigurationId} from '../../../store/actions/configurationActions';
import {ConfigurationProvider} from '../shared/context/ConfigurationContext';
import PlanPreview from './components/PlanPreview';
import PlanSetup from './components/PlanSetup/PlanSetup';
import useTranslations from '../../../utlis/i18n/useTranslations';

import './SetUpPlan.css';

const FLOW_TITLES = {
    Acquisition: 'setUpPlan',
    Migration: 'migratePlan',
};

const ORDER_TYPE = {
    ACQUISITION: 'Acquisition',
    ACQUISITION_WITH_ELIGIBILITY: 'AcquisitionWithEligibility',
    MIGRATION: 'Migration',
};

const OPERATION_TYPE = 'add';
const DEFAULT_TITLE = 'setUpPlan';

const getTitleKey = (orderType) => FLOW_TITLES[orderType] || DEFAULT_TITLE;

const getBreadcrumbs = (orderType, titleKey, selectedOfferName) =>
    getBreadcrumbsByOrderType(orderType, titleKey, {
        selectedOfferName,
        includeOffers: orderType === ORDER_TYPE.ACQUISITION,
    });

function SetUpPlan() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const {t, tNotification} = useTranslations();

    const {orderType} = useSelector((state) => state.order);
    const {selectedOfferName, selectedOfferId} = useSelector((state) => state.offer);
    const {planName} = useSelector((state) => state.plan);
    const {relatedParty} = useSelector((state) => state.auth);
    const {configurationId} = useSelector((state) => state.configuration);

    const [isUserAtFinalStep, setIsUserAtFinalStep] = useState(false);
    const [configuration, setConfiguration] = useState(null);
    const [error, setError] = useState(null);

    const prevConfigIdRef = useRef(null);
    const prevOfferIdRef = useRef(null);
    const isLoadingRef = useRef(false);

    const titleKey = getTitleKey(orderType);
    const breadcrumbItems = getBreadcrumbs(orderType, titleKey, selectedOfferName);

    useTitlePage(titleKey, breadcrumbItems);

    useEffect(() => {
        if (isLoadingRef.current) {
            return;
        }

        const isMigration = orderType === ORDER_TYPE.MIGRATION;
        const isAcquisition = orderType === ORDER_TYPE.ACQUISITION;
        const isAcquisitionWithEligibility = orderType === ORDER_TYPE.ACQUISITION_WITH_ELIGIBILITY;

        const configIdChanged = prevConfigIdRef.current !== configurationId;
        const offerIdChanged = prevOfferIdRef.current !== selectedOfferId;

        const shouldLoadForMigration = isMigration && configurationId && configIdChanged;
        const shouldLoadForAcquisition = isAcquisition && selectedOfferId && offerIdChanged;
        const shouldLoadForAcquisitionWithEligibility =
            isAcquisitionWithEligibility && configurationId && configIdChanged;

        const shouldLoad = shouldLoadForMigration ||
            shouldLoadForAcquisition ||
            shouldLoadForAcquisitionWithEligibility;

        if (!shouldLoad) {
            return;
        }

        const loadConfiguration = async () => {
            isLoadingRef.current = true;
            setError(null);

            try {
                let data = null;

                if (isMigration || isAcquisitionWithEligibility) {
                    if (!configurationId) {
                        console.error('Configuration ID is required');
                        setError('Configuration ID is required');
                        tNotification('error', 'Configuration ID is required');
                        return;
                    }
                    data = await getProductConfiguration(
                        configurationId,
                        dispatch,
                        tNotification
                    );
                } else if (isAcquisition && selectedOfferId) {
                    data = await getDefaultProductConfiguration(
                        selectedOfferId,
                        relatedParty,
                        OPERATION_TYPE,
                        dispatch,
                        tNotification
                    );

                    if (data?.id) {
                        dispatch(setConfigurationId(data.id));
                    }
                }

                if (!data) {
                    console.error('No configuration data received');
                    setError('No configuration data received');
                    tNotification('error', 'No configuration data received');
                    return;
                }

                setConfiguration(data);
                prevConfigIdRef.current = configurationId;
                prevOfferIdRef.current = selectedOfferId;

            } catch (err) {
                console.error('Failed to load configuration:', err);
                setError(err.message || 'Failed to load configuration');
                tNotification('error', err.message || 'Failed to load configuration');
            } finally {
                isLoadingRef.current = false;
            }
        };

        loadConfiguration();
    }, [orderType, configurationId, selectedOfferId]);

    const handleConfigurationChange = useCallback((updatedConfiguration) => {
        if (updatedConfiguration) {
            setConfiguration(updatedConfiguration);
        }
    }, []);

    const handleChangeSelectedPlan = useCallback(() => {
        dispatch(setSelectedOfferName(planName));
        navigate('/eligible-plans');
    }, [dispatch, planName, navigate]);

    if (error) {
        return (
            <div className="set-up-plan-container">
                <div className="container py-4">
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                </div>
            </div>
        );
    }

    if (!configuration) {
        return null;
    }

    const isMigrationFlow = orderType === ORDER_TYPE.MIGRATION;

    return (
        <ConfigurationProvider
            configuration={configuration}
            onConfigurationChange={handleConfigurationChange}
        >
            <main className="set-up-plan-container">
                <div className="set-up-plan-header">
                    <div className="container">
                        <Breadcrumb/>
                        <div className="row">
                            <div className="col-md-12">
                                <div className="d-flex justify-content-between align-items-center">
                                    <h1 className="banner-title mb-0">{t('plan.title')}</h1>
                                    {isMigrationFlow && (
                                        <button
                                            type="button"
                                            className="btn btn-outline-secondary"
                                            onClick={handleChangeSelectedPlan}
                                            aria-label={t('plan.migrate.changeSelectedPlan')}
                                        >
                                            {t('plan.migrate.changeSelectedPlan')}
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="set-up-plan-content container py-4">
                    <div className="row">
                        <div className="col-md-7">
                            <PlanSetup setIsUserAtFinalStep={setIsUserAtFinalStep}/>
                        </div>

                        <div className="col-md-5">
                            <h5 className="plan-preview-title">{t('common.planPreview')}</h5>
                            <PlanPreview isUserAtFinalStep={isUserAtFinalStep}/>
                        </div>
                    </div>
                </div>
            </main>
        </ConfigurationProvider>
    );
}

export default SetUpPlan;