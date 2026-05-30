// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.productoffering;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.productcatalog.productoffering.constant.OdacaConstants;

public interface ProductOfferingCategoryEvent extends Event {
	@Override
	default String aggregateName() {
		return OdacaConstants.PRODUCTUOFFERING_CATEGORY;
	}
}
