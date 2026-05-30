// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;

/**
 * This class represents an invalid event and that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.productspec.ProductSpecCancelCommand}
 * is triggered.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class InvalidProductSpecCancelledEvent implements ProductSpecEvent {

    private final String productSpecId;
    private final ProductSpecificationLifecycle currentLifeCycle;
    private final ProductSpecificationLifecycle lifeCycleShould;

    private InvalidProductSpecCancelledEvent() {
        productSpecId = null;
        currentLifeCycle = null;
        lifeCycleShould = null;
    }

    /**
     * Instantiates a new Invalid product spec cancelled event.
     *
     * @param productSpecId    the product spec id
     * @param currentLifeCycle the current life cycle
     * @param lifeCycleShould  the life cycle should
     */
    public InvalidProductSpecCancelledEvent(String productSpecId, ProductSpecificationLifecycle currentLifeCycle, ProductSpecificationLifecycle lifeCycleShould) {
        this.productSpecId = productSpecId;
        this.currentLifeCycle = currentLifeCycle;
        this.lifeCycleShould = lifeCycleShould;
    }

    @Override
    public String toString() {
        return "InvalidProductSpecCancelledEvent{" +
                "productSpecId='" + productSpecId + '\'' +
                ", currentLifeCycle=" + currentLifeCycle +
                ", lifeCycleShould=" + lifeCycleShould +
                '}';
    }

    /**
     * Gets product spec id.
     *
     * @return the product spec id
     */
    public String getProductSpecId() {
        return productSpecId;
    }

    /**
     * Gets current life cycle.
     *
     * @return the current life cycle
     */
    public ProductSpecificationLifecycle getCurrentLifeCycle() {
        return currentLifeCycle;
    }

    /**
     * Gets life cycle should.
     *
     * @return the life cycle should
     */
    public ProductSpecificationLifecycle getLifeCycleShould() {
        return lifeCycleShould;
    }
}

