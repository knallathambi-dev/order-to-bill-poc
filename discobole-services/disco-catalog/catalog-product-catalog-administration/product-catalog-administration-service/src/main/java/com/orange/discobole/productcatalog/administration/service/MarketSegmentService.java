// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service;


import com.orange.disco.admin.MarketSegment;

import java.util.List;

public interface MarketSegmentService {

    MarketSegment createMarketSegment(MarketSegment marketSegment);
    List< MarketSegment > getAllMarketSegment();

    MarketSegment getMarketSegmentById(String marketSegmentId);

    MarketSegment updateMarketSegment(MarketSegment marketSegment);

    void deleteMarketSegment(String marketSegmentId);

}
