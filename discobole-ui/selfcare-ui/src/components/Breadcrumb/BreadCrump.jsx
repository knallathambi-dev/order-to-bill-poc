// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from 'react';
import {useSelector} from 'react-redux';
import {BreadcrumbItem} from './components';

const Breadcrumb = () => {
    const history = useSelector((state) => state.breadcrumb.history);

    if (!history || history.length <= 1) {
        return null;
    }

    return (
        <nav aria-label="breadcrumb">
            <ol className="breadcrumb">
                {history.map((title, index) => (
                    <BreadcrumbItem
                        key={`${title}-${index}`}
                        label={title}
                        isLast={index === history.length - 1}
                    />
                ))}
            </ol>
        </nav>
    );
};

export default Breadcrumb;