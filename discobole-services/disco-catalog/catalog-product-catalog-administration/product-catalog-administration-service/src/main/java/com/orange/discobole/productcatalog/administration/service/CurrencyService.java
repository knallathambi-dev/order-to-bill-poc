// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service;

import com.orange.disco.admin.Currency;


import java.util.List;
import java.util.Map;


public interface CurrencyService {

    Currency createCurrency(Currency currency);
    List< Currency > getAllCurrency(Map<String, Object> requestParams);

    Currency getCurrencyById(String currencyId);

    Currency updateCurrency(Currency currency);

    void deleteCurrency(String currencyId);
}
