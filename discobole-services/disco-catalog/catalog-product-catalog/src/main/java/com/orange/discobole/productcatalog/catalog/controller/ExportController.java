// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.util.IOUtils;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.JobStateType;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orange.discobole.processflow.exception.DiscoClientException;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ExportJob;
import com.orange.discobole.productcatalog.catalog.service.ExportService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import javax.net.ssl.SSLContext;


/**
 * The ExportController is to export the product offerings.
 *
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

@RestController
@Tag(name = "exportJob")
@RequestMapping(value = "/productCatalogManagement/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExportController {
	private static final Logger LOGGER = LogManager.getLogger(ExportController.class);

	@Resource
	ExportService exportService;

	@Value("${spring.cloud.aws.s3.httpProxy:}")
	private String httpProxy;

	/**
	 * export data of product catalog.
	 *
	 * @param export the export
	 * @return the response entity of Export file
	 */

	@PostMapping("/exportJob")
	public ResponseEntity<Map<String, Object>> exportProductOffering(@RequestBody ExportJob export) {
		LOGGER.info("Object of Export Job-{}", export);
		exportService.validateExportData(export);

		ExportJob exportData = exportService.saveExportData(export);  // RUNNING status

		// Background  export started
		exportService.processExportAsync(exportData);

		// Immediate response
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Export Job in progress ");
		response.put("jobId", exportData.getId());
		response.put("status", "RUNNING");
		response.put("checkStatusUrl", "/productCatalogManagement/v1/exportJob/" + exportData.getId());

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}

	/**
	 * Find list of export Job data based on different criteria.
	 *
	 * @param href        the href
	 * @param contentType the contentType
	 * @param lastUpdate  the last update
	 * @param errorLog    the errorLog
	 * @param path        the path
	 */
	@GetMapping("/exportJob")
	public ResponseEntity<List<ExportJob>> findExportJobData(@RequestParam(name = "id", required = false) String id,
															 @RequestParam(name = "href", required = false) String href,
															 @RequestParam(name = "contentType", required = false) String contentType,
															 @RequestParam(name = "errorLog", required = false) String errorLog,
															 @RequestParam(name = "path", required = false) String path,
															 @RequestParam(name = "query", required = false) String query,
															 @RequestParam(name = "completionDate", required = false) OffsetDateTime completionDate,
															 @RequestParam(name = "creationDate", required = false) OffsetDateTime creationDate,
															 @RequestParam(name = "url", required = false) String url,
															 @RequestParam(name = "status", required = false) String status) throws UnsupportedEncodingException {
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("_id", id);
		requestParams.put("href", href);
		requestParams.put("contentType", contentType);
		requestParams.put("errorLog", errorLog);
		requestParams.put("path", path);
		requestParams.put("query", query);
		requestParams.put("completionDate", completionDate);
		requestParams.put("creationDate", creationDate);
		requestParams.put("url", url);
		requestParams.put("status", status);
		List<ExportJob> exportJobData = exportService.fetchExportJob(requestParams);
		LOGGER.info("list of export Job-{}", exportJobData);
		if (exportJobData.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		LOGGER.info("list of export Job Data-{}", exportJobData.size());
		return ResponseEntity.status(HttpStatus.OK).body(exportJobData);
	}

	/**
	 * Export job by id response entity.
	 *
	 * @param id     the id
	 * @param fields to filter the fields from exportJob
	 * @return the response entity of export Job
	 */
	@GetMapping("/exportJob/{id}")
	public ResponseEntity<ExportJob> exportJobById(@PathVariable String id,
												   @RequestParam(name = "fields", required = false) String fields) {
		ExportJob exportJob;
		if (null == fields)
			exportJob = exportService.fetchExportJobById(id);
		else {
			List<String> fieldList = Arrays.asList(fields.split(","));
			exportJob = exportService.fetchExportJobById(id, fieldList);
		}

		if (null == exportJob) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(exportJob);
	}

	@GetMapping("/exportJob/{id}/download")
	public ResponseEntity<byte[]> downloadExport(@PathVariable String id) throws IOException {
		ExportJob job = exportService.fetchExportJobById(id);

		if (job == null || !JobStateType.SUCCEEDED.equals(job.getStatus()) || job.getUrl() == null) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).build();
		}

		byte[] data;

		try {
			// Create a custom SSL context that trusts all certificates
			SSLContext sslContext = new SSLContextBuilder()
					.loadTrustMaterial(null, (certificate, authType) -> true)
					.build();

			SSLConnectionSocketFactory sslSocketFactory =
					new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

			// Build a local HttpClient just for this request
			var clientBuilder = HttpClients.custom()
					.setSSLSocketFactory(sslSocketFactory);

			// Optional proxy
			if (httpProxy != null && !httpProxy.trim().isEmpty()) {
				URI p = URI.create(httpProxy.trim());
				if (p.getHost() != null && p.getPort() > 0) {
					clientBuilder.setProxy(new HttpHost(p.getHost(), p.getPort(), p.getScheme()));
				}
			}

			try (CloseableHttpClient httpClient = clientBuilder.build()) {
				HttpGet request = new HttpGet(job.getUrl());
				try (CloseableHttpResponse response = httpClient.execute(request)) {
					data = IOUtils.toByteArray(response.getEntity().getContent());
				}
			}

		} catch (Exception e) {
			LOGGER.error("Failed to download file with SSL bypass from URL: {}", job.getUrl(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		String fileName = job.getFileName() != null ? job.getFileName() : "export.xlsx";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
		headers.setContentLength(data.length);

		return ResponseEntity.ok()
				.headers(headers)
				.body(data);
	}


}
