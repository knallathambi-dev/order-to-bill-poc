// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.utils;

import com.orange.discobole.productinventory.controller.impl.ReportingApiImpl;
import com.orange.discobole.productinventory.dto.v1.Report;
import lombok.experimental.UtilityClass;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static com.orange.discobole.productinventory.constant.Constant.PRODUCT_INVENTORY_MANAGEMENT_BASE_URL;
import static com.orange.discobole.productinventory.constant.Constant.REPORTS_SUB_PATH;

@UtilityClass
public class HrefGenerator {

    public static String getBaseUrlReportsApi() {
        return WebMvcLinkBuilder
                .linkTo(ReportingApiImpl.class)
                .slash(PRODUCT_INVENTORY_MANAGEMENT_BASE_URL)
                .slash(REPORTS_SUB_PATH)
                .withSelfRel().getHref();
    }

    public static void generateHrefReport(Report report) {
        String baseUrl = getBaseUrlReportsApi();
        String selfLink = getHrefReport(report.getId(), report.getAtType(), baseUrl);
        report.setHref(selfLink);
    }

    public static String getHrefReport(String id, String type, String baseUrl) {
        return String.format("%s/%s/%s", baseUrl, type, id);
    }
}
