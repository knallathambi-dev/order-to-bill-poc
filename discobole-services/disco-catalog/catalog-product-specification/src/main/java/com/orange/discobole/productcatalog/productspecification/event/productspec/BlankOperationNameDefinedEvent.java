// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.Set;

/**
 * The Class BlankOperationNameDefinedEvent generated when name of operation is
 * blank.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class BlankOperationNameDefinedEvent implements ProductSpecEvent {
	private final String productSpecId;
	private final Set<String> blankOperationName;

	private BlankOperationNameDefinedEvent() {
		productSpecId = null;
		blankOperationName = null;
	}

	public BlankOperationNameDefinedEvent(String productSpecId, Set<String> blankOperationName) {
		this.productSpecId = productSpecId;
		this.blankOperationName = blankOperationName;
	}

	@Override
	public String toString() {
		return "BlankOperationNameDefinedEvent [productSpecId=" + productSpecId + ", blankOperationName="
				+ blankOperationName + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public Set<String> getBlankOperationName() {
		return blankOperationName;
	}

}
