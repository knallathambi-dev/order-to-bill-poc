// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import PropTypes from 'prop-types';
import React, {createContext, useContext, useMemo, useState} from 'react';

const SideMenuContext = createContext();

export const SideMenuProvider = ({children}) => {
    const [isActiveNav, setIsActiveNav] = useState(true);

    const toggleMenu = () => {
        setIsActiveNav((prevState) => !prevState);
    };

    const contextValue = useMemo(
        () => ({
            isActiveNav,
            toggleMenu,
        }),
        [isActiveNav]
    );

    return <SideMenuContext.Provider value={contextValue}>{children}</SideMenuContext.Provider>;
};

SideMenuProvider.propTypes = {
    children: PropTypes.node.isRequired,
};

export const useSideMenu = () => useContext(SideMenuContext);