// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;

/**
 * The CurrentServiceSpecNotAlreadyExistedEvent consist event, if cfs is not already present in the system.
 *
 * @author Ankur Singh
 * @version 1.0
 */
public final class CurrentServiceSpecNotAlreadyExistedEvent implements ServiceSpecEvent{

    private final ServiceSpecification serviceSpecification;

    /**
     * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
     */
    private CurrentServiceSpecNotAlreadyExistedEvent() {
        serviceSpecification = null;
    }

    /**
     * Instantiates a new Not already existed event.
     *
     * @param serviceSpecification the service specification
     */
    public CurrentServiceSpecNotAlreadyExistedEvent(ServiceSpecification serviceSpecification) {
        this.serviceSpecification = serviceSpecification;
    }

    @Override
    public String toString() {
        return "CurrentServiceSpecNotAlreadyExistedEvent{" +
                "serviceSpecification=" + serviceSpecification +
                '}';
    }

    /**
     * Gets service specification.
     *
     * @return the service specification
     */
    public ServiceSpecification getServiceSpecification() {
        return serviceSpecification;
    }
}
