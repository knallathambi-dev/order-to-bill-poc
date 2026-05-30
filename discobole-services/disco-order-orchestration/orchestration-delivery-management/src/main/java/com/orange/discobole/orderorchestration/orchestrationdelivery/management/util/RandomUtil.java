// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
package com.orange.discobole.orderorchestration.orchestrationdelivery.management.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@SuppressFBWarnings("PREDICTABLE_RANDOM")
public class RandomUtil {
    private RandomUtil() {
    }

    public static Duration randomBetween(Duration min, Duration max) {
        if (min.compareTo(max) >= 0) {
            throw new IllegalArgumentException("min must be less than max");
        }

        long minMillis = min.toMillis();
        long maxMillis = max.toMillis();

        long randomMillis = ThreadLocalRandom.current().nextLong(minMillis, maxMillis + 1);
        return Duration.ofMillis(randomMillis);
    }
}