// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.

// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.util;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.MarketSegmentAdmin;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MarketSegmentCache {

    private final Map<String, List<MarketSegmentAdmin>> cache = new ConcurrentHashMap<>();

    public List<MarketSegmentAdmin> get(String key) {
        return cache.get(key);
    }

    public List<MarketSegmentAdmin> computeIfAbsent(String key, java.util.function.Function<String, List<MarketSegmentAdmin>> loader) {
        return cache.computeIfAbsent(key, loader);
    }

    public void clear() {
        cache.clear();
    }

}
