// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import {useFormik} from 'formik';
import {toast, ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import PropTypes from 'prop-types';
import OrchestrationAccordionItem from '../OrchestrationNodeAccordionItem';
import api from '../../service/OrchestrationDeliveryAPI.js';
import {
    ARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL,
    PROCESS_FLOW_ENDPOINT,
    UNARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL,
} from '../../utils/constants.js';
import {useAuth} from "../../context/AuthContext.jsx";
import AsideDetailsCard from "../AsideDetailsCard.jsx";

function FalloutIncidentsDetails({
                                     dto,
                                     relatedOrchestrationPlanNodes,
                                     nodeDetails,
                                     planDetails,
                                     onCloseSubmitSuccess,
                                 }) {
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [processFlowData, setProcessFlowData] = useState(null);
    const {canModifyFalloutIncidents} = useAuth();

    const referredType = dto?.relatedEntity?.initiator['@referredType'];

    const orchestrationDetails = {
        OrchestrationPlanNode: nodeDetails, OrchestrationPlan: planDetails,
    };

    useEffect(() => {
        const fetchProcessFlowData = async () => {
            try {
                const response = await api.get(`${PROCESS_FLOW_ENDPOINT}?relatedEntity.id=${orchestrationDetails[referredType]?.id}`);
                setProcessFlowData(response.data);
            } catch (error) {
                console.error('Error fetching process flow data:', error);
            }
        };

        if (isModalVisible && (nodeDetails?.id || planDetails?.id)) {
            fetchProcessFlowData();
        }
    }, [nodeDetails, planDetails, isModalVisible]);

    const statesOptions = ['Resolved', 'Unresolved'];

    const falloutDetails = {
        'Fallout Id': dto?.id, 'Fallout State': dto?.state,
    };

    const resolutionDetails = {
        Comment: dto?.resolution?.comment,
        State: dto?.resolution?.status === "skipped" ? "automatically resolved" : dto?.resolution?.status,
    };

    const relatedIds = {
        'Related Product Order ID': dto?.relatedEntity?.relatedProductOrder?.id,
    };

    const getNodeState = (id) => {
        return relatedOrchestrationPlanNodes.filter((node) => node.id === id).map((node) => node.state);
    };

    const getNodeName = (id) => {
        return relatedOrchestrationPlanNodes
            .filter((node) => node.id === id)
            .map((node) => node.relatedProduct[1]?.productSpecification?.name);
    };

    const getReferredtype = () => {
        return referredType === 'OrchestrationPlanNode' ? 'Node' : 'Plan';
    };

    const formik = useFormik({
        initialValues: {
            resolutionState: '', comment: '', commentError: '',
        }, validate: (values) => {
            const errors = {};
            if (values.comment.length > 250) {
                errors.commentError = 'Comment cannot exceed 250 characters';
            }
            return errors;
        }, onSubmit: (values) => {
            const closeIncidentValues = {
                characteristic: [{
                    name: 'SelectResolutionStateWithReason', valueType: 'Object', value: {
                        reason: values.comment,
                        resolutionState: values.resolutionState.toLowerCase(),
                    }, '@type': 'ObjectCharacteristic',
                },],
            };
            onCloseSubmit(closeIncidentValues);
        },
    });

    const clearForm = () => {
        formik.resetForm();
        setIsModalVisible(false);
    };

    const isFormValid = () => !formik.values.resolutionState || !formik.values.comment || formik.errors.commentError;

    const onCloseSubmit = async (closeIncidentValues) => {
        setIsSubmitting(true);

        try {
            if (!processFlowData || !Array.isArray(processFlowData)) {
                throw new Error('Process flow data is not available');
            }

            const indexOfProcessFlowData = processFlowData.findIndex((processFlowData) => processFlowData.id === dto.id);

            if (indexOfProcessFlowData === -1) {
                throw new Error('Process flow item not found for this incident');
            }

            const processFlowItem = processFlowData[indexOfProcessFlowData];
            const url = processFlowItem?._links?.nextTaskstoBePerformed?.[0]?.href;

            if (!url) {
                throw new Error('No next task URL found for this process flow');
            }

            const response = await api.patch(url, closeIncidentValues);

            console.log('Incident closed successfully:', response.data);
            toast.success('Incident closed successfully!');

            setIsModalVisible(false);
            clearForm();

            if (onCloseSubmitSuccess) {
                onCloseSubmitSuccess();
            }
        } catch (error) {
            console.error('Error closing incident:', error);
            toast.error('Failed to close the incident. Please try again.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (<>
        <ToastContainer/>
        <style>
            {`
                    body > .modal-backdrop {
                        display: none;
                    }
                    
                    body > .modal-backdrop:nth-of-type(3) {
                        display: block !important;
                    }
                `}
        </style>
        <div className="container-fluid">
            <div className="row">
                <div className="col-12">
                    <nav aria-label="breadcrumb">
                        <ol className="breadcrumb mb-0">
                            <li className="breadcrumb-item">
                                <a href="/orchestration-delivery/administration/fallout-incidents">
                                    Administration
                                </a>
                            </li>
                            <li className="breadcrumb-item active" aria-current="page">
                                Fallout Incident Details
                            </li>
                        </ol>
                    </nav>
                </div>
            </div>
        </div>
        <div className="container-fluid py-2">
            <div className="row">
                <div className="d-flex justify-content-between align-items-center mb-1">
                    <h1 className="display-3 mb-1">Fallout Incident Details</h1>
                    {dto?.state === 'Held' && canModifyFalloutIncidents && (referredType === 'OrchestrationPlanNode' ? nodeDetails?.state === 'Held' : planDetails?.state === 'Held') && (
                        <button
                            className="btn btn-outline-secondary"
                            onClick={() => setIsModalVisible(true)}
                        >
                            Close Incident
                        </button>)}
                </div>
            </div>
            <div className="row mt-0 g-3 py-1">
                <div className="col-8">
                    <div className="card mb-3">
                        <div className="card-header py-3">
                            <div className="d-flex justify-content-between align-items-center">
                                <div className="card-title mb-0">
                                    <h3 className="mb-0">Fallout
                                        Orchestration {getReferredtype()}</h3>
                                </div>
                                <Link
                                    to={`${
                                        orchestrationDetails?.OrchestrationPlan?.archived ? ARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL : UNARCHIVED_ORCHESTRATION_PLANS_Details_UI_URL}/${dto?.relatedEntity?.relatedOrchestrationPlan?.id}?id=${orchestrationDetails[referredType]?.id}`}
                                    className="btn btn-link on-black p-0 "
                                >
                                    View Full Orchestration Details{' '}
                                    <em className="icon-arrow-next ms-2"></em>
                                </Link>
                            </div>
                        </div>
                        <div className="card-body my-3">
                            <OrchestrationAccordionItem
                                getNodeState={getNodeState}
                                getNodeName={getNodeName}
                                orchestrationDetails={orchestrationDetails[referredType]}
                                referredType={getReferredtype()}
                            />
                        </div>
                    </div>
                </div>
                <div className="col-4">
                    <AsideDetailsCard title="Fallout Details" data={falloutDetails}/>
                    {dto?.state === 'Completed' && (<AsideDetailsCard title="Resolution Details"
                                                                      data={resolutionDetails}/>)}

                    <AsideDetailsCard data={relatedIds}/>
                </div>
            </div>

            {isModalVisible && (<div
                className="modal fade show"
                style={{display: 'block'}}
                tabIndex="-1"
                aria-labelledby="exampleModalLabel"
                aria-hidden="true"
            >
                <div className="modal-dialog modal-dialog-centered">
                    <form
                        onSubmit={(e) => {
                            e.preventDefault();
                            formik.handleSubmit();
                        }}
                    >
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title h5" id="exampleModalLabel">
                                    Close Incident
                                </h1>
                                <button
                                    type="button"
                                    className="btn-close"
                                    onClick={clearForm}
                                    data-bs-toggle="tooltip"
                                    data-bs-placement="bottom"
                                    title="Close"
                                >
                                    <span className="visually-hidden">Close</span>
                                </button>
                            </div>
                            <div className="modal-body mt-3">
                                <div className="mb-3">
                                    <label htmlFor="resolutionState"
                                           className="floating-label">
                                        Resolution State
                                    </label>
                                    <select
                                        className="form-select"
                                        id="resolutionState"
                                        name="resolutionState"
                                        onChange={formik.handleChange}
                                        value={formik.values.resolutionState}
                                        aria-label="Default select example"
                                        required
                                    >
                                        <option disabled value="" aria-hidden="true">
                                            Select State
                                        </option>
                                        {statesOptions.map((option) => (
                                            <option key={option + 'opt'} value={option}>
                                                {option}
                                            </option>))}
                                    </select>
                                </div>
                                <div>
                                    <label htmlFor="comment" className="floating-label">
                                        Comment
                                    </label>
                                    <textarea
                                        className="form-control"
                                        placeholder="Please write your comment here"
                                        rows="3"
                                        id="comment"
                                        name="comment"
                                        onChange={formik.handleChange}
                                        value={formik.values.comment}
                                        required
                                    ></textarea>
                                    {formik.errors.commentError && (
                                        <div className="fw-bold text-danger text-end mt-2">
                                            {formik.errors.commentError}
                                        </div>)}
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={clearForm}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={isFormValid() || isSubmitting}
                                >
                                    Submit
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>)}
        </div>
    </>);
}

export default FalloutIncidentsDetails;

FalloutIncidentsDetails.propTypes = {
    dto: PropTypes.object.isRequired,
    relatedOrchestrationPlanNodes: PropTypes.array,
    nodeDetails: PropTypes.object,
    planDetails: PropTypes.object.isRequired,
    onCloseSubmitSuccess: PropTypes.func.isRequired,
};
