// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.kafka.producer;


import com.orange.discobole.productinventory.dto.v1.Product;

import java.util.Set;

public interface EventProducer<T extends Product> {
    void publishEvent(T product, String title, String domain);

    void publishEvents(Set<T> products, String title, String domain);
}
