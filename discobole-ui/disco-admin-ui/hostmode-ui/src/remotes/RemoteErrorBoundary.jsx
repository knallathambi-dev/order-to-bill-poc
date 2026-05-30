// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";

class RemoteErrorBoundary extends React.Component {
    constructor(props) {
        super(props);
        this.state = {hasError: false, error: null};
        this.handleRetry = this.handleRetry.bind(this);
    }

    static getDerivedStateFromError(error) {
        return {hasError: true, error};
    }

    componentDidCatch(error, errorInfo) {
        console.warn(`[Federation] ${this.props.name} is unavailable:`, error.message);
    }

    handleRetry() {
        this.setState({hasError: false, error: null});
    }

    render() {
        if (this.state.hasError) {
            return (
                <div className="container">
                    <div className="row justify-content-center mt-5 pt-5">
                        <div className="col-12 col-md-8 col-lg-6">
                            <div className="card border-0 shadow-sm text-center p-4">
                                <div className="card-body">
                                    <div className="mb-3" style={{fontSize: "3rem"}}>⚠️</div>
                                    <h3 className="card-title fw-bold mb-3">
                                        {this.props.name} is currently unavailable
                                    </h3>
                                    <p className="text-muted mb-4">
                                        The module failed to load. This may be a temporary issue.
                                    </p>
                                    {this.props.showError && (
                                        <details className="text-start mb-4">
                                            <summary className="text-muted">Error details</summary>
                                            <pre className="bg-light rounded p-3 mt-2 text-break small">
                                                {this.state.error?.message}
                                            </pre>
                                        </details>
                                    )}
                                    <hr className="mb-4"/>
                                    <button
                                        className="btn btn-outline-dark btn-lg px-4"
                                        onClick={this.handleRetry}
                                    >
                                        Retry
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            );
        }

        return this.props.children;
    }
}

export default RemoteErrorBoundary;