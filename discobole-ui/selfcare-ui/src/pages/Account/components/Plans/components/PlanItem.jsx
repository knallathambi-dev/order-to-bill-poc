// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useDispatch, useSelector} from 'react-redux';
import {toast} from 'react-toastify';
import {formatToLocalDateTime} from '../../../../../utlis/helpers';
import {setCurrentPlan} from '../../../../../store/actions/planAction';
import {toggleLoading} from '../../../../../store/actions/loadingActions';
import {setIsEdit} from "../../../../../store/actions/editActions";
import {getDefaultProductConfiguration} from "../../../../Plan/shared/services/productConfigurationService";
import processConfiguration from "../../../../Plan/shared/services/processConfiguration";
import {hasOnlyContractInConfiguration} from "../../../../../utlis/utils";
import useTranslations from "../../../../../utlis/i18n/useTranslations";
import apiClient from "../../../../../services/api/apiClient";
import {setSelectedOfferName} from "../../../../../store/actions/offerActions";
import {setConfigurationId} from "../../../../../store/actions/configurationActions";

const productInventoryURL = process.env.REACT_APP_PRODUCT_INVENTORY_URL;

const PlanItem = ({plan, planDetails}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const {relatedParty} = useSelector((state) => state.auth);
    const {t, tNotification} = useTranslations();
    const [isContractActive, setIsContractActive] = useState(false);
    const [isMigrationInProgress, setIsMigrationInProgress] = useState(null);

    const checkContractActiveStatus = useCallback(async () => {
        if (!plan) {
            setIsMigrationInProgress(false);
            return false;
        }

        const productRelationships = Array.isArray(plan.productRelationship)
            ? plan.productRelationship
            : [];

        const migrationSource = productRelationships.find(relationship =>
            relationship?.relationshipType === 'migrateFrom'
        );

        let isPendingMigration = false;

        if (migrationSource) {
            const sourceProductId = migrationSource.product?.id;

            if (sourceProductId) {
                try {
                    const sourceProduct = await apiClient.get(`${productInventoryURL}/${sourceProductId}`);
                    isPendingMigration = sourceProduct?.data.operationalStatus === 'PendingMigrate';
                } catch (error) {
                    toast.error(tNotification("myPlans.fetchPlansFailed"));
                    isPendingMigration = false;
                }
            }
        }

        setIsMigrationInProgress(isPendingMigration);

        const isBasicStatusActive =
            plan.status === 'Active' &&
            plan.operationalStatus === 'Active';

        return isBasicStatusActive && !isPendingMigration;
    }, [plan]);

    useEffect(() => {
        const validateContractStatus = async () => {
            const isActive = await checkContractActiveStatus();
            setIsContractActive(isActive);
        };

        validateContractStatus();
    }, [checkContractActiveStatus]);

    const groupedProducts = useMemo(() => {
        if (planDetails === undefined) return null;

        if (!planDetails) return [];

        const validDetails = planDetails.filter(detail => detail.isCustomerVisible !== false);

        const grouped = validDetails.reduce((acc, detail) => {
            const {name: productName, status, quantity = 1} = detail;
            if (!productName || !status) return acc;

            const key = `${productName}-${status}`;
            const existing = acc.get(key);

            if (existing) {
                existing.quantity += quantity;
            } else {
                acc.set(key, {name: productName, quantity, status});
            }
            return acc;
        }, new Map());

        return Array.from(grouped.values())
            .sort((a, b) => a.name.localeCompare(b.name));
    }, [planDetails]);

    const formatOperationalStatus = useCallback((status) => {
        if (!status) return '';
        return status.replace(/([a-z])([A-Z])/g, '$1 $2');
    }, []);

    const handleModifyContract = useCallback(async () => {
        if (!isContractActive) return;

        try {
            dispatch(toggleLoading(true));

            if (!plan?.id || !plan?.name) {
                toast.error(tNotification("myPlans.modifyContractFailed"));
                return;
            }

            const data = await getDefaultProductConfiguration(
                plan.id,
                relatedParty,
                "modify",
                dispatch,
                tNotification
            );

            if (data) {
                dispatch(setConfigurationId(data.id));
            }

            const {configurationStructure} = processConfiguration(data) || {};
            if (!configurationStructure) {
                toast.error(tNotification("myPlans.modifyContractFailed"));
                return;
            }

            dispatch(setSelectedOfferName(plan.name));
            dispatch(setCurrentPlan({planId: plan.id}));

            const hasOnlyContract = hasOnlyContractInConfiguration(configurationStructure);
            dispatch(setIsEdit(!hasOnlyContract));

            navigate(hasOnlyContract ? "/terminate-plan" : "/edit-plan");
        } catch {
            toast.error(tNotification("myPlans.modifyContractFailed"));
        } finally {
            dispatch(toggleLoading(false));
        }
    }, [isContractActive, dispatch, plan, relatedParty, navigate]);

    const handleMigratePlan = useCallback(async () => {
        if (!isContractActive || !plan) return;

        const {id: planId, name: planName, contractOfferId: planProductOfferingId} = plan;
        const products = Array.isArray(planDetails) ? planDetails : [];

        const hasTerminatedParent = async (productId) => {
            try {
                dispatch(toggleLoading(true));
                const response = await apiClient.get(
                    `${productInventoryURL}?productRelationship.product.id=${productId}&productRelationship.relationshipType=bundles`
                );
                const parents = response.data || [];
                return parents.some(parent => parent.status === 'Terminated');
            } catch (error) {
                toast.error(tNotification("myPlans.fetchPlansFailed"));
            } finally {
                dispatch(toggleLoading(false));
            }
        };

        const eligibleProducts = [];

        for (const product of products) {
            if (product.isCustomerVisible === false) continue;

            if (product.status === 'Active') {
                eligibleProducts.push(product);
                continue;
            }

            if (product.status === 'Sold') {
                const hasTerminated = await hasTerminatedParent(product.id);
                if (!hasTerminated) {
                    eligibleProducts.push(product);
                }
            }
        }

        dispatch(setSelectedOfferName(planName));
        dispatch(setCurrentPlan({planId, planName, planProductOfferingId, planDetails: eligibleProducts}));
        navigate('/eligible-plans');
    }, [isContractActive, plan, planDetails, dispatch, navigate]);

    const renderPlanHeader = () => (
        <div className="plan-head mb-3">
            <div className="d-flex justify-content-between">
                <h5 className="card-title mb-0">{plan.name}</h5>
                {plan.status === 'Active' && plan.operationalStatus.toLowerCase() !== 'active' && (
                    <p className={`tag tag-sm op-status-value m-1 ${plan.operationalStatus.toLowerCase()}`}>
                        {formatOperationalStatus(plan.operationalStatus)}
                    </p>
                )}
                <p className={`tag tag-sm status-value m-0 ${plan.status.toLowerCase()}`}>
                    {plan.status}
                </p>
            </div>

            <div className="d-flex justify-content-between align-items-start mt-2">
                <div className="me-auto">{t('common.creationDate')}</div>
                <strong>{formatToLocalDateTime(plan.creationDate)}</strong>
            </div>

            {plan.terminationDate && (
                <div className="d-flex justify-content-between align-items-start mt-2">
                    <div className="me-auto">{t('common.terminationDate')}</div>
                    <strong>{formatToLocalDateTime(plan.terminationDate)}</strong>
                </div>
            )}
        </div>
    );

    const renderProductsTable = () => (
        <div className="card-text">
            <table className="table align-middle mb-0 plan-items-table">
                <tbody>
                {groupedProducts === null || isMigrationInProgress === null ? (
                    <tr>
                        <td colSpan="3" className="text-center p-4">
                            <span className="visually-hidden">Loading...</span>
                        </td>
                    </tr>
                ) : isMigrationInProgress ? (
                    <tr>
                        <td colSpan="3" className="p-4">
                            <div
                                className="d-flex flex-column align-items-center justify-content-center text-center py-3">
                                <h6 className="mb-2 text-primary">
                                    <svg className="bi me-2" width="20" height="20" fill="currentColor">
                                        <use xlinkHref="#info-circle-fill"/>
                                    </svg>
                                    {t('myPlans.migrationInProgress')}
                                </h6>
                                <p className="text-muted mb-2">{t('myPlans.migrationInProgressDesc')}</p>
                            </div>
                        </td>
                    </tr>
                ) : groupedProducts.length > 0 ? (
                    groupedProducts.map((product, index) => (
                        <tr key={`${product.name}-${product.status}-${index}`}>
                            <td style={{width: '40%'}}>{product.name}</td>
                            <td>{product.quantity > 1 ? `Qty: ${product.quantity}` : ''}</td>
                            <td className="text-end">{product.status}</td>
                        </tr>
                    ))
                ) : (
                    <tr>
                        <td colSpan="3" className="text-center">
                            {t('myPlans.noProductsFound')}
                        </td>
                    </tr>
                )}
                </tbody>
            </table>
        </div>
    );

    const renderActionButtons = () => (
        <div className="card-footer pe-0">
            <div className="d-flex justify-content-md-end gap-2 mt-3">
                <Link
                    to="#"
                    className={`btn btn-secondary ${!isContractActive ? 'disabled' : ''}`}
                    onClick={handleModifyContract}
                >
                    {t('actions.editPlan')}
                </Link>
                <Link
                    to="#"
                    className={`btn btn-primary ${!isContractActive ? 'disabled' : ''}`}
                    onClick={handleMigratePlan}
                >
                    {t('actions.changePlan')}
                </Link>
            </div>
        </div>
    );

    return (
        <div className="col-12 mb-3">
            <div className="card h-100">
                <div className="card-body">
                    {renderPlanHeader()}
                    {renderProductsTable()}
                    {renderActionButtons()}
                </div>
            </div>
        </div>
    );
};

export default PlanItem;