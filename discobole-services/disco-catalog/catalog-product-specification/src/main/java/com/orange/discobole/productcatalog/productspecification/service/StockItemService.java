// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service;

import com.orange.discobole.productcatalog.productspecification.dto.generated.Event;

/**
 * The StockItemService interface have methods to create stock item.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public interface StockItemService {
    /**
     * This method specifies the operation to create a new stock item.
     *
     * @param event the event
     */
    void processStockItemEvent(final Event event);

    /**
     * This method provides the update operation of the stock item.
     *
     * @param event the event
     */
    void processUpdateStockItemEvent(final Event event);
}
