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

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ChannelAdmin;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ChannelCache {

    private final Map<String, List<ChannelAdmin>> cache = new ConcurrentHashMap<>();

    public List<ChannelAdmin> get(String key) {
        return cache.get(key);
    }

    public List<ChannelAdmin> computeIfAbsent(String key, java.util.function.Function<String, List<ChannelAdmin>> mappingFunction) {
        return cache.computeIfAbsent(key, mappingFunction);
    }

    public void clear() {
        cache.clear();
    }
}

