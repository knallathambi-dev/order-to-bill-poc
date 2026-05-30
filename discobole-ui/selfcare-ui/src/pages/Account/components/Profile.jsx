// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

function Profile() {
    return (
        <>
            <div className="row">
                <div className="col-12">
                    <h3 className="mt-2">My Profile</h3>
                </div>
            </div>
            <div className="row">
                <div className="col-12">
                    <div className="card mb-3">
                        <div className="card-header">
                            Basic Information
                        </div>
                        <div className="card-body">
                            <div className="card-text d-grid gap-1">
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <p className="me-auto">
                                        <strong>Primary Line</strong>
                                    </p>
                                    <p>0123456789</p>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <p className="me-auto">
                                        <strong>
                                            First name
                                        </strong>
                                    </p>
                                    <p>Name</p>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <p className="me-auto">
                                        <strong>
                                            Last name
                                        </strong>
                                    </p>
                                    <p>Name</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="card mb-3">
                        <div className="card-header">
                            Account Settings
                        </div>
                        <div className="card-body">
                            <div className="card-text d-grid gap-1">
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <p className="me-auto">
                                        <strong>Email</strong>
                                    </p>
                                    <p>mail@orange.com</p>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <p className="me-auto">
                                        <strong>
                                            Birthdate
                                        </strong>
                                    </p>
                                    <p>--</p>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <p className="me-auto">
                                        <strong>
                                            Profession
                                        </strong>
                                    </p>
                                    <p>--</p>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <p className="me-auto">
                                        <strong>
                                            Education
                                        </strong>
                                    </p>
                                    <p>--</p>
                                </div>
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <p className="me-auto">
                                        <strong>
                                            Address
                                        </strong>
                                    </p>
                                    <p>--</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="card mb-3">
                        <div className="card-header">
                            <div
                                className="d-flex justify-content-between align-items-center">
                                <p className="me-auto mb-0">
                                    <strong> My Lines</strong>
                                </p>
                                <button type="button"
                                        className="btn btn-link btn-inverse btn-sm p-0">Manage
                                    Lines
                                </button>
                            </div>

                        </div>
                        <div className="card-body">
                            <div className="card-text d-grid gap-1">
                                <div
                                    className="d-flex justify-content-between align-items-start">
                                    <p className="me-auto">
                                        <strong>Primary Line</strong>
                                    </p>
                                    <p>0123456789</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Profile;