// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {Link} from "react-router-dom";

function Dashboard() {
    return (
        <>
            <div className="row">
                <div className="col-12">
                    <h3 className="mt-2">Dashboard</h3>
                </div>
            </div>
            <div className="row">
                <div className="col-6 mb-3">
                    <div className="card h-100">
                        <div className="card-header">
                            My Mobile
                        </div>
                        <div className="card-body">
                            <h5 className="card-title">Samsung SM-M105GDS/Samsung</h5>
                        </div>
                    </div>
                </div>
                <div className="col-6 mb-3">
                    <div className="card h-100">
                        <div className="card-header">
                            My Plan
                        </div>
                        <div className="card-body">
                            <h5 className="card-title">Plan title</h5>
                            <div className="card-text d-grid gap-1">
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <div className="me-auto">
                                        <div>Total Minutes</div>
                                    </div>
                                    <span>0</span>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <div className="me-auto">
                                        <div>Total Messages</div>
                                    </div>
                                    <span>0</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-6 mb-3">
                    <div className="card h-100">
                        <div className="card-header">
                            My Credit
                        </div>
                        <div className="card-body">
                            <div className="card-text d-grid gap-1">
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <div className="me-auto">
                                        <div>Current credit balance</div>
                                    </div>
                                    <span></span>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <div className="me-auto">
                                        <div>Credit expiry</div>
                                    </div>
                                    <span>29 February, 2024 </span>
                                </div>
                            </div>
                        </div>
                        <div className="card-footer">
                            <div className="d-flex justify-content-md-end">
                                <Link to="#" className="btn btn-primary mt-3">Recharge</Link>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-6 mb-3">
                    <div className="card h-100">
                        <div className="card-header">
                            My Internet
                        </div>
                        <div className="card-body">
                            <div className="card-text d-grid gap-1">
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <div className="me-auto">
                                        <div>GO 40-5000 SuperMBs
                                        </div>
                                    </div>
                                    <button type="button"
                                            className="btn btn-link p-0">Manage
                                    </button>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <div className="me-auto">
                                        <div> Go Extra</div>
                                    </div>
                                    <button type="button"
                                            className="btn btn-link p-0">Subscribe
                                    </button>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <div className="me-auto">
                                        <div> ABS Management</div>
                                    </div>
                                    <button type="button"
                                            className="btn btn-link p-0">Manage
                                    </button>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <div className="me-auto">
                                        <div> Orange Wi-Fi</div>
                                    </div>
                                    <button type="button"
                                            className="btn btn-link p-0">Subscribe
                                    </button>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <div className="me-auto">
                                        <div> salefny net</div>
                                    </div>
                                    <button type="button"
                                            className="btn btn-link p-0">Subscribe
                                    </button>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Dashboard;