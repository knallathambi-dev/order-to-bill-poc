// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {Suspense, useCallback, useEffect, useState} from "react";
import {clearRemote, getRemote} from "./RemoteRegistry";

export default function RemoteLoader({remoteKey, name}) {
    const [RemoteApp, setRemoteApp] = useState(null);
    const [loadError, setLoadError] = useState(null);

    const loadRemote = useCallback(() => {
        setLoadError(null);
        setRemoteApp(null);

        getRemote(remoteKey)
            .then((App) => {
                setRemoteApp(() => App);
            })
            .catch((err) => {
                console.warn(`[Federation] ${name} MFE is unavailable:`, err.message);
                setLoadError(err);
            });
    }, [remoteKey, name]);

    useEffect(() => {
        loadRemote();
    }, [loadRemote]);

    const handleRetry = () => {
        clearRemote(remoteKey);
        loadRemote();
    };

    if (loadError) {
        return (
            <div className="container">
                <div className="row justify-content-center mt-5 pt-5">
                    <div className="col-12 col-md-8 col-lg-6">
                        <div className="card border-0 shadow-sm text-center p-4">
                            <div className="card-body">
                                <div className="mb-3" style={{fontSize: "3rem"}}>⚠️</div>
                                <h3 className="card-title fw-bold mb-3">
                                    {name} is currently unavailable
                                </h3>
                                <p className="text-muted mb-4">
                                    The module failed to load. This may be a temporary issue.
                                </p>
                                <hr className="mb-4"/>
                                <button
                                    className="btn btn-outline-dark btn-lg px-4"
                                    onClick={handleRetry}
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

    const LoadingFallback = () => (
        <div className="d-flex flex-column align-items-center justify-content-center mt-5 pt-5">
            <div className="spinner-border text-primary mb-3" role="status">
                <span className="visually-hidden">Loading...</span>
            </div>
            <p className="text-muted">Loading {name}...</p>
        </div>
    );

    if (!RemoteApp) return <LoadingFallback/>;

    return (
        <Suspense fallback={<LoadingFallback/>}>
            <RemoteApp/>
        </Suspense>
    );
}