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
 * The ServiceSpecService interface have methods to create and update the service specification.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
public interface ServiceSpecService {

	/**
	 * This method specifies the operation to create a new service specification.
	 *
	 * @param event the event
	 */
	void processEvent(final Event event);


	/**
	 * This method provides the update operation of the service specification.
	 *
	 * @param event the event
	 */
	void processUpdateServiceSpecEvent(final Event event);
}
