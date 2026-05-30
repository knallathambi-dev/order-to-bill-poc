// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.ExportJob;

import jakarta.validation.Valid;

/**
 * The Interface ExportService to export the product offerings.
 * 
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public interface ExportService {

	ExportJob saveExportData(@Valid ExportJob export);

	List<ExportJob> fetchExportJob(Map<String, Object> requestParams) throws UnsupportedEncodingException;

	ExportJob fetchExportJobById(String id);

	ExportJob fetchExportJobById(String id, List<String> fieldList);

	String getFileName(int sheetSize,ExportJob exportJob);

	XSSFWorkbook createWorkBook(ExportJob exportData);

	void updateExportStatus(String exportId);

	void validateExportData(ExportJob export);

	void processExportAsync(ExportJob exportData);

}
