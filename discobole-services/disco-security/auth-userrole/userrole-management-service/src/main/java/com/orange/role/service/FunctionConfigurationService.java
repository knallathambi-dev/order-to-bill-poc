// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.service;

import com.orange.discobole.permission.FunctionConfiguration;

import java.util.List;

public interface FunctionConfigurationService {
    FunctionConfiguration save(FunctionConfiguration functionConfiguration);

    List<FunctionConfiguration> getFunctionConfiguration();

    FunctionConfiguration fetchFunctionById(String id);

    void deleteFunction(String id);
}
