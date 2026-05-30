// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service;

import com.orange.disco.admin.FrequencyTypes;

import java.util.List;

public interface FrequencyTypeService {

    FrequencyTypes createFrequencyTypes(FrequencyTypes frequencyType);
    List< FrequencyTypes > getAllFrequencyTypes();

    FrequencyTypes getFrequencyTypesById(String frequencyTypeId);

    FrequencyTypes updateFrequencyTypes(FrequencyTypes frequencyType);

    void deleteFrequencyType(String frequencyTypeId);
}
