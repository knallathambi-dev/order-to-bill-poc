// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.servicespec;


import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.productcatalog.catalog.constant.OdacaConstants;

/**
 * The Interface ServiceSpecEvent that sets the aggregate name for Service Spec.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public interface ServiceSpecEvent extends Event {

	@Override
	default String aggregateName() {
		return OdacaConstants.SERVICE_SPEC;
	}

}
