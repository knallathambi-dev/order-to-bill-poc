// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useCallback, useState} from "react";
import PropTypes from "prop-types";
import {Link, useLocation} from "react-router-dom";
import {ReloadButton} from "@discobole/common-ui";

import {ReactComponent as ActiveSVG} from "../../assets/images/statuses/active-sm.svg";
import {ReactComponent as CreatedSVG} from "../../assets/images/statuses/created-sm.svg";
import {ReactComponent as SoldSVG} from "../../assets/images/statuses/sold-sm.svg";
import {ReactComponent as AbortedSVG} from "../../assets/images/statuses/aborted-sm.svg";
import {ReactComponent as CancelledSVG} from "../../assets/images/statuses/cancelled-sm.svg";
import {ReactComponent as TerminatedSVG} from "../../assets/images/statuses/terminated-sm.svg";

import ProductNodeGraph from "./ProductNodeGraph/ProductNodeGraph";
import ProductsBasicDetails from "./ProductsBasicDetails";

const DETAIL_TAB = "Details";
const GRAPH_TAB = "Full Hierarchy";

export const TYPES = [
    {name: "Created", icon: CreatedSVG},
    {name: "Cancelled", icon: CancelledSVG},
    {name: "Aborted", icon: AbortedSVG},
    {name: "Terminated", icon: TerminatedSVG},
    {name: "Active", icon: ActiveSVG},
    {name: "Sold", icon: SoldSVG},
];

const TABS = [
    {key: DETAIL_TAB, label: "Details"},
    {key: GRAPH_TAB, label: "Full Hierarchy"},
];

/**
 * Read the active tab from the URL query string.
 * Returns GRAPH_TAB if ?tab=Full Hierarchy, otherwise DETAIL_TAB.
 */
const getTabFromURL = (search) => {
    const params = new URLSearchParams(search);
    const tab = params.get("tab");
    return tab === GRAPH_TAB ? GRAPH_TAB : DETAIL_TAB;
};

function ProductsDetails({
                             dto,
                             productHierarchy,
                             onProdParentContract,
                             refreshData,
                             parentContractHierarchy,
                         }) {
    const location = useLocation();

    const [activeTab, setActiveTab] = useState(() => getTabFromURL(location.search));

    const handleTabClick = useCallback(
        (tab) => {
            setActiveTab(tab);
            const url = new URL(window.location.href);
            url.searchParams.set("tab", tab);
            window.history.pushState({}, "", url);
        },
        []
    );

    return (
        <div>
            <div className="container-fluid">
                <div className="row">
                    <div className="col-12">
                        <nav aria-label="breadcrumb">
                            <ol className="breadcrumb mb-0">
                                <li className="breadcrumb-item">
                                    <Link to="/product-inventory/monitoring/products/">
                                        Monitoring
                                    </Link>
                                </li>
                                <li className="breadcrumb-item active" aria-current="page">
                                    Product Details
                                </li>
                            </ol>
                        </nav>
                    </div>
                </div>
            </div>


            <div className="row">
                <div className="col-12 d-flex justify-content-between align-items-center">
                    <h1 className="display-3">Product Details</h1>
                    <ReloadButton onClick={refreshData}/>
                </div>
            </div>

            <ul role="tablist" className="nav nav-tabs">
                {TABS.map(({key, label}) => (
                    <li key={key} className="nav-item" role="presentation">
                        <button
                            type="button"
                            className={`nav-link ${activeTab === key ? "active" : ""}`}
                            role="tab"
                            aria-selected={activeTab === key}
                            onClick={() => handleTabClick(key)}
                        >
                            {label}
                        </button>
                    </li>
                ))}
            </ul>

            <div className="tab-content p-0 mt-2 border-0">
                {activeTab === DETAIL_TAB && (
                    <div role="tabpanel">
                        <ProductsBasicDetails product={dto}/>
                    </div>
                )}

                {activeTab === GRAPH_TAB && (
                    <div role="tabpanel">
                        <div className="card-body">
                            <ProductNodeGraph
                                onProdparentContract={onProdParentContract}
                                product={dto}
                                productHierarchy={productHierarchy}
                                parentContractHierarchy={parentContractHierarchy}
                                setActiveTab={handleTabClick}
                            />
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

ProductsDetails.propTypes = {
    dto: PropTypes.object.isRequired,
    productHierarchy: PropTypes.array.isRequired,
    onProdParentContract: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired,
    parentContractHierarchy: PropTypes.oneOfType([
        PropTypes.array,
        PropTypes.object,
    ]),
};

export default ProductsDetails;