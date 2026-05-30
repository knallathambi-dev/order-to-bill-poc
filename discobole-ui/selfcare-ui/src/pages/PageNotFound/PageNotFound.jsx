// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import {Link} from "react-router-dom";
import {useTitlePage} from "../../hooks";
import useTranslations from "../../utlis/i18n/useTranslations";

function PageNotFound() {
    useTitlePage("pageNotFound");

    const {t} = useTranslations();
    return (
        <main className="container py-5 text-center" role="main" aria-labelledby="notfound-title">
            <div className="row justify-content-center">
                <div className="col-12 col-md-10 col-lg-8">
                    <h1 id="notfound-title" className="display-5 fw-bold mb-2">
                        {t('pages.pageNotFound')}
                    </h1>
                    <h2 className="h5 text-muted mb-4">
                        {t('pageNotFound.message')}
                    </h2>
                    <div className="d-inline-flex gap-2">
                        <Link to="/home" className="btn btn-primary">
                            {t('actions.backToHomepage')}
                        </Link>
                    </div>
                </div>
            </div>
        </main>
    );
}

export default PageNotFound;