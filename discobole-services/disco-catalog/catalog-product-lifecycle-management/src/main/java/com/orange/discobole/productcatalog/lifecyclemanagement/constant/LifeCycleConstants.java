// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.constant;

import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.CancelEntityOperation;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.DefineLifecycleState;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.SelectLifecycleEntity;

/**
 * 
 * This class represents the constants used in managelifecycle  service.
 * @author GMBV8677
 *
 */
public class LifeCycleConstants {

	private LifeCycleConstants() {
	}
	public static final String ID = "id";
	public static final String PRODUCT_ENTITY = SelectLifecycleEntity.class.getSimpleName();
	public static final String PRODUCT_UPDATE_STATE = DefineLifecycleState.class.getSimpleName();
	public static final String LIFECYCLE_CANCEL = CancelEntityOperation.class.getSimpleName();
	public static final String ENTITY_ID = "entityId";
	public static final String ENTITY_TYPE = "entityType";
	public static final String ENTITY_AGG = "aggregateId";
	

}
