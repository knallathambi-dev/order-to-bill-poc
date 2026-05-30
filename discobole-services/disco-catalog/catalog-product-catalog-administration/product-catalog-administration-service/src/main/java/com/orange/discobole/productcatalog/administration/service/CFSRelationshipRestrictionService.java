// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service;
import com.orange.disco.admin.CFSRelationshipRestriction;

import java.util.List;

public interface CFSRelationshipRestrictionService {
    CFSRelationshipRestriction createCFSRelationship(CFSRelationshipRestriction cfsRelationship);

    CFSRelationshipRestriction getCFSRelationshipById(String cfsRelationshipId);

    CFSRelationshipRestriction updateCFSRelationship(CFSRelationshipRestriction cfsRelationship);

    List<CFSRelationshipRestriction> getAllCFSRelationship();

//    void deleteCFSRelationship(String id);
}
