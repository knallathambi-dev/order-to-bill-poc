// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.service;

import com.orange.discobole.permission.Entitlement;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


public interface EntitlementService {

	List<Entitlement> createEntitlement(List<Entitlement> entitlement, String token) throws IOException;


	Entitlement fetchEntitlementById(String id);

	Entitlement fetchEntitlementById(String id, List<String> fieldList);

	List<Entitlement> fetchEntitlements(Map<String, Object> requestParams, Long offset, Long limit, String fields)
			throws UnsupportedEncodingException;

	Entitlement updateEntitlement(Entitlement entitlement, String id);

	void deleteEntitlement(String id, String token);


	long countEntitlement(Map<String, Object> requestParams);

}
