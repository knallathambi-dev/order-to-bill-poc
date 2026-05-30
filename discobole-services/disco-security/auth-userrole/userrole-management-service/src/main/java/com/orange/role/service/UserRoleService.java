// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.orange.discobole.permission.Entitlement;
import com.orange.discobole.permission.UserRole;



public interface UserRoleService {

	UserRole createUserRole(UserRole userRole, String token) throws IOException;

	UserRole fetchUserRoleById(String id);

	UserRole fetchUserRoleById(String id, List<String> fieldList);

	void updateUserRole(UserRole userRole, String roleId);

	void deleteRole(String roleId, String token);


	long countUserRoles(Map<String, Object> requestParams);

	List<Entitlement> getEntitlements();

	List<Entitlement> createEntitlements(List<Entitlement> entitlements);

	Map<String, Object> fetchCategoryWithCount(Map<String, Object> requestParams, Long offset, Long limit, String fields);
}
