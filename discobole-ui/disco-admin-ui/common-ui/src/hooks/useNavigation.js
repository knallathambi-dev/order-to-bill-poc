// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import {useNavigate} from 'react-router-dom';
import {toast} from 'react-toastify';

const useNavigation = () => {
    const navigate = useNavigate();

    const navigateTo = (path, errorMessage) => {
        if (path) {
            navigate(path);
        } else {
            toast.error(errorMessage);
        }
    };

    return {navigateTo};
};

export default useNavigation;