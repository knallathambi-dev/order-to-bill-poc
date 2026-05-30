// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import java.util.Set;

/**
 * The Class InvalidPopIdSelectedEvent is raised when POP Id is invalid.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class InvalidPopIdSelectedEvent implements ProductOfferingEvent {
    private final String prodOfferingId;
    private final Set<String> invalidPopId;

    public InvalidPopIdSelectedEvent(){
        this.prodOfferingId = null;
        this.invalidPopId = null;
    }


    public InvalidPopIdSelectedEvent(String prodOfferingId, Set<String> invalidPopId) {
        this.prodOfferingId = prodOfferingId;
        this.invalidPopId = invalidPopId;
    }

    public String getProdOfferingId() {
        return prodOfferingId;
    }

    public Set<String> getInvalidPopId() {
        return invalidPopId;
    }

    @Override
    public String toString() {
        return "InvalidPopIdSelectedEvent{" +
                "prodOfferingId='" + prodOfferingId + '\'' +
                ", invalidPopId=" + invalidPopId +
                '}';
    }
}
