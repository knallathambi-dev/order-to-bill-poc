// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;

import java.time.OffsetDateTime;

/**
 * The ServiceSpecEarlyTimeRejectedEvent handle event if current cfs timeOccurred is before the existing cfs timeOccurred.
 *
 * @author Ankur Singh
 * @version 1.0
 */
public final class ServiceSpecEarlyTimeRejectedEvent implements ServiceSpecEvent {

    private final String cfsId;
    private final OffsetDateTime newTimeOccurred;
    private final OffsetDateTime oldTimeOccurred;

    /**
     * Use to create instance by reflection at runtime by spring to fetch the event from the event store.
     */
    private ServiceSpecEarlyTimeRejectedEvent() {
        oldTimeOccurred = null;
        newTimeOccurred = null;
        cfsId = null;
    }

    /**
     * Instantiates a new Service spec early time rejected event.
     *
     * @param cfsId           the cfs id
     * @param newTimeOccurred the new time occurred
     * @param oldTimeOccurred the old time occurred
     */
    public ServiceSpecEarlyTimeRejectedEvent(String cfsId, OffsetDateTime newTimeOccurred, OffsetDateTime oldTimeOccurred) {
        this.cfsId = cfsId;
        this.newTimeOccurred = newTimeOccurred;
        this.oldTimeOccurred = oldTimeOccurred;
    }

    @Override
    public String toString() {
        return "ServiceSpecEarlyTimeRejectedEvent{" +
                "cfsId='" + cfsId + '\'' +
                ", newTimeOccurred=" + newTimeOccurred +
                ", oldTimeOccurred=" + oldTimeOccurred +
                '}';
    }

    /**
     * Gets cfs id.
     *
     * @return the cfs id
     */
    public String getCfsId() {
        return cfsId;
    }

    /**
     * Gets new time occurred.
     *
     * @return the new time occurred
     */
    public OffsetDateTime getNewTimeOccurred() {
        return newTimeOccurred;
    }

    /**
     * Gets old time occurred.
     *
     * @return the old time occurred
     */
    public OffsetDateTime getOldTimeOccurred() {
        return oldTimeOccurred;
    }
}
