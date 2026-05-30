// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import PropTypes from "prop-types";

const AccordionSection = ({title, isExpanded, onToggle, children}) => {
    const collapseId = `collapse-${title.replace(/\s+/g, "")}`;

    return (
        <div className="col-12">
            <div className="card mb-3">
                <div className="card-body">
                    <div className="accordion accordion-sm">
                        <div className="accordion-item field-item border-0">
                            <h2 className="accordion-header border-0">
                                <button
                                    type="button"
                                    className={`accordion-button fs-6 p-0 ${isExpanded ? "" : "collapsed"}`}
                                    onClick={onToggle}
                                    aria-expanded={isExpanded}
                                    aria-controls={collapseId}
                                >
                                    {title}
                                </button>
                            </h2>
                            <div
                                id={collapseId}
                                className={`accordion-collapse collapse ${isExpanded ? "show" : ""}`}
                            >
                                <div className="accordion-body p-0 pt-2">
                                    {children}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

AccordionSection.propTypes = {
    title: PropTypes.string.isRequired,
    isExpanded: PropTypes.bool.isRequired,
    onToggle: PropTypes.func.isRequired,
    children: PropTypes.node.isRequired,
};

export default AccordionSection;