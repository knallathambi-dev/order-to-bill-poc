// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React from "react";
import ContentLoader from "react-content-loader";

const OrdersSkeleton = () => (
    <ContentLoader
        speed={2}
        width={1000}
        height={520}
        viewBox="0 0 1200 520"
        backgroundColor="#f0f0f0"
        foregroundColor="#dedede"
    >
        {/* 10 accordion header skeletons */}
        {Array.from({length: 10}).map((_, i) => {
            const y = 10 + i * 50;
            return (
                <React.Fragment key={i}>
                    <rect x="0" y={y} rx="6" ry="6" width="390" height="40"/>
                    {/* col-4 */}
                    <rect x="410" y={y} rx="6" ry="6" width="290" height="40"/>
                    {/* col-3 */}
                    <rect x="720" y={y} rx="6" ry="6" width="390" height="40"/>
                    {/* col-5 */}
                    {/* Separators */}
                    <rect x="400" y={y} width="8" height="40"/>
                    <rect x="710" y={y} width="8" height="40"/>
                </React.Fragment>
            );
        })}
    </ContentLoader>
);

export default OrdersSkeleton;