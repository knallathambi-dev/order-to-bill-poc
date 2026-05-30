// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.*;
import com.orange.discobole.productcatalog.catalog.dto.generated.servicespec.*;
import com.orange.discobole.productcatalog.catalog.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.exception.DiscoManagedException;
import com.orange.discobole.productcatalog.catalog.constant.ExcelSheetConstants;
import com.orange.discobole.productcatalog.catalog.constant.SupportEntity;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.*;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.catalog.dto.generated.stockitem.StockItemCharacteristicValue;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;


import jakarta.validation.Valid;

/**
 * The ExportService to export the product offerings.
 *
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

@Service
public class ExportServiceImpl implements ExportService {

	private static final String DESCRIPTION = "Description";

	private static final String CONTRACT_BUNDLE_SHEET = "ContractPO-ChildPO";
	private static final String BUNDLE_CHILD_SHEET = "BundlePO-ChildPO";
	private static final String PO_MASTER_SHEET = "PO master sheet";
	private static final String ATOMIC_PS_SHEET = "AtomicPO-PS";
	private static final String PS_MASTER_SHEET = "PS Master Sheet";
	private static final String PS_CHAR_SHEET = "PS Characteristics";
	private static final String PS_CHAR_VALUE_SHEET = "PS Characteristics Value";
	private static final String PS_CFS_SHEET = "PS-CFS";
	private static final String CFS_SHEET = "CFS";
	private static final String POP_MASTER_SHEET = "POP Master Sheet";
	private static final String POP_CHARGE_ALT_SHEET = "POP Charge-Alteration";
	private static final String STOCK_ITEM_SHEET = "Stock Item";
	private static final String ENTITY_REL_SHEET = "Entity Relationships";
	private static final String CATEGORY_SHEET = "Category";
	private static final String ERROR_SHEET = "Error Sheet";
	private static final String PO_CHAR_VALUE_SHEET = "POCharValues";

    // step1c
	private static final String CFS_MASTER_SHEET = "CFS Master sheet";
	private static final String CFS_CHAR_SHEET = "CFS Characteristics";

	// step1s: Add new sheet constants for stockItem export
	private static final String STOCK_ITEM_CHAR_SHEET = "Stock Item Characteristics";

	private static final String CFS_CHAR_VALUE_SHEET = "CFS Characteristics Value";

	private static final String LAST_UPDATE = "Last Update";

	private static final String LIFECYCLE_STATUS = "Lifecycle Status";

	private static final String PRODUCT_SPECIFICATION_ID = "Product Specification ID";

	private static final String PRODUCT_SPECIFICATION_NAME = "Product Specification Name";

	private static final String STOCKITEMID = "Stock Item ID";

	private static final String STOCKITEMNAME = "Stock Item Name";

	private static final String CFSNAME = "CFS Name";

	private static final String CFSID = "CFS ID";

	private static final String VALIDITY_END_DATE = "Validity End Date";

	private static final String VALIDITY_START_DATE = "Validity Start Date";

	private static final String ISDEFAULT = "isDefault";

	private static final String VALUEFROM = "Value From";

	private static final String VALUETO = "Value To";

	private static final String CONFIG = "Configurable";

	private static final String EXTENSIBLE = "Extensible";

	private static final String ISUNIQUE = "IsUnique";

	private static final String VALUETYPE = "Value Type";

	private static final String VERSION = "Version";

	private static final String STATUS = "status";

	private static final String ADDID = "addressId";

	private static final String SUBUNIT = "subUnitNumber";

	private static final String STREET = "streetName";

	private static final String POST = "postcode";

	private static final String CITY = "city";
	private static final String COUNTRY = "country";

	private static final String PSR = "productSpecification";

	private static final String POR = "productOffering";

	// step2c
	private static final String CFSR = "serviceSpecification";

	private static final String BRAND = "brand";

	private static final String UOM = "Unit Of Measure";

	private static final String VALUE = "Value";

	// step2s: Add constant for stockItem path check
	private static final String STOCK = "stockItem";

	private static final String IDNOTPRESENT = "id not present in db";

	private static final String EXCEL = ".xlsx";

	private static final String HIERARCHY = "hierarchy";

	private static final String ERRORMSG = "Error Message";

	private final ProductOfferingService productOfferingService;
	private final ProductSpecService productSpecService;
	private final ServiceSpecService serviceSpecsService;
	private final ProductOfferingPriceService productOfferingPriceService;
	private final StockItemService stockItemService;
	private final CategoryService categoryService;
	private MongoTemplate mongoTemplate;

	@Autowired
	private S3Service s3Service;

	@Autowired
	public ExportServiceImpl(ProductOfferingService productOfferingService, ProductSpecService productSpecService,
							 ServiceSpecService serviceSpecsService, ProductOfferingPriceService productOfferingPriceService,
							 StockItemService stockItemService, CategoryService categoryService, MongoTemplate mongoTemplate) {
		this.productOfferingService = productOfferingService;
		this.productSpecService = productSpecService;
		this.serviceSpecsService = serviceSpecsService;
		this.categoryService = categoryService;
		this.mongoTemplate = mongoTemplate;
		this.productOfferingPriceService = productOfferingPriceService;
		this.stockItemService = stockItemService;
	}

	private static final Logger LOGGER = LogManager.getLogger(ExportServiceImpl.class);

	private static final String DISCO_CPO_NOT_FOUND = "DISCO_CPO_NOT_FOUND";
	private static final String DISCO_CPO_ID_NOT_FOUND = "DISCO_CPO_ID_NOT_FOUND";
	private static final String DISCO_BPO_NOT_FOUND = "DISCO_BPO_NOT_FOUND";
	private static final String DISCO_APO_NOT_FOUND = "DISCO_APO_NOT_FOUND";
	private static final String DISCO_INVALID_QUERY_FORMAT = "DISCO_INVALID_QUERY_FORMAT";
	private static final String DISCO_CPO_WORKBOOK_EXCEPTION = "DISCO_CPO_WORKBOOK_EXCEPTION";
	private static final String DISCO_PS_NOT_FOUND = "DISCO_PS_NOT_FOUND";
	private static final String DISCO_POP_NOT_FOUND = "DISCO_POP_NOT_FOUND";
	private static final String DISCO_STOCKITEM_NOT_FOUND = "DISCO_STOCKITEM_NOT_FOUND";
	private static final String DISCO_CATEGORY_NOT_FOUND = "DISCO_CATEGORY_NOT_FOUND";
	private static final String MISSING_MANDATORY_FIELDS_NOT_FOUND = "MISSING_MANDATORY_FIELDS_NOT_FOUND";
	private static final String DISCO_CFS_NOT_FOUND = "DISCO_CFS_NOT_FOUND";


	// Step 2: Add new error code for CFS
	private static final String DISCO_SS_NOT_FOUND = "DISCO_SS_NOT_FOUND";

	public static final String INVALID_PATH = "INVALID_PATH";



	@Override
	public void validateExportData(ExportJob export) {
		if (export.getPath() == null || export.getPath().isBlank()) {
			throw new DiscoManagedException(MISSING_MANDATORY_FIELDS_NOT_FOUND, "", "path");
		}
       //step 3 step3s
		if (!export.getPath().contains(PSR)  && !export.getPath().contains(POR) && !export.getPath().contains(CFSR) && !export.getPath().contains(STOCK)) {
			throw new DiscoManagedException(INVALID_PATH, "", "path");
		}
		if (export.getQuery() == null || export.getQuery().isBlank()) {
			throw new DiscoManagedException(MISSING_MANDATORY_FIELDS_NOT_FOUND, "", "query");
		}
	}

	/**
	 * Saving ExportJob.
	 *
	 * @param ExportJob export to search upon
	 * @return ExportJob
	 */
	@Override
	public ExportJob saveExportData(@Valid ExportJob export) {
		LOGGER.info("Entering saveExportData method {}", export);
		OffsetDateTime creationDate = OffsetDateTime.now();
		ExportJob exportJob = new ExportJob();
		exportJob.setCreationDate(creationDate);
		exportJob.setStatus(JobStateType.RUNNING);
		exportJob.setQuery(export.getQuery());
		exportJob.setPath(export.getPath());
		exportJob = mongoTemplate.save(exportJob);
		return exportJob;
	}

	/**
	 * Creating workbook from ExportJob.
	 *
	 * @param ExportJob export to search upon
	 * @return XSSFWorkbook
	 */
	@Async
	public void processExportAsync(ExportJob exportJob) {
		try (XSSFWorkbook workbook = createWorkBook(exportJob);
			 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			String fileName = getFileName(workbook.getNumberOfSheets(), exportJob);

			workbook.write(baos);
			byte[] fileData = baos.toByteArray();

			// S3 upload + Presigned URL
			String presignedUrl = s3Service.uploadFileAndGetPresignedUrl(fileName, fileData);

			// Job update in MongoDB
			Update update = new Update()
					.set(STATUS, JobStateType.SUCCEEDED)
					.set("completionDate", OffsetDateTime.now())
					.set("fileName", fileName)
					.set("url", presignedUrl); // CloudAvenue URL save

			mongoTemplate.updateFirst(
					Query.query(Criteria.where("_id").is(exportJob.getId())),
					update,
					ExportJob.class
			);
		} catch (Exception e) {
			updateExportJob(exportJob.getId(), JobStateType.FAILED, e.getMessage());
			LOGGER.error("Export failed for job {}", exportJob.getId(), e);
		}
	}



	@Override
	public XSSFWorkbook createWorkBook(ExportJob export) {
		LOGGER.info("Creating workbook from exportJob: {}", export);
		Map<String, Object> requestParams = extractQuery(export);
		String hierarchy = extractHierarchy(requestParams);

		if (export.getPath().contains(CFSR)) {
			return handleCFSExport(export, requestParams, hierarchy);
		} else if (export.getPath().contains(PSR)) {
			return handlePSExport(export, requestParams, hierarchy);
		} else if (export.getPath().contains(STOCK)) {
			return handleStockExport(export, requestParams, hierarchy);
		} else {
			return handlePOExport(export, requestParams, hierarchy);
		}
	}

// --- Helper methods ---

	private String extractHierarchy(Map<String, Object> requestParams) {
		String hierarchy = "";
		if (requestParams.containsKey(HIERARCHY)) {
			hierarchy = (String) requestParams.get(HIERARCHY);
			requestParams.remove(HIERARCHY);
		}
		return hierarchy;
	}

	private XSSFWorkbook handleCFSExport(ExportJob export, Map<String, Object> requestParams, String hierarchy) {
		List<ServiceSpecification> serviceSpecifications = fetchServiceSpecifications(export, requestParams);
		Set<String> requestedIds = extractRequestedIds(requestParams);
		Set<String> foundIds = extractFoundIds(serviceSpecifications, ServiceSpecification::getId);
		Set<String> missingIds = findMissingIds(requestedIds, foundIds);

		if (serviceSpecifications.isEmpty()) {
			return handleEmptyExport(export, missingIds, this::extractedErrorSheetForCFS, DISCO_SS_NOT_FOUND, "ServiceSpecification is empty");
		}

		XSSFWorkbook workbook = writeWorkbookForCFS(serviceSpecifications, export, hierarchy);
		addErrorSheetIfMissingIds(workbook, missingIds, this::extractedErrorSheetForCFS);
		return workbook;
	}

	private XSSFWorkbook handlePSExport(ExportJob export, Map<String, Object> requestParams, String hierarchy) {
		List<ProductSpecification> productSpecifications = fetchProductSpecifications(export, requestParams);
		Set<String> requestedIds = extractRequestedIds(requestParams);
		Set<String> foundIds = extractFoundIds(productSpecifications, ProductSpecification::getId);
		Set<String> missingIds = findMissingIds(requestedIds, foundIds);

		if (productSpecifications == null || productSpecifications.isEmpty()) {
			return handleEmptyExport(export, missingIds, this::extractedErrorSheetForPS, DISCO_PS_NOT_FOUND, "ProductSpecification is empty");
		}

		XSSFWorkbook workbook = writeWorkbookForPS(productSpecifications, export, hierarchy);
		addErrorSheetIfMissingIds(workbook, missingIds, this::extractedErrorSheetForPS);
		return workbook;
	}

	private XSSFWorkbook handleStockExport(ExportJob export, Map<String, Object> requestParams, String hierarchy) {
		List<StockItem> stockItems = fetchStockItems(export, requestParams);
		Set<String> requestedIds = extractRequestedIds(requestParams);
		Set<String> foundIds = extractFoundIds(stockItems, StockItem::getId);
		Set<String> missingIds = findMissingIds(requestedIds, foundIds);

		if (stockItems.isEmpty()) {
			return handleEmptyExport(export, missingIds, this::extractedErrorSheetForStock, DISCO_STOCKITEM_NOT_FOUND, "StockItem is empty");
		}

		XSSFWorkbook workbook = writeWorkbookForStockItem(stockItems, export, hierarchy);
		addErrorSheetIfMissingIds(workbook, missingIds, this::extractedErrorSheetForStock);
		return workbook;
	}

	private XSSFWorkbook handlePOExport(ExportJob export, Map<String, Object> requestParams, String hierarchy) {
		List<ProductOffering> productOfferings;
		try {
			productOfferings = getProductOfferings(requestParams, null, null, null);
		} catch (UnsupportedEncodingException e) {
			updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
			throw new DiscoManagedClientException(DISCO_CPO_ID_NOT_FOUND);
		}
		if (productOfferings == null || productOfferings.isEmpty()) {
			updateExportJob(export.getId(), JobStateType.FAILED, "ProductOffering is empty");
			throw new DiscoManagedClientException(DISCO_CPO_NOT_FOUND);
		}
		return writeWorkbook(productOfferings, export, hierarchy);
	}

// --- Utility helpers ---

	private List<ServiceSpecification> fetchServiceSpecifications(ExportJob export, Map<String, Object> requestParams) {
		try {
			return getServiceSpecifications(requestParams);
		} catch (Exception e) {
			updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
			throw new DiscoManagedClientException(DISCO_SS_NOT_FOUND);
		}
	}

	private List<ProductSpecification> fetchProductSpecifications(ExportJob export, Map<String, Object> requestParams) {
		try {
			return getProductSpecifications(requestParams, null, null, null);
		} catch (UnsupportedEncodingException e) {
			updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
			throw new DiscoManagedClientException(DISCO_PS_NOT_FOUND);
		}
	}

	private List<StockItem> fetchStockItems(ExportJob export, Map<String, Object> requestParams) {
		try {
			return getStockItemss(requestParams);
		} catch (Exception e) {
			updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
			throw new DiscoManagedClientException(DISCO_STOCKITEM_NOT_FOUND);
		}
	}

	private Set<String> extractRequestedIds(Map<String, Object> requestParams) {
		Set<String> requestedIds = new HashSet<>();
		if (requestParams.containsKey("_id")) {
			String[] ids = ((String) requestParams.get("_id")).split(",");
			for (String id : ids) {
				requestedIds.add(id.trim());
			}
		}
		return requestedIds;
	}

	private <T> Set<String> extractFoundIds(List<T> list, java.util.function.Function<T, String> idGetter) {
		return list != null
				? list.stream().map(idGetter).collect(Collectors.toSet())
				: Collections.emptySet();
	}

	private Set<String> findMissingIds(Set<String> requestedIds, Set<String> foundIds) {
		Set<String> missingIds = new HashSet<>(requestedIds);
		missingIds.removeAll(foundIds);
		return missingIds;
	}

	private XSSFWorkbook handleEmptyExport(
			ExportJob export,
			Set<String> missingIds,
			java.util.function.Function<XSSFWorkbook, Sheet> errorSheetExtractor,
			String errorCode,
			String errorMsg) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		if (!missingIds.isEmpty()) {
			Sheet errorSheet = errorSheetExtractor.apply(workbook);
			for (String missingId : missingIds) {
				Row errorRow = errorSheet.createRow(errorSheet.getLastRowNum() + 1);
				errorRow.createCell(0).setCellValue(missingId);
				errorRow.createCell(1).setCellValue(IDNOTPRESENT);
			}
		}
		updateExportJob(export.getId(), JobStateType.FAILED, errorMsg);
		throw new DiscoManagedClientException(errorCode);
	}

	private void addErrorSheetIfMissingIds(
			XSSFWorkbook workbook,
			Set<String> missingIds,
			java.util.function.Function<XSSFWorkbook, Sheet> errorSheetExtractor) {
		if (!missingIds.isEmpty()) {
			Sheet errorSheet = errorSheetExtractor.apply(workbook);
			for (String missingId : missingIds) {
				Row errorRow = errorSheet.createRow(errorSheet.getLastRowNum() + 1);
				errorRow.createCell(0).setCellValue(missingId);
				errorRow.createCell(1).setCellValue(IDNOTPRESENT);
			}
		}
	}


	private Map<String, Object> extractQuery(ExportJob export) {
		Map<String, Object> requestParams = new HashMap<>();
		String query = export.getQuery();
		try {
			query = query.trim();
			String[] pairs = (query).split("&");
			for (String pair : pairs) {
				String[] keyValue = pair.split("=");
				if (keyValue[0].equalsIgnoreCase("id")) {
					requestParams.put("_id", keyValue[1].trim());
				} else if (keyValue[0].equalsIgnoreCase("lifecycleStatus")) {
					requestParams.put("lifecycleStatus", getUpperCaseData(keyValue[1].trim()));
				} else if (keyValue[0].equalsIgnoreCase("@type")) {
					requestParams.put("type", getUpperCaseData(keyValue[1].trim()));
				} else {
					requestParams.put(keyValue[0].trim(), keyValue[1].trim());
				}
			}
		} catch (Exception e) {
			updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
			throw new DiscoManagedClientException(DISCO_INVALID_QUERY_FORMAT);
		}
		return requestParams;
	}

	private String getUpperCaseData(String data) {
		List<String> stateUpperCase = Arrays.asList(data.split(","));
		data = stateUpperCase.stream().map(String::toUpperCase).collect(Collectors.joining(","));
		return data;
	}

	/**
	 * Updating ExportJob Status.
	 *
	 * @param ExportJob export to search upon
	 */
	@Override
	public void updateExportStatus(String exportId) {
		updateExportJob(exportId, JobStateType.SUCCEEDED, "");
	}

	private void updateExportJob(String exportId, JobStateType status, String errorMessage) {
		OffsetDateTime completionDate = OffsetDateTime.now();
		Update update = new Update();
		update.set(STATUS, status);
		update.set("completionDate", completionDate);
		update.set("errorLog", errorMessage);
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(exportId));
		mongoTemplate.updateFirst(query, update, ExportJob.class);
	}

	//  6: New method for writing PS workbook
	private XSSFWorkbook writeWorkbookForPS(List<ProductSpecification> productSpecifications, ExportJob export, String hierarchy) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			if (hierarchy.isBlank() || hierarchy.isEmpty()) {
				//  7: Write only PS Master Sheet if no hierarchy is specified
				writeDataToWorkbookPS(workbook, productSpecifications);
			} else {
				//  8: Write PS hierarchy data
				writeDataToWorkbookPSHierarchy(workbook, productSpecifications, export);
				implementHierarchyForPS(workbook, hierarchy);
			}
			alterWorkbookFonts(workbook);
		} catch (Exception e) {
			updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
			throw new DiscoManagedException(DISCO_CPO_WORKBOOK_EXCEPTION);
		}
		return workbook;
	}


	// step5c : New method for writing CFS workbook
	private XSSFWorkbook writeWorkbookForCFS(List < ServiceSpecification > serviceSpecifications, ExportJob
			export, String hierarchy) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			if (hierarchy.isBlank() || hierarchy.isEmpty()) {
				writeDataToWorkbookCFS(workbook, serviceSpecifications);
			} else {
				writeDataToWorkbookCFSHierarchy(workbook, serviceSpecifications,
						export);
				implementHierarchyForCFS(workbook, hierarchy);
			}
			alterWorkbookFonts(workbook);
		} catch (Exception e) {
			updateExportJob(
					export.getId(), JobStateType.FAILED, e.getMessage());
			throw new DiscoManagedException(DISCO_CPO_WORKBOOK_EXCEPTION);
		}
		return workbook;
	}


	// step5s : Add new method for writing StockItem workbook
	private XSSFWorkbook writeWorkbookForStockItem(List <StockItem> stockItems, ExportJob
			export, String hierarchy) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			if (hierarchy.isBlank() || hierarchy.isEmpty()) {
				writeDataToWorkbookStock(workbook, stockItems);
			} else {
				writeDataToWorkbookStockHierarchy(workbook, stockItems,
						export);
				implementHierarchyForStock(workbook, hierarchy);
			}
			alterWorkbookFonts(workbook);
		} catch (Exception e) {
			updateExportJob(
					export.getId(), JobStateType.FAILED, e.getMessage());
			throw new DiscoManagedException(DISCO_CPO_WORKBOOK_EXCEPTION);
		}
		return workbook;
	}

	//  9: New method for PS hierarchy implementation
	private void implementHierarchyForPS(XSSFWorkbook workbook, String hierarchy) {
		Set<String> selectedHierarchy = Arrays.stream(hierarchy.split(",")).map(String::trim)
				.collect(Collectors.toSet());

		Map<String, List<String>> hierarchyToSheetsMap = Map.of(
				"all", List.of(PS_MASTER_SHEET, PS_CHAR_SHEET, PS_CHAR_VALUE_SHEET, PS_CFS_SHEET,
						CFS_SHEET, STOCK_ITEM_SHEET, ENTITY_REL_SHEET),
				PSR, List.of(PS_MASTER_SHEET, PS_CHAR_SHEET, PS_CHAR_VALUE_SHEET,
						PS_CFS_SHEET, CFS_SHEET, STOCK_ITEM_SHEET, ENTITY_REL_SHEET));

		Set<String> selectedSheets = new HashSet<>();
		for (String hier : selectedHierarchy) {
			if (hierarchyToSheetsMap.containsKey(hier)) {
				selectedSheets.addAll(hierarchyToSheetsMap.get(hier));
			}
		}

		if (selectedHierarchy.isEmpty() || selectedHierarchy.contains("all")) {
			selectedSheets.addAll(hierarchyToSheetsMap.get("all"));
		}
		//err
		selectedSheets.add(ERROR_SHEET);
		List<String> allSheetNames = List.of(PS_MASTER_SHEET, PS_CHAR_SHEET, PS_CHAR_VALUE_SHEET,
				PS_CFS_SHEET, CFS_SHEET, STOCK_ITEM_SHEET, ENTITY_REL_SHEET, ERROR_SHEET);

		for (String sheetName : allSheetNames) {
			if (!selectedSheets.contains(sheetName)) {
				int sheetIndex = workbook.getSheetIndex(sheetName);
				if (sheetIndex != -1) {
					workbook.removeSheetAt(sheetIndex);
				}
			}
		}
	}

	// step6c :  method for CFS hierarchy implementation
	private void implementHierarchyForCFS(XSSFWorkbook workbook, String hierarchy) {
		Set < String > selectedHierarchy = Arrays.stream(hierarchy.split(",")).map(String::trim)
				.collect(Collectors.toSet());

		Map < String, List < String >> hierarchyToSheetsMap = Map.of(
				"all", List.of(CFS_MASTER_SHEET, CFS_CHAR_SHEET, CFS_CHAR_VALUE_SHEET, ENTITY_REL_SHEET),
				CFSR, List.of(CFS_MASTER_SHEET, CFS_CHAR_SHEET, CFS_CHAR_VALUE_SHEET, ENTITY_REL_SHEET));

		Set < String > selectedSheets = new HashSet < > ();
		for (String hier: selectedHierarchy) {
			if (hierarchyToSheetsMap.containsKey(hier)) {
				selectedSheets.addAll(hierarchyToSheetsMap.get(hier));
			}
		}

		if (selectedHierarchy.isEmpty() || selectedHierarchy.contains("all")) {
			selectedSheets.addAll(hierarchyToSheetsMap.get("all"));
		}
		selectedSheets.add(ERROR_SHEET);
		List < String > allSheetNames = List.of(CFS_MASTER_SHEET, CFS_CHAR_SHEET, CFS_CHAR_VALUE_SHEET, ENTITY_REL_SHEET, ERROR_SHEET);

		for (String sheetName: allSheetNames) {
			if (!selectedSheets.contains(sheetName)) {
				int sheetIndex = workbook.getSheetIndex(sheetName);
				if (sheetIndex != -1) {
					workbook.removeSheetAt(sheetIndex);
				}
			}
		}
	}

	// step6s: method for StockItem hierarchy implementation
	private void implementHierarchyForStock(XSSFWorkbook workbook, String hierarchy) {
		Set <String> selectedHierarchy = Arrays.stream(hierarchy.split(",")).map(String::trim)
				.collect(Collectors.toSet());

		Map <String, List <String>> hierarchyToSheetsMap = Map.of(
				"all", List.of(STOCK_ITEM_SHEET, STOCK_ITEM_CHAR_SHEET));

		Set <String> selectedSheets = new HashSet <> ();
		for (String hier: selectedHierarchy) {
			if (hierarchyToSheetsMap.containsKey(hier)) {
				selectedSheets.addAll(hierarchyToSheetsMap.get(hier));
			}
		}

		if (selectedHierarchy.isEmpty() || selectedHierarchy.contains("all")) {
			selectedSheets.addAll(hierarchyToSheetsMap.get("all"));
		}
		selectedSheets.add(ERROR_SHEET);

		List < String > allSheetNames = List.of(STOCK_ITEM_SHEET, STOCK_ITEM_CHAR_SHEET, ERROR_SHEET);

		for (String sheetName: allSheetNames) {
			if (!selectedSheets.contains(sheetName)) {
				int sheetIndex = workbook.getSheetIndex(sheetName);
				if (sheetIndex != -1) {
					workbook.removeSheetAt(sheetIndex);
				}
			}
		}
	}

	private XSSFWorkbook writeWorkbook(List<ProductOffering> productOfferings, ExportJob export,
									   String hierarchy) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			if (hierarchy.isBlank() || hierarchy.isEmpty()) {
				writeDataToWorkbook(workbook, productOfferings);
			} else {
				writeDataToWorkbookHierarchy(workbook, productOfferings, export);
				implementHierarchy(workbook, hierarchy);

			}
			alterWorkbookFonts(workbook);
		} catch (Exception e) {
			updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
			throw new DiscoManagedException(DISCO_CPO_WORKBOOK_EXCEPTION);
		}
		return workbook;
	}

	private void implementHierarchy(XSSFWorkbook workbook, String hierarchy) {
		Set<String> selectedHierarchy = Arrays.stream(hierarchy.split(",")).map(String::trim)
				.collect(Collectors.toSet());

		Map<String, List<String>> hierarchyToSheetsMap = Map.of(
				"all", List.of(CONTRACT_BUNDLE_SHEET, BUNDLE_CHILD_SHEET, PO_MASTER_SHEET, ATOMIC_PS_SHEET,PO_CHAR_VALUE_SHEET,
						PS_MASTER_SHEET, PS_CHAR_SHEET, PS_CHAR_VALUE_SHEET, POP_MASTER_SHEET, POP_CHARGE_ALT_SHEET,
						PS_CFS_SHEET, CFS_SHEET, STOCK_ITEM_SHEET, CATEGORY_SHEET, ENTITY_REL_SHEET),
				PSR, List.of(CONTRACT_BUNDLE_SHEET, BUNDLE_CHILD_SHEET, PO_MASTER_SHEET,
						ATOMIC_PS_SHEET,PO_CHAR_VALUE_SHEET, PS_MASTER_SHEET, PS_CHAR_SHEET, PS_CHAR_VALUE_SHEET, ENTITY_REL_SHEET),
				"productOfferingPrice",
				List.of(CONTRACT_BUNDLE_SHEET, BUNDLE_CHILD_SHEET, PO_MASTER_SHEET,PO_CHAR_VALUE_SHEET, POP_MASTER_SHEET,
						POP_CHARGE_ALT_SHEET),
				CFSR,
				List.of(CONTRACT_BUNDLE_SHEET, BUNDLE_CHILD_SHEET, PO_MASTER_SHEET, ATOMIC_PS_SHEET,PO_CHAR_VALUE_SHEET, PS_MASTER_SHEET,
						CFS_SHEET),
				STOCK,
				List.of(CONTRACT_BUNDLE_SHEET, BUNDLE_CHILD_SHEET, PO_MASTER_SHEET, ATOMIC_PS_SHEET, PO_CHAR_VALUE_SHEET,PS_MASTER_SHEET,
						STOCK_ITEM_SHEET),
				"category", List.of(CONTRACT_BUNDLE_SHEET, BUNDLE_CHILD_SHEET, PO_MASTER_SHEET, PO_CHAR_VALUE_SHEET,CATEGORY_SHEET));

		Set<String> selectedSheets = new HashSet<>();
		for (String hier : selectedHierarchy) {
			if (hierarchyToSheetsMap.containsKey(hier)) {
				selectedSheets.addAll(hierarchyToSheetsMap.get(hier));
			}
		}

		if (selectedHierarchy.isEmpty() || selectedHierarchy.contains("all")) {
			selectedSheets.addAll(hierarchyToSheetsMap.get("all"));
		}

		List<String> allSheetNames = List.of(CONTRACT_BUNDLE_SHEET, BUNDLE_CHILD_SHEET, PO_MASTER_SHEET,
				ATOMIC_PS_SHEET,PO_CHAR_VALUE_SHEET, PS_MASTER_SHEET, PS_CHAR_SHEET, PS_CHAR_VALUE_SHEET, POP_MASTER_SHEET,
				POP_CHARGE_ALT_SHEET, PS_CFS_SHEET, CFS_SHEET, STOCK_ITEM_SHEET, CATEGORY_SHEET, ENTITY_REL_SHEET);

		for (String sheetName : allSheetNames) {
			if (!selectedSheets.contains(sheetName)) {
				int sheetIndex = workbook.getSheetIndex(sheetName);
				if (sheetIndex != -1) {
					workbook.removeSheetAt(sheetIndex);
				}
			}
		}
	}

	private void writeDataToWorkbook(XSSFWorkbook workbook, List<ProductOffering> contractProductOfferings) {
		Sheet productOfferingSheet = extractedPoMaster(workbook);
		int atomicRowNum = productOfferingSheet.getLastRowNum() + 1;

		for (ProductOffering po : contractProductOfferings) {
			Row productOfferingRow = productOfferingSheet.createRow(atomicRowNum++);
			writeProductOfferingRow(productOfferingRow, po);
		}
	}

	//  10: New method to write PS data without hierarchy
	private void writeDataToWorkbookPS(XSSFWorkbook workbook, List<ProductSpecification> productSpecifications) {
		Sheet psSheet = extractedPs(workbook);
		int psRowNum = psSheet.getLastRowNum() + 1;

		for (ProductSpecification ps : productSpecifications) {
			Row psRow = psSheet.createRow(psRowNum++);
			prepareProductSpecificationRows(psRow, ps);
		}
	}

	// step7c : method to write CFS data without hierarchy
	private void writeDataToWorkbookCFS(XSSFWorkbook workbook, List <ServiceSpecification> serviceSpecifications) {
		Sheet cfsSheet = extractedCfsMaster(workbook);
		int cfsRowNum = cfsSheet.getLastRowNum() + 1;

		for (ServiceSpecification ss: serviceSpecifications) {
			Row cfsRow = cfsSheet.createRow(cfsRowNum++);
			prepareServiceSpecificationRows(cfsRow, ss,null);
		}
	}

	// step7s: method to write StockItem data without hierarchy
	private void writeDataToWorkbookStock(XSSFWorkbook workbook, List <StockItem> stockItems) {
		Sheet stockSheet = extractedStockItemForStock(workbook);
		int stockRowNum = stockSheet.getLastRowNum() + 1;

		for (StockItem stock: stockItems) {
			Row stockRow = stockSheet.createRow(stockRowNum++);
			prepareStockItemRowForStock(stockRow, stock, null);
		}
	}

//	//  11: New method to write PS hierarchy data
	private void writeDataToWorkbookPSHierarchy(XSSFWorkbook workbook, List<ProductSpecification> productSpecifications, ExportJob export) {
		Set<String> productSpecificationsSet = new HashSet<>();
		Set<String> cfss = new HashSet<>();
		Set<String> stockItemMaster = new HashSet<>();
		Set<String> psChars = new HashSet<>();
		Set<String> psCharsVals = new HashSet<>();
		Set<String> entRels = new HashSet<>();

		Sheet psSheet = extractedPsforPS(workbook);
		Sheet psCharSheet = extractedPsCharforPS(workbook);
		Sheet psCharValueSheet = extractedPsCharValforPS(workbook);
		Sheet psCfsSheet = extractedPsCfs(workbook);
		Sheet cfsSheet = extractedCfs(workbook);
		Sheet stockItemSheet = extractedStockItem(workbook);
		Sheet entityRelSheet = extractedEntityRel(workbook);
		Sheet errorSheet = extractedErrorSheetForPS(workbook);

		int psRowNum = psSheet.getLastRowNum() + 1;
		int psCharRowNum = psCharSheet.getLastRowNum() + 1;
		int psCharValRowNum = psCharValueSheet.getLastRowNum() + 1;
		int psCfsRowNum = psCfsSheet.getLastRowNum() + 1;
		int cfsRowNum = cfsSheet.getLastRowNum() + 1;
		int stockItemRowNum = stockItemSheet.getLastRowNum() + 1;
		int entityRelRowNum = entityRelSheet.getLastRowNum() + 1;

		for (ProductSpecification ps : productSpecifications) {
			List<Integer> createdRows = new ArrayList<>();
			try {
				Set<String> cfsIds = new HashSet<>();
				Set<String> stockItemTypeIds = new HashSet<>();

				psRowNum = writePSMaster(ps, psSheet, psRowNum, productSpecificationsSet, createdRows);
				psCharRowNum = writePSCharacteristics(ps, psCharSheet, psCharRowNum, psChars);
				psCharValRowNum = writePSCharacteristicValues(ps, psCharValueSheet, psCharValRowNum, psCharsVals);
				psCfsRowNum = writePSCFS(ps, psCfsSheet, psCfsRowNum, productSpecificationsSet, cfsIds, stockItemTypeIds, createdRows);

				cfsRowNum = writeCFSData(export, cfsIds, cfsSheet, cfsRowNum, cfss, createdRows);
				stockItemRowNum = writeStockItemData(export, stockItemTypeIds, stockItemSheet, stockItemRowNum, stockItemMaster, createdRows);
				entityRelRowNum = writeEntityRelationship(ps, entityRelSheet, entityRelRowNum, entRels, createdRows);

			} catch (Exception e) {
				rollbackRows(psSheet, createdRows);
				rollbackRows(psCharSheet, createdRows);
				rollbackRows(psCharValueSheet, createdRows);
				rollbackRows(psCfsSheet, createdRows);
				rollbackRows(cfsSheet, createdRows);
				rollbackRows(stockItemSheet, createdRows);
				rollbackRows(entityRelSheet, createdRows);
				createErrorSheet(errorSheet, ps.getId(), e.toString());
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				LOGGER.error("Exception occurred for product specification with ID {}: {}", ps.getId(), e.getMessage(), e);
			}
		}
		workbook.setSheetOrder(ERROR_SHEET, workbook.getNumberOfSheets() - 1);
	}

// --- Helper methods ---

	private int writePSMaster(ProductSpecification ps, Sheet psSheet, int psRowNum, Set<String> productSpecificationsSet, List<Integer> createdRows) {
		if (!productSpecificationsSet.contains(ps.getId())) {
			productSpecificationsSet.add(ps.getId());
			Row psRow = psSheet.createRow(psRowNum++);
			prepareProductSpecificationRowsforPS(psRow, ps);
			createdRows.add(psRowNum - 1);
		}
		return psRowNum;
	}

	private int writePSCharacteristics(ProductSpecification ps, Sheet psCharSheet, int psCharRowNum, Set<String> psChars) {
		if (ps.getProductSpecCharacteristic() != null && !psChars.contains(ps.getId())) {
			psChars.add(ps.getId());
			psCharRowNum = prepareProductSpecificationCharacteristicRowsforPS(psCharSheet, ps, psCharRowNum);
		}
		return psCharRowNum;
	}

	private int writePSCharacteristicValues(ProductSpecification ps, Sheet psCharValueSheet, int psCharValRowNum, Set<String> psCharsVals) {
		if (ps.getProductSpecCharacteristic() != null) {
			for (ProductSpecificationCharacteristic psChar : ps.getProductSpecCharacteristic()) {
				if (!psCharsVals.contains(psChar.getId()) && psChar.getProductSpecCharacteristicValue() != null) {
					psCharsVals.add(psChar.getId());
					psCharValRowNum = processProductSpecificationCharacteristicsforPS(psCharValueSheet, psChar, psCharValRowNum);
				}
			}
		}
		return psCharValRowNum;
	}

	private int writePSCFS(ProductSpecification ps, Sheet psCfsSheet, int psCfsRowNum, Set<String> productSpecificationsSet, Set<String> cfsIds, Set<String> stockItemTypeIds, List<Integer> createdRows) {
		if (productSpecificationsSet.contains(ps.getId())) {
			Row psCfsRow = psCfsSheet.createRow(psCfsRowNum++);
			psCfsRow.createCell(0).setCellValue(ps.getId());
			psCfsRow.createCell(1).setCellValue(sanitizeForExcel(ps.getName()));
			psCfsRow.createCell(2).setCellValue(ps.getSupportEntity().getValue());
			if (ps.getSupportEntity().getValue().equals(SupportEntity.CFSSPEC.getValue())) {
				psCfsRow.createCell(3).setCellValue(ps.getServiceSpecification().get(0).getId());
				psCfsRow.createCell(4).setCellValue(sanitizeForExcel(ps.getServiceSpecification().get(0).getName()));
				cfsIds.add(ps.getServiceSpecification().get(0).getId());
			} else {
				psCfsRow.createCell(3).setCellValue(ps.getStockItemType().getId());
				psCfsRow.createCell(4).setCellValue(sanitizeForExcel(ps.getStockItemType().getName()));
				stockItemTypeIds.add(ps.getStockItemType().getId());
			}
			createdRows.add(psCfsRowNum - 1);
		}
		return psCfsRowNum;
	}

	private int writeCFSData(
			ExportJob export,
			Set<String> cfsIds,
			Sheet cfsSheet,
			int cfsRowNum,
			Set<String> cfss,
			List<Integer> createdRows) {

		List<ServiceSpecification> cfsSpecs = fetchCFSAndHandleError(cfsIds, export);
		if (!cfsSpecs.isEmpty()) {
			Set<String> fetchedCfsIds = cfsSpecs.stream()
					.map(ServiceSpecification::getId)
					.collect(Collectors.toSet());
			extracted(export, cfsIds, fetchedCfsIds, DISCO_CFS_NOT_FOUND);

			for (ServiceSpecification cfs : cfsSpecs) {
				if (!cfss.contains(cfs.getId())) {
					cfss.add(cfs.getId());
					cfsRowNum = writeSingleCFSRow(cfs, cfsSheet, cfsRowNum, createdRows);
				}
			}
		}
		return cfsRowNum;
	}

	private int writeSingleCFSRow(ServiceSpecification cfs, Sheet cfsSheet, int cfsRowNum, List<Integer> createdRows) {
		Row cfsRow = cfsSheet.createRow(cfsRowNum++);
		cfsRow.createCell(0).setCellValue(cfs.getId());
		cfsRow.createCell(1).setCellValue(sanitizeForExcel(cfs.getName()));
		cfsRow.createCell(2).setCellValue(sanitizeForExcel(cfs.getDescription()));

		if (cfs.getValidFor() != null) {
			cfsRow.createCell(3).setCellValue(
					cfs.getValidFor().getStartDateTime() != null
							? cfs.getValidFor().getStartDateTime().toString()
							: "");
			cfsRow.createCell(4).setCellValue(
					cfs.getValidFor().getEndDateTime() != null
							? cfs.getValidFor().getEndDateTime().toString()
							: "");
		} else {
			cfsRow.createCell(3).setCellValue("");
			cfsRow.createCell(4).setCellValue("");
		}

		cfsRow.createCell(5).setCellValue(cfs.getType());
		cfsRow.createCell(6).setCellValue(getRelatedResourcesId(cfs));
		cfsRow.createCell(7).setCellValue(sanitizeForExcel(getRelatedResourcesName(cfs)));
		createdRows.add(cfsRowNum - 1);
		return cfsRowNum;
	}


	private int writeStockItemData(ExportJob export, Set<String> stockItemTypeIds, Sheet stockItemSheet, int stockItemRowNum, Set<String> stockItemMaster, List<Integer> createdRows) {
		List<StockItem> stockItems = fetchStockItemsAndHandleError(stockItemTypeIds, export);
		for (StockItem stockItem : stockItems) {
			if (!stockItemMaster.contains(stockItem.getStockItemType().getId())) {
				stockItemMaster.add(stockItem.getStockItemType().getId());
				for (StockItemCharacteristicValue stockItemCharVal : stockItem.getStockItemCharacteristicValue()) {
					Row stockItemRow = stockItemSheet.createRow(stockItemRowNum++);
					prepareStockItemRows(stockItemRow, stockItem, stockItemCharVal);
					createdRows.add(stockItemRowNum - 1);
				}
			}
		}
		return stockItemRowNum;
	}

	private int writeEntityRelationship(ProductSpecification ps, Sheet entityRelSheet, int entityRelRowNum, Set<String> entRels, List<Integer> createdRows) {
		if (ps.getProductSpecificationRelationship() != null && !entRels.contains(ps.getId())) {
			entRels.add(ps.getId());
			for (ProductSpecificationRelationship psRel : ps.getProductSpecificationRelationship()) {
				Row entityRelRow = entityRelSheet.createRow(entityRelRowNum++);
				entityRelRow.createCell(0).setCellValue(ps.getId());
				entityRelRow.createCell(1).setCellValue(ps.getType());
				entityRelRow.createCell(2).setCellValue(sanitizeForExcel(ps.getName()));
				entityRelRow.createCell(3).setCellValue(psRel.getRelationshipType().getValue());
				entityRelRow.createCell(4).setCellValue(psRel.getId());
				String relatedPsId = psRel.getId();
				String relatedPsName = fetchRelatedProductSpecificationName(relatedPsId);
				entityRelRow.createCell(5).setCellValue(sanitizeForExcel(relatedPsName));
				if (psRel.getValidFor() != null) {
					entityRelRow.createCell(6).setCellValue(psRel.getValidFor().getStartDateTime() != null ? psRel.getValidFor().getStartDateTime().toString() : "");
					entityRelRow.createCell(7).setCellValue(psRel.getValidFor().getEndDateTime() != null ? psRel.getValidFor().getEndDateTime().toString() : "");
				} else {
					entityRelRow.createCell(6).setCellValue("");
					entityRelRow.createCell(7).setCellValue("");
				}
				createdRows.add(entityRelRowNum - 1);
			}
		}
		return entityRelRowNum;
	}


	// step8s: method for writing StockItem hierarchy data
	private void writeDataToWorkbookStockHierarchy(XSSFWorkbook workbook, List<StockItem> stockItems, ExportJob export) {
		Set<String> stockSet = new HashSet<>();
		Set<String> stockCharSet = new HashSet<>();
		Sheet stockItemSheet = extractedStockItemForStock(workbook);
		Sheet stockCharSheet = extractedStockItemChar(workbook);
		Sheet errorSheet = extractedErrorSheetForStock(workbook);

		int stockRowNum = stockItemSheet.getLastRowNum() + 1;
		int stockCharRowNum = stockCharSheet.getLastRowNum() + 1;

		for (StockItem stock : stockItems) {
			List<Integer> createdRows = new ArrayList<>();
			try {
				stockRowNum = writeStockRowsForItem(stock, stockItemSheet, stockRowNum, stockSet, createdRows);
				stockCharRowNum = writeStockCharRowsForItem(stock, stockCharSheet, stockCharRowNum, stockCharSet, createdRows);
			} catch (Exception e) {
				rollbackRows(stockItemSheet, createdRows);
				rollbackRows(stockCharSheet, createdRows);
				createErrorSheet(errorSheet, stock.getId(), e.toString());
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				LOGGER.error("Exception occurred for stock item with ID {}: {}", stock.getId(), e.getMessage(), e);
			}
		}
		workbook.setSheetOrder(ERROR_SHEET, workbook.getNumberOfSheets() - 1);
	}

	private int writeStockRowsForItem(StockItem stock, Sheet stockItemSheet, int stockRowNum, Set<String> stockSet, List<Integer> createdRows) {
		if (!stockSet.contains(stock.getId())) {
			stockSet.add(stock.getId());
			List<ProductSpecification> productSpecificationList = fetchRelatedPSForStock(stock);
			if (productSpecificationList.isEmpty()) {
				Row stockRow = stockItemSheet.createRow(stockRowNum++);
				prepareStockItemRowForStock(stockRow, stock, null);
				createdRows.add(stockRowNum - 1);
			} else {
				for (ProductSpecification ps : productSpecificationList) {
					Row stockRow = stockItemSheet.createRow(stockRowNum++);
					prepareStockItemRowForStock(stockRow, stock, ps);
					createdRows.add(stockRowNum - 1);
				}
			}
		}
		return stockRowNum;
	}

	private int writeStockCharRowsForItem(StockItem stock, Sheet stockCharSheet, int stockCharRowNum, Set<String> stockCharSet, List<Integer> createdRows) {
		if (stock.getStockItemCharacteristicValue() != null && !stockCharSet.contains(stock.getId())) {
			stockCharSet.add(stock.getId());
			for (StockItemCharacteristicValue charVal : stock.getStockItemCharacteristicValue()) {
				Row charRow = stockCharSheet.createRow(stockCharRowNum++);
				prepareStockItemCharRow(charRow, stock, charVal);
				createdRows.add(stockCharRowNum - 1);
			}
		}
		return stockCharRowNum;
	}


	// step8c :method to write CFS hierarchy data
	private void writeDataToWorkbookCFSHierarchy(
			XSSFWorkbook workbook,
			List<ServiceSpecification> serviceSpecifications,
			ExportJob export) {

		Set<String> serviceSpecificationsSet = new HashSet<>();
		Set<String> cfsChars = new HashSet<>();
		Set<String> cfsCharsVals = new HashSet<>();
		Set<String> entRels = new HashSet<>();

		Sheet cfsSheet = extractedCfsMaster(workbook);
		Sheet cfsCharSheet = extractedCfsChar(workbook);
		Sheet cfsCharValueSheet = extractedCfsCharVal(workbook);
		Sheet entityRelSheet = extractedEntityRel(workbook);
		Sheet errorSheet = extractedErrorSheetForCFS(workbook);

		int cfsRowNum = cfsSheet.getLastRowNum() + 1;
		int cfsCharRowNum = cfsCharSheet.getLastRowNum() + 1;
		int cfsCharValRowNum = cfsCharValueSheet.getLastRowNum() + 1;
		int entityRelRowNum = entityRelSheet.getLastRowNum() + 1;

		for (ServiceSpecification ss : serviceSpecifications) {
			List<Integer> createdRows = new ArrayList<>();
			try {
				cfsRowNum = writeCFSMasterRows(ss, cfsSheet, cfsRowNum, serviceSpecificationsSet, createdRows);
				cfsCharRowNum = writeCFSCharacteristicRows(ss, cfsCharSheet, cfsCharRowNum, cfsChars);
				cfsCharValRowNum = writeCFSCharacteristicValueRows(ss, cfsCharValueSheet, cfsCharValRowNum, cfsCharsVals);
				entityRelRowNum = writeCFSEntityRelationshipRows(ss, entityRelSheet, entityRelRowNum, entRels, createdRows);
			} catch (Exception e) {
				rollbackRows(cfsSheet, createdRows);
				rollbackRows(cfsCharSheet, createdRows);
				rollbackRows(cfsCharValueSheet, createdRows);
				rollbackRows(entityRelSheet, createdRows);
				createErrorSheet(errorSheet, ss.getId(), e.toString());
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				LOGGER.error("Exception occurred for service specification with ID {}: {}", ss.getId(), e.getMessage(), e);
			}
		}
		workbook.setSheetOrder(ERROR_SHEET, workbook.getNumberOfSheets() - 1);
	}

// --- Helper methods ---

	private int writeCFSMasterRows(ServiceSpecification ss, Sheet cfsSheet, int cfsRowNum, Set<String> serviceSpecificationsSet, List<Integer> createdRows) {
		if (!serviceSpecificationsSet.contains(ss.getId())) {
			serviceSpecificationsSet.add(ss.getId());
			List<ProductSpecification> productSpecificationList = fetchRelatedProductSpecificationByCfsId(ss.getId());
			if (productSpecificationList.isEmpty()) {
				Row cfsRow = cfsSheet.createRow(cfsRowNum++);
				prepareServiceSpecificationRows(cfsRow, ss, null);
				createdRows.add(cfsRowNum - 1);
			} else {
				for (ProductSpecification ps : productSpecificationList) {
					Row cfsRow = cfsSheet.createRow(cfsRowNum++);
					prepareServiceSpecificationRows(cfsRow, ss, ps);
					createdRows.add(cfsRowNum - 1);
				}
			}
		}
		return cfsRowNum;
	}

	private int writeCFSCharacteristicRows(ServiceSpecification ss, Sheet cfsCharSheet, int cfsCharRowNum, Set<String> cfsChars) {
		if (ss.getServiceSpecCharacteristic() != null && !cfsChars.contains(ss.getId())) {
			cfsChars.add(ss.getId());
			cfsCharRowNum = prepareServiceSpecCharacteristicRows(cfsCharSheet, ss, cfsCharRowNum);
		}
		return cfsCharRowNum;
	}

	private int writeCFSCharacteristicValueRows(ServiceSpecification ss, Sheet cfsCharValueSheet, int cfsCharValRowNum, Set<String> cfsCharsVals) {
		if (ss.getServiceSpecCharacteristic() != null) {
			for (CharacteristicSpecification cfsChar : ss.getServiceSpecCharacteristic()) {
				if (!cfsCharsVals.contains(cfsChar.getId()) && cfsChar.getCharacteristicValueSpecification() != null) {
					cfsCharsVals.add(cfsChar.getId());
					cfsCharValRowNum = processServiceSpecCharacteristics(cfsCharValueSheet, cfsChar, cfsCharValRowNum);
				}
			}
		}
		return cfsCharValRowNum;
	}

	private int writeCFSEntityRelationshipRows(ServiceSpecification ss, Sheet entityRelSheet, int entityRelRowNum, Set<String> entRels, List<Integer> createdRows) {
		if (ss.getServiceSpecRelationship() != null && !entRels.contains(ss.getId())) {
			entRels.add(ss.getId());
			for (ServiceSpecRelationship ssRel : ss.getServiceSpecRelationship()) {
				Row entityRelRow = entityRelSheet.createRow(entityRelRowNum++);
				prepareServiceSpecRelationshipRows(entityRelRow, ss, ssRel);
				createdRows.add(entityRelRowNum - 1);
			}
		}
		return entityRelRowNum;
	}

	private String fetchRelatedProductSpecificationName(String relatedPsId) {
		String relatedPsName = "";
		try {
			HashMap<String, Object> requestParams = new HashMap<String, Object>();
			requestParams.put("_id", relatedPsId);
			List<ProductSpecification> relatedPs = productSpecService.fetchProductSpecification(requestParams, null, null, "name");
			if (relatedPs != null && !relatedPs.isEmpty()) {
				relatedPsName = relatedPs.get(0).getName();
			}
		} catch (Exception e) {
			LOGGER.error("Failed to fetch ProductSpecification for id: {}, error: {}", relatedPsId, e.getMessage());
		}
		return relatedPsName;
	}



	private void writeProductOfferingRow(Row row, ProductOffering po) {
		row.createCell(0).setCellValue(po.getId());
		row.createCell(1).setCellValue(sanitizeForExcel(po.getName()));
		row.createCell(2).setCellValue(sanitizeForExcel(po.getDescription()));
		row.createCell(3).setCellValue(sanitizeForExcel(po.getBrand()));
		row.createCell(4).setCellValue(po.getType().getValue());
		row.createCell(5).setCellValue(getNullableValue(po.getIsSellable()));
		row.createCell(6).setCellValue(getNullableValue(po.getIsBundle()));
		row.createCell(7).setCellValue(getNullableValue(po.isIsInstallable()));
		row.createCell(8)
				.setCellValue(getNullableValue(po.getBillingType() != null ? po.getBillingType().getValue() : null));
		row.createCell(9).setCellValue(
				getNullableValue(po.getLifecycleStatus() != null ? po.getLifecycleStatus().getValue() : null));
		writeValidForCells(row, po.getValidFor());
		row.createCell(12).setCellValue(po.getVersion());
		row.createCell(13).setCellValue(getChannelData(po.getChannel()));
		row.createCell(14).setCellValue(getNullableValue(getCommercialDataIds(po.getCommercialOperation())));
		row.createCell(15).setCellValue(getNullableValue(getCommercialDataName(po.getCommercialOperation())));
		row.createCell(16).setCellValue(getNullableValue(getMarketSegment(po.getMarketSegment())));
		row.createCell(17).setCellValue(getNullableValue(getPolicyRuleRefID(po)));
		row.createCell(18).setCellValue(sanitizeForExcel(getNullableValue(getPolicyRuleRefName(po))));
	}

	private void writeValidForCells(Row row, TimePeriod validFor) {
		if (validFor != null) {
			row.createCell(10).setCellValue(getNullableDateTime(validFor.getStartDateTime()));
			row.createCell(11).setCellValue(getNullableDateTime(validFor.getEndDateTime()));
		} else {
			row.createCell(10).setCellValue("");
			row.createCell(11).setCellValue("");
		}
	}

	private String getNullableValue(Object value) {
		return value != null ? value.toString() : "";
	}



	private String getNullableDateTime(OffsetDateTime dateTime) {
		return dateTime != null ? dateTime.toString() : "";
	}

	private void alterWorkbookFonts(XSSFWorkbook workbook) {
		// Enhancing the column width
		int defaultColumnWidth = 20 * 256;
		for (Sheet sheet : workbook) {
			// Assuming we want to set the width for the first 10 columns for example
			for (int colIndex = 0; colIndex < 22; colIndex++) {
				sheet.setColumnWidth(colIndex, defaultColumnWidth);
			}
		}
		// Create a cell style with a background color
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFColor color = new XSSFColor(new java.awt.Color(211, 211, 211), new DefaultIndexedColorMap()); // Yellow
		// color
		cellStyle.setFillForegroundColor(color);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// Create a font and set it to bold
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		cellStyle.setFont(font);

		// Apply the style to the first row of all sheets
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			XSSFRow row = sheet.getRow(0);
			if (row != null) {
				for (int j = 0; j < row.getLastCellNum(); j++) {
					row.getCell(j).setCellStyle(cellStyle);
				}
			}
		}
	}

	/**
	 * Creating Filename for ExportJob.
	 *
	 * @return filename.
	 */
	@Override
	public String getFileName(int sheetSize,ExportJob exportJob) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmm");
		String dateTimeString = now.format(formatter);
		if (exportJob.getPath() != null && exportJob.getPath().contains(PSR)) {
			return "PS_" + (sheetSize > 1 ? HIERARCHY : "") + dateTimeString + EXCEL;
		}
		// step8c :  getFileName to handle CFS exports
		if (exportJob.getPath() != null && exportJob.getPath().contains(CFSR)) {
			return "CFS_" + (sheetSize > 1 ? HIERARCHY : "") + dateTimeString + EXCEL;}

		// step9s : getFileName to handle stockItem exports
		if (exportJob.getPath() != null && exportJob.getPath().contains(STOCK)) {
			return "stockItem_" + (sheetSize > 1 ? HIERARCHY : "") + dateTimeString + EXCEL;
		}
			String fileName = "Contract_" + (sheetSize > 1 ? HIERARCHY : "");
			return fileName + dateTimeString + EXCEL;
		}


		private List<ProductOffering> fetchProductOfferingsByPsId(String psId) {

			Map<String, Object> requestParams = new HashMap<>();
			requestParams.put("productSpecification._id", psId);
			try {
				List<ProductOffering> offerings = getProductOfferings(requestParams, null, null, "id");
				return (offerings != null) ? offerings : Collections.emptyList();
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException("Unsupported encoding while fetching ProductOfferings for psId",e);
			}

		}

	// step11c :  method to fetch related PSid to ServiceSpecification
	private List<ProductSpecification> fetchRelatedProductSpecificationByCfsId(String cfsId) {
		try {
			HashMap <String, Object> requestParams = new HashMap <String,Object> ();
			requestParams.put("serviceSpecification._id", cfsId);
			List <ProductSpecification> relatedPS = productSpecService.fetchProductSpecification(requestParams, null, null, "id,lifecycleStatus");
			if (relatedPS != null) {
				return  relatedPS;
			}
	}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unsupported encoding while fetching productspec for cfsid",e);
		}
		return  Collections.emptyList();
	}


	// step11s: method to fetch related PS for stockItems
	private List <ProductSpecification> fetchRelatedPSForStock(StockItem stock) {
		try {
			String typeId = stock.getStockItemType().getId();
			Map <String,Object> requestParams = new HashMap <> ();
			requestParams.put("stockItemType._id", typeId);
			List <ProductSpecification> relatedPS = productSpecService.fetchProductSpecification(requestParams, null, null, "id,lifecycleStatus");
			if (relatedPS != null) {
				return  relatedPS;
			}
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unsupported encoding while fetching productspecs for stockitemtypeid",e);
		}
		return  Collections.emptyList();
	}


		private List<ProductOffering> getProductOfferings(Map<String, Object> requestParams, Long offset, Long limit,
				String fields) throws UnsupportedEncodingException {
			return productOfferingService.fetchProductOffering(requestParams, offset,
					limit, fields);
		}

		//Method to fetch Product Specifications
		private List<ProductSpecification> getProductSpecifications(Map<String, Object> requestParams, Long offset, Long limit,
				String fields) throws UnsupportedEncodingException {
			return productSpecService.fetchProductSpecification(requestParams, offset, limit, fields);
		}

		// step10c :  method to fetch Service Specifications(cfs)
		private List<ServiceSpecification> getServiceSpecifications(
				Map<String, Object> requestParams
		)  {

			Query query = new Query();

			Object idObject = requestParams.get("_id");
			List<String> ids = new ArrayList<>();

			if (idObject instanceof Collection<?>) {
				for (Object obj : (Collection<?>) idObject) {
					if (obj != null) {
						addIdsFromString(ids, obj.toString());
					}
				}
			} else if (idObject != null && idObject.getClass().isArray()) {
				Object[] array = (Object[]) idObject;
				for (Object obj : array) {
					if (obj != null) {
						addIdsFromString(ids, obj.toString());
					}
				}
			} else if (idObject != null) {
				addIdsFromString(ids, idObject.toString());
			}

			if (!ids.isEmpty()) {
				query.addCriteria(Criteria.where("_id").in(ids));
			}


			return mongoTemplate.find(query, ServiceSpecification.class, CFSR);
		}



	// step10s : method to fetch StockItems
	private List<StockItem> getStockItemss(Map<String, Object> requestParams)  {

		Query query = new Query();

		Object idObject = requestParams.get("_id");
		List<String> ids = new ArrayList<>();

		if (idObject instanceof Collection<?>) {
			for (Object obj : (Collection<?>) idObject) {
				if (obj != null) {
					addIdsFromString(ids, obj.toString());
				}
			}
		} else if (idObject != null && idObject.getClass().isArray()) {
			Object[] array = (Object[]) idObject;
			for (Object obj : array) {
				if (obj != null) {
					addIdsFromString(ids, obj.toString());
				}
			}
		} else if (idObject != null) {
			addIdsFromString(ids, idObject.toString());
		}

		query.addCriteria(Criteria.where("_id").in(ids));

		return mongoTemplate.find(query, StockItem.class, STOCK);
	}

	/**
	 * Utility method to handle comma-separated IDs.
	 */
	private void addIdsFromString(List<String> ids, String value) {
		if (value.contains(",")) {
			for (String id : value.split(",")) {
				if (!id.trim().isEmpty()) {
					ids.add(id.trim());
				}
			}
		} else {
			ids.add(value.trim());
		}
	}




	private String sanitizeForExcel(String value) {
			if (value == null) return "";
			if (value.isEmpty()) return value;
			char first = value.charAt(0);
			if (first == '=' || first == '+' || first == '-' || first == '@' || first == '\t' || first == '\r') {
				return "Restricted Value";
			}
			return value;
		}



		private void writeDataToWorkbookHierarchy(XSSFWorkbook workbook,
				List<? extends ProductOffering> contractProductOfferings, ExportJob export) {

			Set<String> bundleProductOfferings = new HashSet<>();
			Set<String> atomicProductOfferings = new HashSet<>();
			Set<String> popMasters = new HashSet<>();
			Set<String> productSpecifications = new HashSet<>();
			Set<String> cfss = new HashSet<>();
			Set<String> productSpecificationCfss = new HashSet<>();
			Set<String> stockItemMaster = new HashSet<>();
			Set<String> categorys = new HashSet<>();
			Set<String> poMasters = new HashSet<>();
			Set<String> psChars = new HashSet<>();
			Set<String> psCharsVals = new HashSet<>();
			Set<String> popCharAlts = new HashSet<>();
			Set<String> entRels = new HashSet<>();
			Set<String> poCharsVals = new HashSet<>();

			// New collection for all BPOs
			Set<ProductOffering> allBundlePo = new HashSet<>();

			Sheet contractBundleSheet = extractedContract(workbook);
			Sheet bundleChildSheet = extractedBundle(workbook);
			Sheet productOfferingSheet = extractedPoMaster(workbook);
			Sheet atomicPsSheet = extractedAtomicPs(workbook);
			Sheet poCharValueSheet = extractedProductOfferingCharVal(workbook);
			Sheet pSSheet = extractedPs(workbook);
			Sheet pSCharSheet = extractedPsChar(workbook);
			Sheet pSCharValueSheet = extractedPsCharVal(workbook);
			Sheet popSheet = extractedPop(workbook);
			Sheet popChargeAltSheet = extractedPopCharAlt(workbook);
			Sheet pSCFsSheet = extractedPsCfs(workbook);
			Sheet cfsSheet = extractedCfs(workbook);
			Sheet stockItemSheet = extractedStockItem(workbook);
			Sheet entityRelSheet = extractedEntityRel(workbook);
			Sheet categorySheet = extractedCategory(workbook);
			Sheet errorSheet = extractedErrorSheet(workbook);

			int cBRowNum = contractBundleSheet.getLastRowNum() + 1;
			int bARowNum = bundleChildSheet.getLastRowNum() + 1;

			// Write data rows
			for (ProductOffering cpo : contractProductOfferings) {
				// Store initial state
				List<Integer> createdCBRows = new ArrayList<>();
				List<Integer> createdBARows = new ArrayList<>();
				try {
					Set<String> bPoIds = new HashSet<>();
					Set<String> aPoIds = new HashSet<>();
					Set<String> popIds = new HashSet<>();
					Set<String> psIds = new HashSet<>();
					Set<String> cfsIds = new HashSet<>();
					Set<String> stockItemTypeIds = new HashSet<>();
					Set<String> categoryIds = new HashSet<>();
					Set<ProductOffering> poMaster = new HashSet<>();

					//prepare contract-bundle and bundle-atomic sheet
					cBRowNum = prepareContractBundleAndBundleAtomicSheet(contractBundleSheet,cpo,cBRowNum,createdCBRows,aPoIds,bPoIds);

					List<ProductOffering> bundlePo = new ArrayList<>();
					if(!bPoIds.isEmpty()){
						bundlePo = fetchBundleProductOfferingAndHandleError(bPoIds,export);
						allBundlePo.addAll(bundlePo); // Add initial BPOs
						if (!bundlePo.isEmpty()) {
							Set<String> fetchedPoIds = bundlePo.stream().map(ProductOffering::getId)
									.collect(Collectors.toSet());
							extracted(export, bPoIds, fetchedPoIds, DISCO_BPO_NOT_FOUND);
						}

					}
					allBundlePo.addAll(bundlePo); // Add initial BPOs
					if (!bundlePo.isEmpty()) {
						Set<String> fetchedPoIds = bundlePo.stream().map(ProductOffering::getId)
								.collect(Collectors.toSet());
						extracted(export, bPoIds, fetchedPoIds, DISCO_BPO_NOT_FOUND);
					}

					// Write Bundle-Atomic data rows
					// Write BundlePO-Child PO data rows
					bARowNum = writeBundleChildDataRows(bundlePo, bundleChildSheet, bARowNum, createdBARows, aPoIds, bundleProductOfferings, allBundlePo);

					List<ProductOffering> atomicPo = fetchAtomicPOAndHandleError(aPoIds,export);
					if (!atomicPo.isEmpty()) {
						Set<String> fetchedPoIds = atomicPo.stream().map(ProductOffering::getId)
								.collect(Collectors.toSet());
						extracted(export, aPoIds, fetchedPoIds, DISCO_APO_NOT_FOUND);
					}

					poMaster.addAll(atomicPo);
					poMaster.add(cpo);
					poMaster.addAll(allBundlePo);

					//Write PO Characteristic and Characteristic values Data Rows
					writeProductOfferingCharacteristicsValues(atomicPo,poCharsVals,poCharValueSheet);

					// Write Po Master data rows
					writePOMasterDataRows(poMaster,productOfferingSheet,poMasters,productOfferingSheet.getLastRowNum() + 1,categoryIds,popIds);

					// Write Atomic-Ps data rows
					writeAtomicPSDataRows(atomicPsSheet,atomicPo,atomicProductOfferings,psIds,atomicPsSheet.getLastRowNum() + 1);

					List<ProductSpecification> prodSpecs = fetchProductSpecificationsAndHandleError(psIds,export);
					if (!prodSpecs.isEmpty()) {
						Set<String> fetchedPsIds = prodSpecs.stream().map(ProductSpecification::getId)
								.collect(Collectors.toSet());
						extracted(export, psIds, fetchedPsIds, DISCO_PS_NOT_FOUND);
					}

					// Write Ps data rows
					writePSDataRows(prodSpecs,productSpecifications,pSSheet,pSSheet.getLastRowNum() + 1);

					// Write Ps char data rows
					writePSCharDataRows(pSCharSheet,prodSpecs,psChars,pSCharSheet.getLastRowNum() + 1);

					// Write Ps char Value data rows
					writePSCharValuesDataRows(pSCharValueSheet,prodSpecs,psCharsVals,pSCharValueSheet.getLastRowNum() + 1);

					if (!popIds.isEmpty()) {
						// No POPs to fetch, so skip fetching and exporting POPs


						List<ProductOfferingPrice> pops = fetchPOPAndHandleError(popIds, export);
						// Write PoP Master data rows
						writePOPMasterDataRows(popSheet, poMaster, popMasters, pops, popSheet.getLastRowNum() + 1);

						// Write PoP Char-Alt data rows
						writePOPChargeAlterationDataRows(popChargeAltSheet, pops, popCharAlts, popChargeAltSheet.getLastRowNum() + 1);
					}
					// Write Product Specification-Customer Facing Specification data rows
					writePSCFSDataRows(pSCFsSheet,productSpecificationCfss,prodSpecs,cfsIds,stockItemTypeIds,pSCFsSheet.getLastRowNum() + 1);

					List<ServiceSpecification> cfsSpecs = fetchCFSAndHandleError(cfsIds,export);
					if (!cfsSpecs.isEmpty()) {
						Set<String> fetchedCfsIds = cfsSpecs.stream().map(ServiceSpecification::getId)
								.collect(Collectors.toSet());
						extracted(export, cfsIds, fetchedCfsIds, DISCO_CFS_NOT_FOUND);
					}

					// Write CFS data rows
					writeCFSDataRows(cfsSheet,cfsSpecs,cfss,cfsSheet.getLastRowNum() + 1);
					List<StockItem> stockItems = fetchStockItemsAndHandleError(stockItemTypeIds,export);

					// Write stockItem data rows
					writeStockItemDataRows(stockItemSheet,stockItems,stockItemMaster,stockItemSheet.getLastRowNum() + 1);

					// Write Entity Rel data rows
					writeEntityRelDataRows(entityRelSheet,poMaster,entRels,entityRelSheet.getLastRowNum() + 1);

					//Category Sheet Filling
					List<Category> categories = fetchCategoryAndHandleError(categoryIds,export);
					if (!categories.isEmpty()) {
						Set<String> fetchedCategoryItemIds = categories.stream().map(Category::getId)
								.collect(Collectors.toSet());
						extracted(export, categoryIds, fetchedCategoryItemIds, DISCO_CATEGORY_NOT_FOUND);
					}
					// Write Category data rows
					writeCategoryDataRows(categorySheet,poMaster,categorys,categories,categorySheet.getLastRowNum() + 1);

				} catch (Exception e) {
					// Rollback logic
					rollbackRows(contractBundleSheet, createdCBRows);
					rollbackRows(bundleChildSheet, createdBARows);
					createErrorSheet(errorSheet, cpo.getId(), e.toString());
					updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
					LOGGER.error("Exception occurred for product offering with ID {}: {}", cpo.getId(), e.getMessage(), e);
				}
			}
			workbook.setSheetOrder(ERROR_SHEET, workbook.getNumberOfSheets() - 1);

		}

		private List<ProductOffering> fetchBundleProductOfferingAndHandleError(Set<String> bPoIds, ExportJob export) {
			try {
				return getProductOfferingsData(bPoIds);
			} catch (Exception e) {
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				throw new DiscoManagedClientException(DISCO_BPO_NOT_FOUND, "", bPoIds.toString());
			}
		}

		private List<ProductOffering> fetchAtomicPOAndHandleError(Set<String> aPoIds, ExportJob export) {
			try {
				return getProductOfferingsData(aPoIds);
			} catch (Exception e) {
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				throw new DiscoManagedClientException(DISCO_APO_NOT_FOUND);
			}
		}

		private List<ProductSpecification> fetchProductSpecificationsAndHandleError(Set<String> psIds, ExportJob export) {
			try {
				return getProductSpecs(psIds);
			} catch (Exception e) {
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				throw new DiscoManagedClientException(DISCO_PS_NOT_FOUND);
			}
		}

		private List<ProductOfferingPrice> fetchPOPAndHandleError(Set<String> popIds, ExportJob export) {
			try {
				return getProductOfferingPrices(popIds);
			} catch (Exception e) {
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				throw new DiscoManagedClientException(DISCO_POP_NOT_FOUND);
			}
		}

		private List<ServiceSpecification> fetchCFSAndHandleError(Set<String> cfsIds, ExportJob export) {
			try {
				return getCFSSpecs(cfsIds);
			} catch (Exception e) {
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				throw new DiscoManagedClientException(DISCO_CFS_NOT_FOUND);
			}
		}

		private List<StockItem> fetchStockItemsAndHandleError(Set<String> stockItemTypeIds, ExportJob export) {
			try {
				return getStockitems(stockItemTypeIds);
			} catch (Exception e) {
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				throw new DiscoManagedClientException(DISCO_STOCKITEM_NOT_FOUND);
			}
		}

		private List<Category> fetchCategoryAndHandleError(Set<String> categoryIds, ExportJob export) {
			try {
				return getCategories(categoryIds);
			} catch (Exception e) {
				updateExportJob(export.getId(), JobStateType.FAILED, e.getMessage());
				throw new DiscoManagedClientException(DISCO_CATEGORY_NOT_FOUND);
			}
		}




		private void writeCategoryDataRows(Sheet categorySheet, Set<ProductOffering> poMaster, Set<String> categorys, List<Category> categories, int categoryRowNum) {
			for (ProductOffering po : poMaster) {
				if (categorys.contains(po.getId()) || po.getCategory() == null) {
					continue;
				}
				categorys.add(po.getId());
				for (CategoryRef categoryRef : po.getCategory()) {
					if (categoryRef != null) {
						Category category = getCategory(categoryRef.getId(), categories);
						if (category != null) {
							Row categoryRow = categorySheet.createRow(categoryRowNum++);
							populateCategoryDataRows(categoryRow, po, category, categoryRef);
						}
					}
				}
			}
		}

		private void populateCategoryDataRows(Row categoryRow, ProductOffering po, Category category, CategoryRef categoryRef) {
			categoryRow.createCell(0).setCellValue(po.getId());
			categoryRow.createCell(1).setCellValue(sanitizeForExcel(po.getName()));
			categoryRow.createCell(2).setCellValue(categoryRef.getId());
			categoryRow.createCell(3).setCellValue(sanitizeForExcel(categoryRef.getName()));
			categoryRow.createCell(4).setCellValue(category.isIsRoot());
			categoryRow.createCell(5).setCellValue(category.getLastUpdate().toString());
			categoryRow.createCell(6).setCellValue(category.getLifecycleStatus());
			if (category.getValidFor() != null) {
				categoryRow.createCell(7)
						.setCellValue(category.getValidFor().getStartDateTime() != null
								? category.getValidFor().getStartDateTime().toString()
								: "");
				categoryRow.createCell(8)
						.setCellValue(category.getValidFor().getEndDateTime() != null
								? category.getValidFor().getEndDateTime().toString()
								: "");
			} else {
				categoryRow.createCell(7).setCellValue("");
				categoryRow.createCell(8).setCellValue("");
			}
		}

		private void writeEntityRelDataRows(Sheet entityRelSheet, Set<ProductOffering> poMaster, Set<String> entRels, int entityRelRowNum) {
			for (ProductOffering po : poMaster) {
				if (!entRels.contains(po.getId()) && po.getProductOfferingRelationship() != null) {
					entRels.add(po.getId());
					for (ProductOfferingRelationship poR : po.getProductOfferingRelationship()) {
						Row entityRelRow = entityRelSheet.createRow(entityRelRowNum++);
						prepareEntityRelDataRows(entityRelRow,po,poR,poMaster);
					}
				}
			}
		}

		private void prepareEntityRelDataRows(Row entityRelRow, ProductOffering po, ProductOfferingRelationship poR, Set<ProductOffering> poMaster) {
			entityRelRow.createCell(0).setCellValue(po.getId());
			entityRelRow.createCell(1).setCellValue(po.getType().getValue());
			entityRelRow.createCell(2).setCellValue(sanitizeForExcel(po.getName()));
			entityRelRow.createCell(3).setCellValue(poR.getRelationshipType().getValue());
			entityRelRow.createCell(4).setCellValue(poR.getId());
			entityRelRow.createCell(5).setCellValue(sanitizeForExcel(getPORelationshipName(poR.getId(), poMaster)));
			if (poR.getValidFor() != null) {
				entityRelRow.createCell(6)
						.setCellValue(poR.getValidFor().getStartDateTime() != null
								? poR.getValidFor().getStartDateTime().toString()
								: "");
				entityRelRow.createCell(7)
						.setCellValue(poR.getValidFor().getEndDateTime() != null
								? poR.getValidFor().getEndDateTime().toString()
								: "");
			} else {
				entityRelRow.createCell(6).setCellValue("");
				entityRelRow.createCell(7).setCellValue("");
			}
		}

		private void writeStockItemDataRows(Sheet stockItemSheet, List<StockItem> stockItems, Set<String> stockItemMaster, int stockItemRowNum) {
			for (StockItem stockItem : stockItems) {
				if (!stockItemMaster.contains(stockItem.getStockItemType().getId())) {
					stockItemMaster.add(stockItem.getStockItemType().getId());
					List<StockItemCharacteristicValue> values = stockItem.getStockItemCharacteristicValue();
					if (values != null && !values.isEmpty()){
						for (StockItemCharacteristicValue stockItemCharVal : stockItem
							.getStockItemCharacteristicValue()) {
						Row stockItemRow = stockItemSheet.createRow(stockItemRowNum++);
						prepareStockItemRows(stockItemRow,stockItem,stockItemCharVal);
					         }
					}
					else {
						Row stockItemRow = stockItemSheet.createRow(stockItemRowNum++);
						prepareStockItemRows(stockItemRow, stockItem, null);
					}
				}
			}
		}

		private void prepareStockItemRows(Row stockItemRow, StockItem stockItem, StockItemCharacteristicValue stockItemCharVal) {
			stockItemRow.createCell(0).setCellValue(stockItem.getStockItemType().getId());
			stockItemRow.createCell(1).setCellValue(stockItem.getId());
			stockItemRow.createCell(2).setCellValue(sanitizeForExcel(stockItem.getName()));
			stockItemRow.createCell(3).setCellValue(sanitizeForExcel(stockItem.getDescription()));
			stockItemRow.createCell(4).setCellValue(sanitizeForExcel(stockItem.getEAN()));
			stockItemRow.createCell(5).setCellValue(sanitizeForExcel(stockItem.getGTIN()));
			stockItemRow.createCell(6).setCellValue(stockItem.getLastUpdate().toString());
			if (stockItemCharVal != null) {
				stockItemRow.createCell(7)
						.setCellValue(stockItemCharVal.getStockItemCharacteristic() != null
								? sanitizeForExcel(stockItemCharVal.getStockItemCharacteristic().getName())
								: "");
				stockItemRow.createCell(8).setCellValue(
						stockItemCharVal.getValue() != null ? stockItemCharVal.getValue() : ""
				);
			} else {
				stockItemRow.createCell(7).setCellValue("");
				stockItemRow.createCell(8).setCellValue("");
			}

			if (stockItem.getValidFor() != null) {
				stockItemRow.createCell(9)
						.setCellValue(stockItem.getValidFor().getStartDateTime() != null
								? stockItem.getValidFor().getStartDateTime().toString()
								: "");
				stockItemRow.createCell(10)
						.setCellValue(stockItem.getValidFor().getEndDateTime() != null
								? stockItem.getValidFor().getEndDateTime().toString()
								: "");
			} else {
				stockItemRow.createCell(9).setCellValue("");
				stockItemRow.createCell(10).setCellValue("");
			}
		}


	// step12s:  method for stockItem row in standalone export
	private void prepareStockItemRowForStock(Row stockRow, StockItem stock, ProductSpecification ps) {
		stockRow.createCell(0).setCellValue(stock.getStockItemType().getId());
		stockRow.createCell(1).setCellValue(stock.getId());
		stockRow.createCell(2).setCellValue(sanitizeForExcel(stock.getName()));
		if(ps == null){
			stockRow.createCell(3).setCellValue("");
			stockRow.createCell(4).setCellValue("");
		}else{
		stockRow.createCell(3).setCellValue(ps.getId());
		stockRow.createCell(4).setCellValue(ps.getLifecycleStatus().getValue());
		}
		stockRow.createCell(5).setCellValue(sanitizeForExcel(stock.getDescription()));
		stockRow.createCell(6).setCellValue(sanitizeForExcel(stock.getEAN()));
		stockRow.createCell(7).setCellValue(sanitizeForExcel(stock.getGTIN()));
		stockRow.createCell(8).setCellValue(stock.getLastUpdate().toString());
		if (stock.getValidFor() != null) {
			stockRow.createCell(9).setCellValue(stock.getValidFor().getStartDateTime() != null ? stock.getValidFor().getStartDateTime().toString() : "");
			stockRow.createCell(10).setCellValue(stock.getValidFor().getEndDateTime() != null ? stock.getValidFor().getEndDateTime().toString() : "");
		} else {
			stockRow.createCell(9).setCellValue("");
			stockRow.createCell(10).setCellValue("");
		}
	}

	// step13s: method for stockItem characteristic row
	private void prepareStockItemCharRow(Row charRow, StockItem stock, StockItemCharacteristicValue charVal) {
		charRow.createCell(0).setCellValue(stock.getId());
		charRow.createCell(1).setCellValue(sanitizeForExcel(stock.getName()));
		charRow.createCell(2).setCellValue(charVal.getStockItemCharacteristic().getId());
		charRow.createCell(3).setCellValue(sanitizeForExcel(charVal.getStockItemCharacteristic().getName()));
		charRow.createCell(4).setCellValue(charVal.getValue());
	}


		private void writeCFSDataRows(Sheet cfsSheet, List<ServiceSpecification> cfsSpecs, Set<String> cfss, int cfsRowNum) {
			for (ServiceSpecification cfs : cfsSpecs) {
				if (!cfss.contains(cfs.getId())) {
					cfss.add(cfs.getId());
					Row cfsRow = cfsSheet.createRow(cfsRowNum++);
					cfsRow.createCell(0).setCellValue(cfs.getId());
					cfsRow.createCell(1).setCellValue(sanitizeForExcel(cfs.getName()));
					cfsRow.createCell(2).setCellValue(sanitizeForExcel(cfs.getDescription()));
					if (cfs.getValidFor() != null) {
						cfsRow.createCell(3)
								.setCellValue(cfs.getValidFor().getStartDateTime() != null
										? cfs.getValidFor().getStartDateTime().toString()
										: "");
						cfsRow.createCell(4)
								.setCellValue(cfs.getValidFor().getEndDateTime() != null
										? cfs.getValidFor().getEndDateTime().toString()
										: "");
					} else {
						cfsRow.createCell(3).setCellValue("");
						cfsRow.createCell(4).setCellValue("");
					}
					cfsRow.createCell(5).setCellValue(cfs.getType());
					cfsRow.createCell(6).setCellValue(getRelatedResourcesId(cfs));
					cfsRow.createCell(7).setCellValue(sanitizeForExcel(getRelatedResourcesName(cfs)));

				}
			}
		}

		private void writePSCFSDataRows(Sheet pSCFsSheet, Set<String> productSpecificationCfss, List<ProductSpecification> prodSpecs, Set<String> cfsIds, Set<String> stockItemTypeIds, int psCfsRowNum) {
			for (ProductSpecification ps : prodSpecs) {
				if (!productSpecificationCfss.contains(ps.getId())) {
					productSpecificationCfss.add(ps.getId());
					Row psCfsRow = pSCFsSheet.createRow(psCfsRowNum++);
					psCfsRow.createCell(0).setCellValue(ps.getId());
					psCfsRow.createCell(1).setCellValue(sanitizeForExcel(ps.getName()));
					psCfsRow.createCell(2).setCellValue(ps.getSupportEntity().getValue());
					if (ps.getSupportEntity().getValue().equals(SupportEntity.CFSSPEC.getValue())) {
						psCfsRow.createCell(3).setCellValue(ps.getServiceSpecification().get(0).getId());
						psCfsRow.createCell(4).setCellValue(sanitizeForExcel(ps.getServiceSpecification().get(0).getName()));
						cfsIds.add(ps.getServiceSpecification().get(0).getId());
					} else {
						psCfsRow.createCell(3).setCellValue(ps.getStockItemType().getId());
						psCfsRow.createCell(4).setCellValue(sanitizeForExcel(ps.getStockItemType().getName()));
						stockItemTypeIds.add(ps.getStockItemType().getId());
					}
				}
			}
		}

		private void writePOPChargeAlterationDataRows(Sheet popChargeAltSheet, List<ProductOfferingPrice> pops, Set<String> popCharAlts, int popChargeAltRowNum) {
			for (ProductOfferingPrice pop : pops) {
				// Only process subclass that contains popRelationship
				List<ProductOfferingPriceRelationship> popRelationships=null;
				if(pop instanceof ProductOfferingPriceCharge poc &&
						!popCharAlts.contains(poc.getId()) &&
						poc.getPopRelationship() != null){
					popCharAlts.add(pop.getId());
					popRelationships=poc.getPopRelationship();
				}
				else if (pop instanceof InstallmentCharge ic &&
						!popCharAlts.contains(ic.getId()) &&
						ic.getPopRelationship() != null){
					popCharAlts.add(pop.getId());
					popRelationships=ic.getPopRelationship();
				}

				if(popRelationships!=null){
						for (ProductOfferingPriceRelationship popRelationship : popRelationships) {
							Row popChargeAltRow = popChargeAltSheet.createRow(popChargeAltRowNum++);
							popChargeAltRow.createCell(0).setCellValue(pop.getId());
							popChargeAltRow.createCell(1).setCellValue(sanitizeForExcel(pop.getName()));
							popChargeAltRow.createCell(2).setCellValue(popRelationship.getRelationshipType().getValue());
							popChargeAltRow.createCell(3).setCellValue(popRelationship.getId());
							popChargeAltRow.createCell(4).setCellValue(popRelationship.getType());
							popChargeAltRow.createCell(5).setCellValue(sanitizeForExcel(popRelationship.getName()));
						}
				}


			}
		}

		private void writePOPMasterDataRows(Sheet popSheet, Set<ProductOffering> poMaster, Set<String> popMasters, List<ProductOfferingPrice> pops, int popRowNum) {
			for (ProductOffering po : poMaster) {
				if (popMasters.contains(po.getId())) {
					continue;
				}
				popMasters.add(po.getId());
				for(ProductOfferingTerm term : po.getProductOfferingTerm()){
					if(term.getCommercialOperation() != null){
						for (CommercialOperation co : term.getCommercialOperation()) {
							if (co.getCarries() != null) {
								popRowNum = processCarriesForPrice(popSheet,co,po,pops,popRowNum,term);
							}
						}

					}
				}
				for (CommercialOperation co : po.getCommercialOperation()) {
					if (co.getCarries() != null) {
						popRowNum = processCarriesForPrice(popSheet,co,po,pops,popRowNum, null);
					}
				}
			}
		}

		private int processCarriesForPrice(Sheet popSheet, CommercialOperation co, ProductOffering po, List<ProductOfferingPrice> pops, int popRowNum, ProductOfferingTerm term) {
			for (ProductOfferingPriceRef pop : co.getCarries()) {
				if (pop != null) {
					ProductOfferingPrice popData = getProductOfferingPriceData(pop.getId(), pops);
					if (popData != null) {
						Row popRow = popSheet.createRow(popRowNum++);
						prepareProductOfferingPrice(popRow, popData, po, pop, co,term);
					}
				}
			}
			return popRowNum;
		}

	private void prepareProductOfferingPrice(
			Row row,
			ProductOfferingPrice popData,
			ProductOffering po,
			ProductOfferingPriceRef pop,
			CommercialOperation co,
			ProductOfferingTerm term) {

		initializeCommonCells(row, popData, po, pop, co, term);

		if (popData instanceof ProductOfferingPriceCharge charge) {
			applyChargeSpecificValues(row, charge);
		}
		else if (popData instanceof ProductOfferingPriceAlteration alteration) {
			applyAlterationSpecificValues(row, alteration);
		}
		else if (popData instanceof TaxProductOfferingPriceAlteration tax) {
			applyTaxSpecificValues(row, tax);
		}
		else if (popData instanceof InstallmentCharge installment) {
			applyInstallmentSpecificValues(row, installment);
		}
	}


	private void initializeCommonCells(Row row,
									   ProductOfferingPrice popData,
									   ProductOffering po,
									   ProductOfferingPriceRef pop,
									   CommercialOperation co,
									   ProductOfferingTerm term) {

		// Product info
		setCellValueSafely(row, 0, po.getId());
		setCellValueSafely(row, 1, sanitizeForExcel(po.getName()));

		// Term info
		String duration = "NA";
		String units = "NA";

		if (term != null && term.getDuration() != null) {
			duration = term.getDuration().getAmount() != null
					? term.getDuration().getAmount().toString()
					: "NA";

			units = term.getDuration().getUnits() != null
					? term.getDuration().getUnits()
					: "NA";
		}

		setCellValueSafely(row, 2, sanitizeForExcel(getNullableValue(duration)));
		setCellValueSafely(row, 3, sanitizeForExcel(getNullableValue(units)));

		// Commercial info
		setCellValueSafely(row, 4, sanitizeForExcel(co.getName()));
		setCellValueSafely(row, 5, pop.getId());
		setCellValueSafely(row, 6, sanitizeForExcel(pop.getName()));

		// Lifecycle
		setCellValueSafely(row, 7, popData.getLifecycleStatus().getValue());

		// Initialize type-specific columns
		int[] typeSpecificColumns = {8, 9, 10, 14, 18, 19, 20, 23, 24, 25, 26, 27, 28};
		for (int col : typeSpecificColumns) {
			setCellValueSafely(row, col, "");
		}

		// Common price info
		setValidForCellValueForProductOfferingPrice(popData, row);

		setCellValueSafely(row, 13, popData.getVersion());
		setCellValueSafely(row, 15, popData.getType());

		setCellValueSafely(row, 16,
				popData.getPrice() != null
						? popData.getPrice().getValue().toString()
						: "");

		setCellValueSafely(row, 17,
				popData.getPrice() != null
						? popData.getPrice().getUnit()
						: "");

		setUnitOfMeasureCellForProductOfferingPrice(popData, row);
	}


	private void applyChargeSpecificValues(Row row, ProductOfferingPriceCharge charge) {

		if (charge.getPriceType() != null)
			setCellValueSafely(row, 8, charge.getPriceType().getValue());

		if (charge.getProrationType() != null)
			setCellValueSafely(row, 9, charge.getProrationType().getValue());

		if (charge.getChargeCycle() != null)
			setCellValueSafely(row, 10, charge.getChargeCycle().getValue());

		if (charge.getImmediatePayment() != null)
			setCellValueSafely(row, 14, charge.getImmediatePayment().toString());

		if (charge.getRecurringChargePeriodLength() != null)
			setCellValueSafely(row, 19, charge.getRecurringChargePeriodLength().toString());

		if (charge.getRecurringChargePeriodType() != null)
			setCellValueSafely(row, 20, charge.getRecurringChargePeriodType());
	}


	private void applyAlterationSpecificValues(Row row, ProductOfferingPriceAlteration alteration) {

		if (alteration.getPriceType() != null)
			setCellValueSafely(row, 8, alteration.getPriceType().getValue());

		if (alteration.getProrationType() != null)
			setCellValueSafely(row, 9, alteration.getProrationType().getValue());

		if (alteration.getApplicationDuration() != null) {

			if (alteration.getApplicationDuration().getAmount() != null)
				setCellValueSafely(row, 23,
						alteration.getApplicationDuration().getAmount().toString());

			if (alteration.getApplicationDuration().getUnits() != null)
				setCellValueSafely(row, 24,
						alteration.getApplicationDuration().getUnits());
		}
	}


	private void applyTaxSpecificValues(Row row, TaxProductOfferingPriceAlteration tax) {

		if (tax.getPercentage() != null)
			setCellValueSafely(row, 18, tax.getPercentage().toString());
	}


	private void applyInstallmentSpecificValues(Row row, InstallmentCharge installment) {

		if (installment.getApplicationDuration() != null) {

			if (installment.getApplicationDuration().getAmount() != null)
				setCellValueSafely(row, 23,
						installment.getApplicationDuration().getAmount().toString());

			if (installment.getApplicationDuration().getUnits() != null)
				setCellValueSafely(row, 24,
						installment.getApplicationDuration().getUnits());
		}

		if (installment.getDownPayment() != null)
			setCellValueSafely(row, 25, installment.getDownPayment().toString());

		if (installment.getPartner() != null)
			setCellValueSafely(row, 26, installment.getPartner());

		if (installment.getInterestRate() != null)
			setCellValueSafely(row, 27, installment.getInterestRate().toString());

		if (installment.getExternalId() != null)
			setCellValueSafely(row, 28, installment.getExternalId());
	}


	private void setCellValueSafely(Row row, int columnIndex, Object value) {

		Cell cell = row.getCell(columnIndex);

		if (cell == null) {
			cell = row.createCell(columnIndex);
		}

		cell.setCellValue(value != null ? value.toString() : "");
	}

		private void setValidForCellValueForProductOfferingPrice(ProductOfferingPrice popData, Row popRow) {
			if (popData.getValidFor() != null) {
				popRow.createCell(9)
						.setCellValue(popData.getValidFor().getStartDateTime() != null
								? popData.getValidFor().getStartDateTime().toString()
								: "");
				popRow.createCell(10)
						.setCellValue(popData.getValidFor().getEndDateTime() != null
								? popData.getValidFor().getEndDateTime().toString()
								: "");
			} else {
				popRow.createCell(9).setCellValue("");
				popRow.createCell(10).setCellValue("");
			}
		}

		private void setUnitOfMeasureCellForProductOfferingPrice(ProductOfferingPrice popData, Row popRow) {
			Quantity unitOfMeasure = null;

		if (popData instanceof ProductOfferingPriceAlteration alteration) {
			unitOfMeasure = alteration.getUnitOfMeasure();
			}else if (popData instanceof TaxProductOfferingPriceAlteration tax) {
			unitOfMeasure = tax.getUnitOfMeasure();

			}

			if (unitOfMeasure != null) {
				popRow.createCell(21)
						.setCellValue(unitOfMeasure.getAmount() != null
								? unitOfMeasure.getAmount().toString()
								: "");
				popRow.createCell(22)
						.setCellValue(unitOfMeasure.getUnits() != null
								? unitOfMeasure.getUnits()
								: "");
			} else {
				popRow.createCell(21).setCellValue("");
				popRow.createCell(22).setCellValue("");
			}
		}

		private void writePSCharValuesDataRows(Sheet pSCharValueSheet, List<ProductSpecification> prodSpecs, Set<String> psCharsVals, int prodSpecCharValRowNum) {
			for (ProductSpecification ps : prodSpecs) {
				if (ps.getProductSpecCharacteristic() == null) {
					continue;
				}
				for (ProductSpecificationCharacteristic psChar : ps.getProductSpecCharacteristic()) {
					if (!psCharsVals.contains(psChar.getId()) && psChar.getProductSpecCharacteristicValue() != null) {
						psCharsVals.add(psChar.getId());
						prodSpecCharValRowNum = processProductSpecificationCharacteristics(pSCharValueSheet,psChar,prodSpecCharValRowNum);
					}
				}
			}
		}

		private int processProductSpecificationCharacteristics(Sheet pSCharValueSheet, ProductSpecificationCharacteristic psChar, int prodSpecCharValRowNum) {
			for (ProductSpecificationCharacteristicValue psCharVal : psChar.getProductSpecCharacteristicValue()) {
				if (psCharVal != null) {
					Row prodSpecCharValRow = pSCharValueSheet.createRow(prodSpecCharValRowNum++);
					prepareProductSpecCharValRows(prodSpecCharValRow,psChar,psCharVal);
				}
			}
			return prodSpecCharValRowNum;
		}

		private void prepareProductSpecCharValRows(Row prodSpecCharValRow, ProductSpecificationCharacteristic psChar, ProductSpecificationCharacteristicValue psCharVal) {
			prodSpecCharValRow.createCell(0).setCellValue(psChar.getId());
			prodSpecCharValRow.createCell(1).setCellValue(sanitizeForExcel(psChar.getName()));
			prodSpecCharValRow.createCell(2).setCellValue(psCharVal.getValue());
			prodSpecCharValRow.createCell(3)
					.setCellValue(psCharVal.isIsDefault() != null
							? psCharVal.isIsDefault().toString()
							: "");
			prodSpecCharValRow.createCell(4).setCellValue(psCharVal.getValueFrom());
			prodSpecCharValRow.createCell(5).setCellValue(psCharVal.getValueTo());
			prodSpecCharValRow.createCell(6).setCellValue(psCharVal.getUnitOfMeasure());
			if (psCharVal.getValidFor() != null) {
				prodSpecCharValRow.createCell(7)
						.setCellValue(psCharVal.getValidFor().getStartDateTime() != null
								? psCharVal.getValidFor().getStartDateTime().toString()
								: "");
				prodSpecCharValRow.createCell(8)
						.setCellValue(psCharVal.getValidFor().getEndDateTime() != null
								? psCharVal.getValidFor().getEndDateTime().toString()
								: "");
			} else {
				prodSpecCharValRow.createCell(7).setCellValue("");
				prodSpecCharValRow.createCell(8).setCellValue("");
			}
			prodSpecCharValRow.createCell(9).setCellValue(psCharVal.getAddressId() != null ? psCharVal.getAddressId() : "");
			prodSpecCharValRow.createCell(10).setCellValue(psCharVal.getSubUnitNumber() != null ? psCharVal.getSubUnitNumber() : "");
			prodSpecCharValRow.createCell(11).setCellValue(psCharVal.getStreetName() != null ? psCharVal.getStreetName() : "");
			prodSpecCharValRow.createCell(12).setCellValue(psCharVal.getPostcode() != null ? psCharVal.getPostcode() : "");
			prodSpecCharValRow.createCell(13).setCellValue(psCharVal.getCity() != null ? psCharVal.getCity() : "");
			prodSpecCharValRow.createCell(14).setCellValue(psCharVal.getCountry() != null ? psCharVal.getCountry() : "");
		}

		//for ps export
		private int processProductSpecificationCharacteristicsforPS(Sheet pSCharValueSheet, ProductSpecificationCharacteristic psChar, int prodSpecCharValRowNum) {
			for (ProductSpecificationCharacteristicValue psCharVal : psChar.getProductSpecCharacteristicValue()) {
				if (psCharVal != null) {
					Row prodSpecCharValRow = pSCharValueSheet.createRow(prodSpecCharValRowNum++);
					prepareProductSpecCharValRowsforPS(prodSpecCharValRow,psChar,psCharVal);
				}
			}
			return prodSpecCharValRowNum;
		}

		private void prepareProductSpecCharValRowsforPS(Row prodSpecCharValRow, ProductSpecificationCharacteristic psChar, ProductSpecificationCharacteristicValue psCharVal) {
			prodSpecCharValRow.createCell(0).setCellValue(psChar.getId());
			prodSpecCharValRow.createCell(1).setCellValue(sanitizeForExcel(psChar.getName()));
			prodSpecCharValRow.createCell(2).setCellValue(psCharVal.getValue());
			prodSpecCharValRow.createCell(3)
					.setCellValue(psCharVal.isIsDefault() != null
							? psCharVal.isIsDefault().toString()
							: "");
			prodSpecCharValRow.createCell(4).setCellValue(psCharVal.getValueFrom());
			prodSpecCharValRow.createCell(5).setCellValue(psCharVal.getValueTo());
			prodSpecCharValRow.createCell(6).setCellValue(psCharVal.getUnitOfMeasure());
			if (psCharVal.getTimeRange() != null) {
				prodSpecCharValRow.createCell(7)
						.setCellValue(psCharVal.getTimeRange().getValidFrom() != null
								? psCharVal.getTimeRange().getValidFrom().toString()
								: "");
				prodSpecCharValRow.createCell(8)
						.setCellValue(psCharVal.getTimeRange().getValidTo() != null
								? psCharVal.getTimeRange().getValidTo().toString()
								: "");
			} else {
				prodSpecCharValRow.createCell(7).setCellValue("NA");
				prodSpecCharValRow.createCell(8).setCellValue("NA");
			}
			if (psCharVal.getValidFor() != null) {
				prodSpecCharValRow.createCell(9)
						.setCellValue(psCharVal.getValidFor().getStartDateTime() != null
								? psCharVal.getValidFor().getStartDateTime().toString()
								: "");
				prodSpecCharValRow.createCell(10)
						.setCellValue(psCharVal.getValidFor().getEndDateTime() != null
								? psCharVal.getValidFor().getEndDateTime().toString()
								: "");
			} else {
				prodSpecCharValRow.createCell(9).setCellValue("");
				prodSpecCharValRow.createCell(10).setCellValue("");
			}
		}

		private void writePSCharDataRows(Sheet pSCharSheet, List<ProductSpecification> prodSpecs, Set<String> psChars, int prodSpecCharRowNum) {
			for (ProductSpecification ps : prodSpecs) {
				if (!psChars.contains(ps.getId()) && ps.getProductSpecCharacteristic() != null) {
					psChars.add(ps.getId());
					prodSpecCharRowNum = prepareProductSpecificationCharacteristicRows(pSCharSheet,ps,prodSpecCharRowNum);
				}
			}
		}

		private int prepareProductSpecificationCharacteristicRows(Sheet pSCharSheet, ProductSpecification ps, int prodSpecCharRowNum) {
			for (ProductSpecificationCharacteristic psChar : ps.getProductSpecCharacteristic()) {
				if (psChar != null) {
					Row prodSpecCharRow = pSCharSheet.createRow(prodSpecCharRowNum++);
					prodSpecCharRow.createCell(0).setCellValue(ps.getId());
					prodSpecCharRow.createCell(1).setCellValue(sanitizeForExcel(ps.getName()));
					prodSpecCharRow.createCell(2).setCellValue(sanitizeForExcel(psChar.getName()));
					prodSpecCharRow.createCell(3).setCellValue(psChar.getId());
					prodSpecCharRow.createCell(4).setCellValue(sanitizeForExcel(psChar.getDescription()));
					prodSpecCharRow.createCell(5).setCellValue(
							psChar.isConfigurable() != null ? psChar.isConfigurable().toString() : "");
					prodSpecCharRow.createCell(6).setCellValue(
							psChar.isExtensible() != null ? psChar.isExtensible().toString() : "");
					prodSpecCharRow.createCell(7).setCellValue(
							psChar.isIsUnique() != null ? psChar.isIsUnique().toString() : "");
					prodSpecCharRow.createCell(8).setCellValue(psChar.getValueType());
				}
			}
			return prodSpecCharRowNum;
		}
		//for ps export
		private int prepareProductSpecificationCharacteristicRowsforPS(Sheet pSCharSheet, ProductSpecification ps, int prodSpecCharRowNum) {
			for (ProductSpecificationCharacteristic psChar : ps.getProductSpecCharacteristic()) {
				if (psChar != null) {
					Row prodSpecCharRow = pSCharSheet.createRow(prodSpecCharRowNum++);
					prodSpecCharRow.createCell(0).setCellValue(ps.getId());
					prodSpecCharRow.createCell(1).setCellValue(sanitizeForExcel(ps.getName()));
					prodSpecCharRow.createCell(2).setCellValue(sanitizeForExcel(psChar.getName()));
					prodSpecCharRow.createCell(3).setCellValue(psChar.getType());
					prodSpecCharRow.createCell(4).setCellValue(psChar.getId());
					prodSpecCharRow.createCell(5).setCellValue(sanitizeForExcel(psChar.getDescription()));
					prodSpecCharRow.createCell(6).setCellValue(
							psChar.isConfigurable() != null ? psChar.isConfigurable().toString() : "");
					prodSpecCharRow.createCell(7).setCellValue(
							psChar.isExtensible() != null ? psChar.isExtensible().toString() : "");
					prodSpecCharRow.createCell(8).setCellValue(
							psChar.isIsUnique() != null ? psChar.isIsUnique().toString() : "");
					prodSpecCharRow.createCell(9).setCellValue(psChar.getValueType());
				}
			}
			return prodSpecCharRowNum;
		}

		private void writePSDataRows(List<ProductSpecification> prodSpecs, Set<String> pss, Sheet pSSheet, int prodSpecRowNum) {
			for (ProductSpecification ps : prodSpecs) {
				if (!pss.contains(ps.getId())) {
					pss.add(ps.getId());
					Row prodSpecRow = pSSheet.createRow(prodSpecRowNum++);
					prepareProductSpecificationRows(prodSpecRow,ps);
				}
			}
		}

		private void prepareProductSpecificationRows(Row prodSpecRow, ProductSpecification ps) {
			prodSpecRow.createCell(0).setCellValue(sanitizeForExcel(ps.getId()));
			prodSpecRow.createCell(1).setCellValue(sanitizeForExcel(sanitizeForExcel(ps.getName())));
			prodSpecRow.createCell(2).setCellValue(sanitizeForExcel(ps.getLastUpdate().toString()));
			prodSpecRow.createCell(3).setCellValue(ps.getSupportEntity().getValue());
			prodSpecRow.createCell(4).setCellValue(ps.getLifecycleStatus().getValue());
			prodSpecRow.createCell(5).setCellValue(ps.getVersion());
			if (ps.getValidFor() != null) {
				prodSpecRow.createCell(6)
						.setCellValue(ps.getValidFor().getStartDateTime() != null
								? ps.getValidFor().getStartDateTime().toString()
								: "");
				prodSpecRow.createCell(7)
						.setCellValue(ps.getValidFor().getEndDateTime() != null
								? ps.getValidFor().getEndDateTime().toString()
								: "");
			} else {
				prodSpecRow.createCell(6).setCellValue("");
				prodSpecRow.createCell(7).setCellValue("");
			}
			prodSpecRow.createCell(8).setCellValue(sanitizeForExcel(ps.getBrand()));
			prodSpecRow.createCell(9).setCellValue(sanitizeForExcel(ps.getDescription()));
			prodSpecRow.createCell(10).setCellValue(ps.getProductNumber());
			prodSpecRow.createCell(11).setCellValue(getRelatedPartyId(ps));
			prodSpecRow.createCell(12).setCellValue(sanitizeForExcel(getRelatedPartyName(ps)));
			prodSpecRow.createCell(13).setCellValue(
					ps.getServiceSpecification() != null && !ps.getServiceSpecification().isEmpty()
							? ps.getServiceSpecification().get(0).getId()
							: "");
			prodSpecRow.createCell(14).setCellValue(
					ps.getServiceSpecification() != null && !ps.getServiceSpecification().isEmpty()
							? sanitizeForExcel(ps.getServiceSpecification().get(0).getName())
							: "");
			prodSpecRow.createCell(15)
					.setCellValue(ps.getStockItemType() != null ? ps.getStockItemType().getId() : "");
			prodSpecRow.createCell(16)
					.setCellValue(ps.getStockItemType() != null ? sanitizeForExcel(ps.getStockItemType().getName()) : "");

			prodSpecRow.createCell(17).setCellValue(ps.getType());
		}

		//for PS export
		private void prepareProductSpecificationRowsforPS(Row prodSpecRow, ProductSpecification ps) {
			prodSpecRow.createCell(0).setCellValue(ps.getId());
			prodSpecRow.createCell(1).setCellValue(sanitizeForExcel(ps.getName()));
			List<ProductOffering> relatedPOs = fetchProductOfferingsByPsId(ps.getId());
			String poIds = relatedPOs.stream()
					.map(ProductOffering::getId)
					.filter(id -> id != null)
					.collect(Collectors.joining(",", "[", "]"));
			prodSpecRow.createCell(2).setCellValue(poIds.isEmpty() ? "" : poIds);
			prodSpecRow.createCell(3).setCellValue(ps.getLastUpdate().toString());
			prodSpecRow.createCell(4).setCellValue(ps.getSupportEntity().getValue());
			prodSpecRow.createCell(5).setCellValue(ps.getLifecycleStatus().getValue());
			prodSpecRow.createCell(6).setCellValue(ps.getVersion());
			if (ps.getValidFor() != null) {
				prodSpecRow.createCell(7)
						.setCellValue(ps.getValidFor().getStartDateTime() != null
								? ps.getValidFor().getStartDateTime().toString()
								: "");
				prodSpecRow.createCell(8)
						.setCellValue(ps.getValidFor().getEndDateTime() != null
								? ps.getValidFor().getEndDateTime().toString()
								: "");
			} else {
				prodSpecRow.createCell(7).setCellValue("");
				prodSpecRow.createCell(8).setCellValue("");
			}
			prodSpecRow.createCell(9).setCellValue(sanitizeForExcel(ps.getBrand()));
			prodSpecRow.createCell(10).setCellValue(sanitizeForExcel(ps.getDescription()));
			prodSpecRow.createCell(11).setCellValue(ps.getProductNumber());
			prodSpecRow.createCell(12).setCellValue(getRelatedPartyId(ps));
			prodSpecRow.createCell(13).setCellValue(sanitizeForExcel(getRelatedPartyName(ps)));
			prodSpecRow.createCell(14).setCellValue(
					ps.getServiceSpecification() != null && !ps.getServiceSpecification().isEmpty()
							? ps.getServiceSpecification().get(0).getId()
							: "");
			prodSpecRow.createCell(15).setCellValue(
					ps.getServiceSpecification() != null && !ps.getServiceSpecification().isEmpty()
							? sanitizeForExcel(ps.getServiceSpecification().get(0).getName())
							: "");
			prodSpecRow.createCell(16)
					.setCellValue(ps.getStockItemType() != null ? ps.getStockItemType().getId() : "");
			prodSpecRow.createCell(17)
					.setCellValue(ps.getStockItemType() != null ? sanitizeForExcel(ps.getStockItemType().getName()) : "");

			prodSpecRow.createCell(18).setCellValue(ps.getType());
		}


		private void writeAtomicPSDataRows(Sheet atomicPsSheet, List<ProductOffering> atomicPo, Set<String> aPos, Set<String> psIds, int aPsRowNum) {
			for (ProductOffering aPo : atomicPo) {
				if (!aPos.contains(aPo.getId())) {
					aPos.add(aPo.getId());
					Row atomicPsRow = atomicPsSheet.createRow(aPsRowNum++);
					atomicPsRow.createCell(0).setCellValue(aPo.getId());
					atomicPsRow.createCell(1).setCellValue(sanitizeForExcel(aPo.getName()));
					atomicPsRow.createCell(2).setCellValue(aPo.getProductSpecification().getId());
					atomicPsRow.createCell(3).setCellValue(sanitizeForExcel(aPo.getProductSpecification().getName()));
					psIds.add(aPo.getProductSpecification().getId());
				}
			}
		}

		private void writePOMasterDataRows(Set<ProductOffering> poMaster, Sheet productOfferingSheet, Set<String> poMasters, int atomicRowNum, Set<String> categoryIds, Set<String> popIds) {
			for (ProductOffering po : poMaster) {
				if (poMasters.contains(po.getId())) {
					continue;
				}

				poMasters.add(po.getId());
				Row productOfferingRow = productOfferingSheet.createRow(atomicRowNum++);
				productOfferingRow.createCell(0).setCellValue(po.getId());
				productOfferingRow.createCell(1).setCellValue(sanitizeForExcel(po.getName()));
				productOfferingRow.createCell(2).setCellValue(sanitizeForExcel(po.getDescription()));
				productOfferingRow.createCell(3).setCellValue(sanitizeForExcel(po.getBrand()));
				productOfferingRow.createCell(4).setCellValue(po.getType().getValue());
				productOfferingRow.createCell(5)
						.setCellValue(po.getIsSellable() != null ? po.getIsSellable().toString() : "");
				productOfferingRow.createCell(6)
						.setCellValue(po.getIsBundle() != null ? po.getIsBundle().toString() : "");
				productOfferingRow.createCell(7)
						.setCellValue(po.isIsInstallable() != null ? po.isIsInstallable().toString() : "");
				productOfferingRow.createCell(8)
						.setCellValue(po.getBillingType() != null ? po.getBillingType().getValue() : "");
				productOfferingRow.createCell(9).setCellValue(
						po.getLifecycleStatus() != null ? po.getLifecycleStatus().getValue() : "");
				setValidForCellValidForProductOffering(productOfferingRow, po);
				productOfferingRow.createCell(12).setCellValue(po.getVersion());
				productOfferingRow.createCell(13).setCellValue(getChannelData(po.getChannel()));
				productOfferingRow.createCell(14)
						.setCellValue(getCommercialDataIds(po.getCommercialOperation()));
				productOfferingRow.createCell(15)
						.setCellValue(sanitizeForExcel(getCommercialDataName(po.getCommercialOperation())));


				List<ProductOfferingTerm> terms = po.getProductOfferingTerm();
				String durationString = terms != null && !terms.isEmpty()
						? terms.stream()
						.filter(term -> term.getDuration().getAmount() != null)
						.map(term -> term.getDuration().getAmount().toString())
						.collect(Collectors.joining(", "))
						: "NA";
				String unitsString = terms != null && !terms.isEmpty()
						? terms.stream()
						.filter(term -> term.getDuration().getUnits() != null)
						.map(term -> term.getDuration().getUnits())
						.collect(Collectors.joining(", "))
						: "NA";
				productOfferingRow.createCell(16).setCellValue(sanitizeForExcel(getNullableValue(durationString)));
				productOfferingRow.createCell(17).setCellValue(sanitizeForExcel(getNullableValue(unitsString)));



				productOfferingRow.createCell(18).setCellValue(getMarketSegment(po.getMarketSegment()));
				productOfferingRow.createCell(19).setCellValue(getPolicyRuleRefID(po));
				productOfferingRow.createCell(20).setCellValue(sanitizeForExcel(getPolicyRuleRefName(po)));
				popIds.addAll(getPopIds(po));
				addCategoriesForProductOffering(po, categoryIds);
			}
		}

		private void addCategoriesForProductOffering(ProductOffering po, Set<String> categoryIds) {
			if (po.getCategory() != null && !po.getCategory().isEmpty()) {
				categoryIds.addAll(getCatIds(po));
			}
		}

		private void setValidForCellValidForProductOffering(Row productOfferingRow, ProductOffering po) {
			if (po.getValidFor() != null) {
				productOfferingRow.createCell(10).setCellValue(po.getValidFor().getStartDateTime() != null ? po.getValidFor().getStartDateTime().toString() : "");
				productOfferingRow.createCell(11).setCellValue(po.getValidFor().getEndDateTime() != null ? po.getValidFor().getEndDateTime().toString() : "");
			} else {
				productOfferingRow.createCell(10).setCellValue("");
				productOfferingRow.createCell(11).setCellValue("");
			}
		}

private int writeBundleChildDataRows(List<ProductOffering> bundlePo,
									 Sheet bundleChildSheet,
									 int bARowNum,
									 List<Integer> createdBARows,
									 Set<String> aPoIds,
									 Set<String> bPoIds,
									 Set<ProductOffering> allBundlePo) {

	if (bundlePo == null || bundlePo.isEmpty()) {
		LOGGER.warn("Bundle PO list is null or empty");
		return bARowNum;
	}

	Queue<ProductOffering> queue = enqueueInitialBundlePo(bundlePo, bPoIds);

	while (!queue.isEmpty()) {
		ProductOffering currentBPO = queue.poll();

		List<BundledProductOffering> children = currentBPO.getBundledProductOffering();
		int childrenCount = (children != null) ? children.size() : 0;
		LOGGER.debug("Processing BPO: {} with {} children", currentBPO.getId(), childrenCount);

		if (children == null || children.isEmpty()) {
			LOGGER.debug("No bundled product offerings for BPO: {}", currentBPO.getId());
			continue;
		}

		Map<String, ProductOffering> fetchedChildMap = fetchChildrenForBpo(currentBPO);

		for (BundledProductOffering childPo : children) {
			bARowNum = processChild(
					currentBPO,
					childPo,
					fetchedChildMap,
					bundleChildSheet,
					bARowNum,
					createdBARows,
					aPoIds,
					bPoIds,
					allBundlePo,
					queue
			);
		}
	}

	return bARowNum;
}

	private Queue<ProductOffering> enqueueInitialBundlePo(List<ProductOffering> bundlePo, Set<String> bPoIds) {
		Queue<ProductOffering> queue = new LinkedList<>();
		for (ProductOffering bPo : bundlePo) {
			if (bPo == null || bPo.getType() == null
					|| !ProductOfferingType.BUNDLEPRODUCTOFFERING.equals(bPo.getType())) {
				LOGGER.warn("Skipping non-BPO or null PO with ID: {}",
						bPo != null ? bPo.getId() : "null");
				continue;
			}
			if (!bPoIds.contains(bPo.getId())) {
				bPoIds.add(bPo.getId());
				queue.offer(bPo);
			}
		}
		return queue;
	}

	private Map<String, ProductOffering> fetchChildrenForBpo(ProductOffering currentBPO) {
		Set<String> childIds = new HashSet<>();
		if (currentBPO.getBundledProductOffering() != null) {
			for (BundledProductOffering child : currentBPO.getBundledProductOffering()) {
				if (child != null && child.getId() != null) {
					childIds.add(child.getId());
				}
			}
		}

		if (childIds.isEmpty()) {
			return Collections.emptyMap();
		}

		return fetchChildMap(childIds, currentBPO.getId());
	}

	private Map<String, ProductOffering> fetchChildMap(Set<String> childIds, String parentBpoId) {
		Map<String, ProductOffering> fetchedChildMap = new HashMap<>();

		String idsParam = buildIdsParam(childIds);
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("_id", idsParam);

		try {
			List<ProductOffering> fetchedList = getProductOfferings(requestParams, null, null, null);
			if (fetchedList != null) {
				for (ProductOffering po : fetchedList) {
					if (po != null && po.getId() != null) {
						fetchedChildMap.put(po.getId(), po);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.warn("Batch fetch failed for children of BPO {}: {}", parentBpoId, e.getMessage());
		}

		return fetchedChildMap;
	}

	private String buildIdsParam(Set<String> childIds) {
		StringBuilder idsBuilder = new StringBuilder();
		for (String id : childIds) {
			if (!idsBuilder.isEmpty()) {
				idsBuilder.append(",");
			}
			idsBuilder.append(id);
		}
		return idsBuilder.toString();
	}

	@SuppressWarnings("java:S107")
	private int processChild(ProductOffering currentBPO,
							 BundledProductOffering childPo,
							 Map<String, ProductOffering> fetchedChildMap,
							 Sheet bundleChildSheet,
							 int bARowNum,
							 List<Integer> createdBARows,
							 Set<String> aPoIds,
							 Set<String> bPoIds,
							 Set<ProductOffering> allBundlePo,
							 Queue<ProductOffering> queue) {

		if (childPo == null || childPo.getType() == null) {
			LOGGER.warn("Null child PO or type for BPO: {}", currentBPO.getId());
			return bARowNum;
		}

		ProductOffering fetchedChild = fetchedChildMap.get(childPo.getId());
		String childName = sanitizeForExcel(
				getNullableValue(
						fetchedChild != null && fetchedChild.getName() != null
								? fetchedChild.getName()
								: childPo.getName()
				)
		);

		writeBundleChildRow(
				bundleChildSheet,
				bARowNum,
				currentBPO,
				childPo,
				childName
		);

		// Keep row index management as before
		createdBARows.add(bARowNum++);

		String childType = childPo.getType();
		if (ProductOfferingType.ATOMICPRODUCTOFFERING.getValue().equals(childType)) {
			aPoIds.add(childPo.getId());
		} else if (ProductOfferingType.BUNDLEPRODUCTOFFERING.getValue().equals(childType)) {
			handleChildBpo(
					currentBPO,
					childPo,
					bundleChildSheet,
					bPoIds,
					allBundlePo,
					queue
			);
		} else {
			LOGGER.warn("Unexpected child PO type: {} for child ID: {}", childPo.getType(), childPo.getId());
		}

		return bARowNum;
	}

	private Row writeBundleChildRow(Sheet bundleChildSheet,
									int rowNum,
									ProductOffering currentBPO,
									BundledProductOffering childPo,
									String childName) {

		Row bundleChildRow = bundleChildSheet.createRow(rowNum);

		bundleChildRow.createCell(0).setCellValue(getNullableValue(currentBPO.getId()));
		bundleChildRow.createCell(1).setCellValue(sanitizeForExcel(getNullableValue(currentBPO.getName())));

		if (childPo.getBundledProductOfferingOption() != null) {
			bundleChildRow.createCell(2).setCellValue(
					childPo.getBundledProductOfferingOption().getNumberRelOfferDefault());
			bundleChildRow.createCell(3).setCellValue(
					childPo.getBundledProductOfferingOption().getNumberRelOfferLowerLimit());
			bundleChildRow.createCell(4).setCellValue(
					childPo.getBundledProductOfferingOption().getNumberRelOfferUpperLimit());
		} else {
			// preserve behavior: if null, default Excel numeric cells will be 0.0
			bundleChildRow.createCell(2).setCellValue(0);
			bundleChildRow.createCell(3).setCellValue(0);
			bundleChildRow.createCell(4).setCellValue(0);
		}

		bundleChildRow.createCell(5).setCellValue(getNullableValue(childPo.getId()));
		bundleChildRow.createCell(6).setCellValue(childName);
		bundleChildRow.createCell(7).setCellValue(getNullableValue(childPo.getType()));

		return bundleChildRow;
	}

	private void handleChildBpo(ProductOffering currentBPO,
								BundledProductOffering childPo,
								Sheet bundleChildSheet,
								Set<String> bPoIds,
								Set<ProductOffering> allBundlePo,
								Queue<ProductOffering> queue) {

		if (bPoIds.contains(childPo.getId())) {
			return;
		}

		bPoIds.add(childPo.getId());

		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("_id", childPo.getId());

		try {
			List<ProductOffering> childBPOs = getProductOfferings(requestParams, null, null, null);
			if (childBPOs == null || childBPOs.isEmpty()) {
				LOGGER.warn("No BPO found for ID: {}", childPo.getId());
				createErrorSheet(
						bundleChildSheet,
						currentBPO.getId(),
						"Child BPO not found: " + childPo.getId()
				);
			} else {
				ProductOffering childBpoPo = childBPOs.get(0);
				queue.offer(childBpoPo);
				allBundlePo.add(childBpoPo);
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Error fetching child BPO {}: {}", childPo.getId(), e.getMessage(), e);
			createErrorSheet(bundleChildSheet, currentBPO.getId(), e.getMessage());
		}
	}


private int prepareContractBundleAndBundleAtomicSheet(
		Sheet contractBundleSheet,
		ProductOffering cpo,
		int cBRowNum,
		List<Integer> createdCBRows,
		Set<String> aPoIds,
		Set<String> bPoIds) {

	if (isNullOrEmpty(cpo.getBundledProductOffering())) {
		cBRowNum = writeNoBundledOfferingsRow(contractBundleSheet, cpo, cBRowNum, createdCBRows);
		return cBRowNum;
	}

	List<BundledProductOffering> bundledList = cpo.getBundledProductOffering();

	Set<String> allChildIds = collectAllChildIds(bundledList);
	Map<String, ProductOffering> childPoMap = fetchChildProductOfferings(allChildIds);

	for (BundledProductOffering bpo : bundledList) {
		cBRowNum = writeBundledRow(
				contractBundleSheet,
				cpo,
				bpo,
				childPoMap,
				cBRowNum,
				createdCBRows,
				aPoIds,
				bPoIds
		);
	}

	return cBRowNum;
}

	private boolean isNullOrEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	private int writeNoBundledOfferingsRow(Sheet contractBundleSheet,
										   ProductOffering cpo,
										   int cBRowNum,
										   List<Integer> createdCBRows) {

		Row row = contractBundleSheet.createRow(cBRowNum++);
		row.createCell(0).setCellValue(cpo.getId());
		row.createCell(1).setCellValue(sanitizeForExcel(cpo.getName()));
		row.createCell(2).setCellValue("");
		row.createCell(3).setCellValue("");
		row.createCell(4).setCellValue("");
		row.createCell(5).setCellValue("");
		row.createCell(6).setCellValue("");
		row.createCell(7).setCellValue("");

		createdCBRows.add(cBRowNum - 1);
		return cBRowNum;
	}

	private Set<String> collectAllChildIds(List<BundledProductOffering> bundledList) {
		Set<String> allChildIds = new HashSet<>();
		for (BundledProductOffering bpo : bundledList) {
			if (bpo != null && bpo.getId() != null) {
				allChildIds.add(bpo.getId());
			}
		}
		return allChildIds;
	}

	private Map<String, ProductOffering> fetchChildProductOfferings(Set<String> allChildIds) {
		Map<String, ProductOffering> childPoMap = new HashMap<>();
		if (allChildIds == null || allChildIds.isEmpty()) {
			return childPoMap;
		}

		String idsParam = buildIdsParamm(allChildIds);
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("_id", idsParam);

		try {
			List<ProductOffering> fetched = getProductOfferings(requestParams, null, null, null);
			if (fetched != null) {
				for (ProductOffering po : fetched) {
					if (po != null && po.getId() != null) {
						childPoMap.put(po.getId(), po);
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.warn("Failed to fetch child POs for names (ids: {})", idsParam, e);
		}

		return childPoMap;
	}

	private String buildIdsParamm(Set<String> ids) {
		StringBuilder idsBuilder = new StringBuilder();
		for (String id : ids) {
			if (!idsBuilder.isEmpty()) {
				idsBuilder.append(",");
			}
			idsBuilder.append(id);
		}
		return idsBuilder.toString();
	}

	@SuppressWarnings("java:S107")
	private int writeBundledRow(Sheet contractBundleSheet,
								ProductOffering cpo,
								BundledProductOffering bpo,
								Map<String, ProductOffering> childPoMap,
								int cBRowNum,
								List<Integer> createdCBRows,
								Set<String> aPoIds,
								Set<String> bPoIds) {

		Row row = contractBundleSheet.createRow(cBRowNum++);

		// Contract PO info
		row.createCell(0).setCellValue(cpo.getId());
		row.createCell(1).setCellValue(sanitizeForExcel(cpo.getName()));

		// Cardinalities
		writeCardinalityCells(row, bpo.getBundledProductOfferingOption());

		// Child ID
		row.createCell(5).setCellValue(bpo.getId());

		// Child name
		String childName = resolveChildName(bpo, childPoMap);
		row.createCell(6).setCellValue(sanitizeForExcel(childName));

		// Child type
		String childType = (bpo.getType() != null) ? bpo.getType() : "";
		row.createCell(7).setCellValue(childType);

		// Track atomic vs bundle IDs
		trackChildTypeIds(childType, bpo.getId(), aPoIds, bPoIds);

		createdCBRows.add(cBRowNum - 1);
		return cBRowNum;
	}

	private void writeCardinalityCells(Row row, BundledProductOfferingOption opt) {
		if (opt != null) {
			String defaultCard = (opt.getNumberRelOfferDefault() != null)
					? opt.getNumberRelOfferDefault().toString()
					: "";
			String lowerCard = (opt.getNumberRelOfferLowerLimit() != null)
					? opt.getNumberRelOfferLowerLimit().toString()
					: "";
			String upperCard = (opt.getNumberRelOfferUpperLimit() != null)
					? opt.getNumberRelOfferUpperLimit().toString()
					: "";

			row.createCell(2).setCellValue(defaultCard);
			row.createCell(3).setCellValue(lowerCard);
			row.createCell(4).setCellValue(upperCard);
		} else {
			row.createCell(2).setCellValue("");
			row.createCell(3).setCellValue("");
			row.createCell(4).setCellValue("");
		}
	}

	private String resolveChildName(BundledProductOffering bpo,
									Map<String, ProductOffering> childPoMap) {

		String childName = "";
		ProductOffering fetchedPo = (bpo != null && bpo.getId() != null)
				? childPoMap.get(bpo.getId())
				: null;

		if (fetchedPo != null && fetchedPo.getName() != null && !fetchedPo.getName().trim().isEmpty()) {
			childName = fetchedPo.getName(); // Using fetched name
		} else if (bpo != null && bpo.getName() != null && !bpo.getName().trim().isEmpty()) {
			childName = bpo.getName();
		}

		return childName;
	}

	private void trackChildTypeIds(String childType,
								   String childId,
								   Set<String> aPoIds,
								   Set<String> bPoIds) {

		if (ProductOfferingType.ATOMICPRODUCTOFFERING.getValue().equals(childType)) {
			aPoIds.add(childId);
		} else {
			bPoIds.add(childId);
		}
	}



	/**
		 * This method is used to write rows for product offering characteristic sheet
		 * @param atomicPo
		 * @param poCharsVals
		 * @param poCharValueSheet
		 */
		private void writeProductOfferingCharacteristicsValues(List<ProductOffering> atomicPo, Set<String> poCharsVals, Sheet poCharValueSheet) {
			int poSpecCharValRowNum = poCharValueSheet.getLastRowNum() + 1;
			for (ProductOffering productOffering : atomicPo) {
				if (productOffering.getProdSpecCharValueUse() == null) {
					continue;
				}
				for (ProductSpecificationCharacteristicValueUse poChar : productOffering.getProdSpecCharValueUse()) {
					if (poCharsVals.contains(poChar.getId()) ||  poChar.getProductSpecCharacteristicValue() == null) {
						continue;
					}
					poCharsVals.add(poChar.getId());
					poSpecCharValRowNum = prepareAtomicProductOfferingChar(productOffering,poChar, poCharValueSheet, poSpecCharValRowNum);
				}
			}
		}

		private int prepareAtomicProductOfferingChar(ProductOffering productOffering, ProductSpecificationCharacteristicValueUse poChar, Sheet poCharValueSheet, int poSpecCharValRowNum) {
			for (ProductSpecificationCharacteristicValue psCharVal : poChar.getProductSpecCharacteristicValue()) {
				if (psCharVal == null) {
					continue;
				}
				Row prodSpecCharValRow = poCharValueSheet.createRow(poSpecCharValRowNum++);
				populateRowsForAtomicProductOfferingChar(prodSpecCharValRow, productOffering, poChar, psCharVal);
			}
			return poSpecCharValRowNum;
		}

		private void populateRowsForAtomicProductOfferingChar(Row prodSpecCharValRow, ProductOffering productOffering, ProductSpecificationCharacteristicValueUse poChar, ProductSpecificationCharacteristicValue psCharVal) {
			prodSpecCharValRow.createCell(0).setCellValue(productOffering.getId());
			prodSpecCharValRow.createCell(1).setCellValue(sanitizeForExcel(productOffering.getName()));
			prodSpecCharValRow.createCell(2).setCellValue(poChar.getId());
			prodSpecCharValRow.createCell(3).setCellValue(sanitizeForExcel(poChar.getName()));
			prodSpecCharValRow.createCell(4).setCellValue(sanitizeForExcel(poChar.getDescription()));
			prodSpecCharValRow.createCell(5).setCellValue(psCharVal.getValue());
			prodSpecCharValRow.createCell(6).setCellValue(psCharVal.isIsDefault() != null ? psCharVal.isIsDefault().toString() : "");
			prodSpecCharValRow.createCell(7).setCellValue(psCharVal.getIsSelectable() != null ? psCharVal.getIsSelectable().toString() : "");
			prodSpecCharValRow.createCell(8).setCellValue(psCharVal.getUnitOfMeasure());
			if (psCharVal.getTimeRange() != null) {
				prodSpecCharValRow.createCell(9).setCellValue(psCharVal.getTimeRange().getValidFrom() != null ? psCharVal.getTimeRange().getValidFrom().toString() : "");
				prodSpecCharValRow.createCell(10).setCellValue(psCharVal.getTimeRange().getValidTo() != null ? psCharVal.getTimeRange().getValidTo().toString() : "");
			} else {
				prodSpecCharValRow.createCell(9).setCellValue("");
				prodSpecCharValRow.createCell(10).setCellValue("");
			}
			prodSpecCharValRow.createCell(11).setCellValue(psCharVal.getType());
			prodSpecCharValRow.createCell(12).setCellValue(psCharVal.getAddressId() != null ? psCharVal.getAddressId() : "");
			prodSpecCharValRow.createCell(13).setCellValue(psCharVal.getSubUnitNumber() != null ? psCharVal.getSubUnitNumber() : "");
			prodSpecCharValRow.createCell(14).setCellValue(psCharVal.getStreetName() != null ? psCharVal.getStreetName() : "");
			prodSpecCharValRow.createCell(15).setCellValue(psCharVal.getPostcode() != null ? psCharVal.getPostcode() : "");
			prodSpecCharValRow.createCell(16).setCellValue(psCharVal.getCity() != null ? psCharVal.getCity() : "");
			prodSpecCharValRow.createCell(17).setCellValue(psCharVal.getCountry() != null ? psCharVal.getCountry() : "");


		}



		//helper methods for cfs export
		// Step 12: New method to prepare CFS master rows with PS details
		private void prepareServiceSpecificationRows(Row cfsRow, ServiceSpecification ss,  ProductSpecification ps) {
			cfsRow.createCell(0).setCellValue(ss.getId());
			cfsRow.createCell(1).setCellValue(sanitizeForExcel(ss.getName()));
			if(ps == null){
				cfsRow.createCell(2).setCellValue("");
				cfsRow.createCell(3).setCellValue("");
			}
			else{
			cfsRow.createCell(2).setCellValue(ps.getId());
			cfsRow.createCell(3).setCellValue(ps.getLifecycleStatus().getValue());}
			cfsRow.createCell(4).setCellValue(sanitizeForExcel(ss.getDescription()));
			if (ss.getValidFor() != null) {
				cfsRow.createCell(5)
						.setCellValue(ss.getValidFor().getStartDateTime() != null ?
								ss.getValidFor().getStartDateTime().toString() :
								"");
				cfsRow.createCell(6)
						.setCellValue(ss.getValidFor().getEndDateTime() != null ?
								ss.getValidFor().getEndDateTime().toString() :
								"");
			} else {
				cfsRow.createCell(5).setCellValue("");
				cfsRow.createCell(6).setCellValue("");
			}
			cfsRow.createCell(7).setCellValue(ss.getType());
			cfsRow.createCell(8).setCellValue(getRelatedResourcesId(ss));
			cfsRow.createCell(9).setCellValue(sanitizeForExcel(getRelatedResourcesName(ss)));
			cfsRow.createCell(10).setCellValue(getOperationSpecification(ss));

		}

	// Step 13: New method to get Operation Specification for CFS
	private String getOperationSpecification(ServiceSpecification ss) {
		if (ss.getOperationSpecification() != null && !ss.getOperationSpecification().isEmpty()) {
			return ss.getOperationSpecification().stream()
					.filter(Objects::nonNull)
					.map(OperationSpecification::getName)
					.filter(Objects::nonNull)
					.collect(Collectors.joining(", ", "[", "]"));
		}
		return "";
	}


	// Step 14: New method to prepare CFS characteristic rows
	private int prepareServiceSpecCharacteristicRows(Sheet cfsCharSheet, ServiceSpecification ss, int cfsCharRowNum) {
		for (CharacteristicSpecification cfsChar: ss.getServiceSpecCharacteristic()) {
			if (cfsChar != null) {
				Row cfsCharRow = cfsCharSheet.createRow(cfsCharRowNum++);
				cfsCharRow.createCell(0).setCellValue(ss.getId());
				cfsCharRow.createCell(1).setCellValue(sanitizeForExcel(ss.getName()));
				cfsCharRow.createCell(2).setCellValue(sanitizeForExcel(cfsChar.getName()));
				cfsCharRow.createCell(3).setCellValue(cfsChar.getType());
				cfsCharRow.createCell(4).setCellValue(cfsChar.getId());
				cfsCharRow.createCell(5).setCellValue(sanitizeForExcel(cfsChar.getDescription()));
				cfsCharRow.createCell(6).setCellValue(
						cfsChar.isConfigurable() != null ? cfsChar.isConfigurable().toString() : "");
				cfsCharRow.createCell(7).setCellValue(
						cfsChar.isExtensible() != null ? cfsChar.isExtensible().toString() : "");
				cfsCharRow.createCell(8).setCellValue(
						cfsChar.isIsUnique() != null ? cfsChar.isIsUnique().toString() : "");
				cfsCharRow.createCell(9).setCellValue(cfsChar.getValueType());
			}
		}
		return cfsCharRowNum;
	}

	// Step 15: New method to process CFS characteristic values
	private int processServiceSpecCharacteristics(Sheet cfsCharValueSheet, CharacteristicSpecification cfsChar, int cfsCharValRowNum) {
		for (CharacteristicValueSpecification cfsCharVal: cfsChar.getCharacteristicValueSpecification()) {
			if (cfsCharVal != null) {
				Row cfsCharValRow = cfsCharValueSheet.createRow(cfsCharValRowNum++);
				cfsCharValRow.createCell(0).setCellValue(cfsChar.getId());
				cfsCharValRow.createCell(1).setCellValue(sanitizeForExcel(cfsChar.getName()));
				cfsCharValRow.createCell(2).setCellValue(cfsCharVal.getValue());
				cfsCharValRow.createCell(3).setCellValue(cfsCharVal.isIsDefault() != null ? cfsCharVal.isIsDefault().toString() : "");
				cfsCharValRow.createCell(4).setCellValue(cfsCharVal.getValueFrom());
				cfsCharValRow.createCell(5).setCellValue(cfsCharVal.getValueTo());
				cfsCharValRow.createCell(6).setCellValue(cfsCharVal.getUnitOfMeasure());
				if (cfsCharVal.getTimeRange() != null) {
					cfsCharValRow.createCell(7).setCellValue(cfsCharVal.getTimeRange().getValidFrom() != null ? cfsCharVal.getTimeRange().getValidFrom().toString() : "NA");
					cfsCharValRow.createCell(8).setCellValue(cfsCharVal.getTimeRange().getValidTo() != null ? cfsCharVal.getTimeRange().getValidTo().toString() : "NA");
				} else {
					cfsCharValRow.createCell(7).setCellValue("NA");
					cfsCharValRow.createCell(8).setCellValue("NA");
				}
				if (cfsCharVal.getValidFor() != null) {
					cfsCharValRow.createCell(9).setCellValue(cfsCharVal.getValidFor().getStartDateTime() != null ? cfsCharVal.getValidFor().getStartDateTime().toString() : "");
					cfsCharValRow.createCell(10).setCellValue(cfsCharVal.getValidFor().getEndDateTime() != null ? cfsCharVal.getValidFor().getEndDateTime().toString() : "");
				} else {
					cfsCharValRow.createCell(9).setCellValue("");
					cfsCharValRow.createCell(10).setCellValue("");
				}

				if (cfsCharVal.getAddressId() != null) {
					cfsCharValRow.createCell(11).setCellValue(cfsCharVal.getAddressId());
					cfsCharValRow.createCell(12).setCellValue(cfsCharVal.getSubUnitNumber());
					cfsCharValRow.createCell(13).setCellValue(cfsCharVal.getStreetName());
					cfsCharValRow.createCell(14).setCellValue(cfsCharVal.getPostcode());
					cfsCharValRow.createCell(15).setCellValue(cfsCharVal.getCity());
				} else {
					cfsCharValRow.createCell(11).setCellValue("");
					cfsCharValRow.createCell(12).setCellValue("");
					cfsCharValRow.createCell(13).setCellValue("");
					cfsCharValRow.createCell(14).setCellValue("");
					cfsCharValRow.createCell(15).setCellValue("");
				}
			}
		}
		return cfsCharValRowNum;
	}

	// Step 16: New method to prepare CFS relationship rows
	private void prepareServiceSpecRelationshipRows(Row entityRelRow, ServiceSpecification ss, ServiceSpecRelationship ssRel) {
		entityRelRow.createCell(0).setCellValue(ss.getId());
		entityRelRow.createCell(1).setCellValue(ss.getType());
		entityRelRow.createCell(2).setCellValue(sanitizeForExcel(ss.getName()));
		entityRelRow.createCell(3).setCellValue(ssRel.getType());
		entityRelRow.createCell(4).setCellValue(ssRel.getId());
		entityRelRow.createCell(5).setCellValue(sanitizeForExcel(ssRel.getName()));
		if (ssRel.getValidFor() != null) {
			entityRelRow.createCell(6)
					.setCellValue(ssRel.getValidFor().getStartDateTime() != null ?
							ssRel.getValidFor().getStartDateTime().toString() :
							"");
			entityRelRow.createCell(7)
					.setCellValue(ssRel.getValidFor().getEndDateTime() != null ?
							ssRel.getValidFor().getEndDateTime().toString() :
							"");
		} else {
			entityRelRow.createCell(6).setCellValue("");
			entityRelRow.createCell(7).setCellValue("");
		}
	}

		private Sheet extractedProductOfferingCharVal(XSSFWorkbook workbook) {
			Sheet poCharValueSheet = workbook.getSheet(PO_CHAR_VALUE_SHEET);
			if (null == poCharValueSheet) {
				poCharValueSheet = workbook.createSheet(PO_CHAR_VALUE_SHEET);
				Row poCharValHeaderRow = poCharValueSheet.createRow(0);
				poCharValHeaderRow.createCell(0).setCellValue(ExcelSheetConstants.PRODUCT_OFFERING_ID);
				poCharValHeaderRow.createCell(1).setCellValue(ExcelSheetConstants.PRODUCT_OFFERING_NAME);
				poCharValHeaderRow.createCell(2).setCellValue(ExcelSheetConstants.CHARACTERISTIC_ID);
				poCharValHeaderRow.createCell(3).setCellValue(ExcelSheetConstants.CHARACTERISTIC_NAME);
				poCharValHeaderRow.createCell(4).setCellValue("Characteristic Description");
				poCharValHeaderRow.createCell(5).setCellValue(VALUE);
				poCharValHeaderRow.createCell(6).setCellValue("Is Default");
				poCharValHeaderRow.createCell(7).setCellValue("Is Selectable");
				poCharValHeaderRow.createCell(8).setCellValue(UOM);
				poCharValHeaderRow.createCell(9).setCellValue("Time Range ValidFrom");
				poCharValHeaderRow.createCell(10).setCellValue("Time Range ValidTo");
				poCharValHeaderRow.createCell(11).setCellValue("atType");
				poCharValHeaderRow.createCell(12).setCellValue(ADDID);
				poCharValHeaderRow.createCell(13).setCellValue(SUBUNIT);
				poCharValHeaderRow.createCell(14).setCellValue(STREET);
				poCharValHeaderRow.createCell(15).setCellValue(POST);
				poCharValHeaderRow.createCell(16).setCellValue(CITY);
				poCharValHeaderRow.createCell(17).setCellValue(COUNTRY);
			}
			return poCharValueSheet;
		}

		private void rollbackRows(Sheet sheet, List<Integer> rowsToRollback) {
			for (int rowIndex : rowsToRollback) {
				clearRowData(sheet.getRow(rowIndex));
			}
		}

		private void clearRowData(Row row) {
			if (row != null) {
				for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
					Cell cell = row.getCell(cellIndex);
					if (cell != null) {
						cell.setCellValue("");
					}
				}
			}
		}

		private void createErrorSheet(Sheet errorSheet, String contractId, String exception) {
			Row errorRow = errorSheet.createRow(errorSheet.getLastRowNum() + 1);
			errorRow.createCell(0).setCellValue(contractId);
			errorRow.createCell(1).setCellValue(exception);
		}

		private Sheet extractedErrorSheet(XSSFWorkbook workbook) {
			Sheet errorSheet = workbook.getSheet(ERROR_SHEET);
			if (null == errorSheet) {
				errorSheet = workbook.createSheet(ERROR_SHEET);
				Row errorHeaderRow = errorSheet.createRow(0);
				errorHeaderRow.createCell(0).setCellValue("Contract PO Id");
				errorHeaderRow.createCell(1).setCellValue("Error");
			}
			return errorSheet;
		}

		private Sheet extractedErrorSheetForPS(XSSFWorkbook workbook) {
			Sheet errorSheet = workbook.getSheet(ERROR_SHEET);
			if (errorSheet == null) {
				errorSheet = workbook.createSheet(ERROR_SHEET);
				Row headerRow = errorSheet.createRow(0);
				headerRow.createCell(0).setCellValue("PS Id");
				headerRow.createCell(1).setCellValue(ERRORMSG);
				LOGGER.debug("Error Sheet created for PS export with header 'PS Id'");
			}
			return errorSheet;
		}

		// step12c : method for error sheet in CFS export
		private Sheet extractedErrorSheetForCFS(XSSFWorkbook workbook) {
			Sheet errorSheet = workbook.getSheet(ERROR_SHEET);
			if (errorSheet == null) {
				errorSheet = workbook.createSheet(ERROR_SHEET);
				Row headerRow = errorSheet.createRow(0);
				headerRow.createCell(0).setCellValue(CFSID);
				headerRow.createCell(1).setCellValue(ERRORMSG);
				LOGGER.debug("Error Sheet created for CFS export with header 'CFS Id'");
			}
			return errorSheet;
		}

	// step15s: Add new error sheet extractor for stockItem
	private Sheet extractedErrorSheetForStock(XSSFWorkbook workbook) {
		Sheet errorSheet = workbook.getSheet(ERROR_SHEET);
		if (errorSheet == null) {
			errorSheet = workbook.createSheet(ERROR_SHEET);
			Row headerRow = errorSheet.createRow(0);
			headerRow.createCell(0).setCellValue("stockItem Id");
			headerRow.createCell(1).setCellValue(ERRORMSG);
		}
		return errorSheet;
	}



	private Sheet extractedCategory(XSSFWorkbook workbook) {
			Sheet categorySheet = workbook.getSheet(CATEGORY_SHEET);
			if (null == categorySheet) {
				categorySheet = workbook.createSheet(CATEGORY_SHEET);
				Row categoryHeaderRow = categorySheet.createRow(0);
				categoryHeaderRow.createCell(0).setCellValue(ExcelSheetConstants.PRODUCT_OFFERING_ID);
				categoryHeaderRow.createCell(1).setCellValue(ExcelSheetConstants.PRODUCT_OFFERING_NAME);
				categoryHeaderRow.createCell(2).setCellValue("Category ID");
				categoryHeaderRow.createCell(3).setCellValue("Category Name");
				categoryHeaderRow.createCell(4).setCellValue("isRoot");
				categoryHeaderRow.createCell(5).setCellValue(LAST_UPDATE);
				categoryHeaderRow.createCell(6).setCellValue("LifeCycle Status");
				categoryHeaderRow.createCell(7).setCellValue(VALIDITY_START_DATE);
				categoryHeaderRow.createCell(8).setCellValue(VALIDITY_END_DATE);
			}
			return categorySheet;
		}

		private Sheet extractedEntityRel(XSSFWorkbook workbook) {
			Sheet entityRelSheet = workbook.getSheet(ENTITY_REL_SHEET);
			if (null == entityRelSheet) {
				entityRelSheet = workbook.createSheet(ENTITY_REL_SHEET);
				Row entityRelHeaderRow = entityRelSheet.createRow(0);
				entityRelHeaderRow.createCell(0).setCellValue("Entity Id");
				entityRelHeaderRow.createCell(1).setCellValue("Entity Type");
				entityRelHeaderRow.createCell(2).setCellValue("Entity Name");
				entityRelHeaderRow.createCell(3).setCellValue("Relationship Type");
				entityRelHeaderRow.createCell(4).setCellValue("Target Entity ID");
				entityRelHeaderRow.createCell(5).setCellValue("Target Entity Name");
				entityRelHeaderRow.createCell(6).setCellValue(VALIDITY_START_DATE);
				entityRelHeaderRow.createCell(7).setCellValue(VALIDITY_END_DATE);
			}
			return entityRelSheet;
		}

		private Sheet extractedStockItem(XSSFWorkbook workbook) {
			Sheet stockItemSheet = workbook.getSheet(STOCK_ITEM_SHEET);
			if (null == stockItemSheet) {
				stockItemSheet = workbook.createSheet(STOCK_ITEM_SHEET);
				Row stockItemHeaderRow = stockItemSheet.createRow(0);
				stockItemHeaderRow.createCell(0).setCellValue("Stock Item Type ID");
				stockItemHeaderRow.createCell(1).setCellValue(STOCKITEMID);
				stockItemHeaderRow.createCell(2).setCellValue(STOCKITEMNAME);
				stockItemHeaderRow.createCell(3).setCellValue(DESCRIPTION);
				stockItemHeaderRow.createCell(4).setCellValue("EAN");
				stockItemHeaderRow.createCell(5).setCellValue("GTIN");
				stockItemHeaderRow.createCell(6).setCellValue(LAST_UPDATE);
				stockItemHeaderRow.createCell(7).setCellValue("Stock Item characteristic");
				stockItemHeaderRow.createCell(8).setCellValue("Stock Item characteristic Value");
				stockItemHeaderRow.createCell(9).setCellValue(VALIDITY_START_DATE);
				stockItemHeaderRow.createCell(10).setCellValue(VALIDITY_END_DATE);
			}
			return stockItemSheet;
		}

	// step16s: Add new extractor for stockItem master sheet with custom header for standalone export
	private Sheet extractedStockItemForStock(XSSFWorkbook workbook) {
		Sheet stockItemSheet = workbook.getSheet(STOCK_ITEM_SHEET);
		if (null == stockItemSheet) {
			stockItemSheet = workbook.createSheet(STOCK_ITEM_SHEET);
			Row headerRow = stockItemSheet.createRow(0);
			headerRow.createCell(0).setCellValue("Stock Item Type ID");
			headerRow.createCell(1).setCellValue(STOCKITEMID);
			headerRow.createCell(2).setCellValue(STOCKITEMNAME);
			headerRow.createCell(3).setCellValue("Related PS ID");
			headerRow.createCell(4).setCellValue("Related PS Status");
			headerRow.createCell(5).setCellValue(DESCRIPTION);
			headerRow.createCell(6).setCellValue("EAN");
			headerRow.createCell(7).setCellValue("GTIN");
			headerRow.createCell(8).setCellValue(LAST_UPDATE);
			headerRow.createCell(9).setCellValue(VALIDITY_START_DATE);
			headerRow.createCell(10).setCellValue(VALIDITY_END_DATE);
		}
		return stockItemSheet;
	}

	// step17s: Add new extractor for stockItem characteristics sheet
	private Sheet extractedStockItemChar(XSSFWorkbook workbook) {
		Sheet stockCharSheet = workbook.getSheet(STOCK_ITEM_CHAR_SHEET);
		if (null == stockCharSheet) {
			stockCharSheet = workbook.createSheet(STOCK_ITEM_CHAR_SHEET);
			Row headerRow = stockCharSheet.createRow(0);
			headerRow.createCell(0).setCellValue(STOCKITEMID);
			headerRow.createCell(1).setCellValue(STOCKITEMNAME);
			headerRow.createCell(2).setCellValue("Stock Item Characteristic ID");
			headerRow.createCell(3).setCellValue("Stock Item Characteristic Name");
			headerRow.createCell(4).setCellValue("Stock Item Characteristic Value");
		}
		return stockCharSheet;
	}

		private Sheet extractedPopCharAlt(XSSFWorkbook workbook) {
			Sheet popChargeAltSheet = workbook.getSheet(POP_CHARGE_ALT_SHEET);
			if (null == popChargeAltSheet) {
				popChargeAltSheet = workbook.createSheet(POP_CHARGE_ALT_SHEET);
				Row popChargeAltHeaderRow = popChargeAltSheet.createRow(0);
				popChargeAltHeaderRow.createCell(0).setCellValue("Product Offering Price Charge ID");
				popChargeAltHeaderRow.createCell(1).setCellValue("Product Offering Price Charge Name");
				popChargeAltHeaderRow.createCell(2).setCellValue("Relationship Type");
				popChargeAltHeaderRow.createCell(3).setCellValue("Product Offering Price Alteration ID");
				popChargeAltHeaderRow.createCell(4).setCellValue("Alteration Type");
				popChargeAltHeaderRow.createCell(5).setCellValue("Product Offering Price Alteration Name");
			}
			return popChargeAltSheet;
		}

		private Sheet extractedPop(XSSFWorkbook workbook) {
			Sheet popSheet = workbook.getSheet(POP_MASTER_SHEET);
			if (null == popSheet) {
				popSheet = workbook.createSheet(POP_MASTER_SHEET);
				Row popHeaderRow = popSheet.createRow(0);
				popHeaderRow.createCell(0).setCellValue(ExcelSheetConstants.PRODUCT_OFFERING_ID);
				popHeaderRow.createCell(1).setCellValue(ExcelSheetConstants.PRODUCT_OFFERING_NAME);
				popHeaderRow.createCell(2).setCellValue("Commitment Term Duration");
				popHeaderRow.createCell(3).setCellValue("Commitment Term Units");
				popHeaderRow.createCell(4).setCellValue("Commercial Operation Name");
				popHeaderRow.createCell(5).setCellValue("Product Offering Price ID");
				popHeaderRow.createCell(6).setCellValue("Product Offering Price Name");
				popHeaderRow.createCell(7).setCellValue(LIFECYCLE_STATUS);
				popHeaderRow.createCell(8).setCellValue("Price Type");
				popHeaderRow.createCell(9).setCellValue("Proration Type");
				popHeaderRow.createCell(10).setCellValue("Charge Cycle");
				popHeaderRow.createCell(11).setCellValue(VALIDITY_START_DATE);
				popHeaderRow.createCell(12).setCellValue(VALIDITY_END_DATE);
				popHeaderRow.createCell(13).setCellValue(VERSION);
				popHeaderRow.createCell(14).setCellValue("Immediate Payment");
				popHeaderRow.createCell(15).setCellValue("Product Offering Price Type");
				popHeaderRow.createCell(16).setCellValue("Price Value");
				popHeaderRow.createCell(17).setCellValue("Price Unit");
				popHeaderRow.createCell(18).setCellValue("Percentage");
				popHeaderRow.createCell(19).setCellValue("Recurring Charge Length");
				popHeaderRow.createCell(20).setCellValue("Recurring Charge Type");
				popHeaderRow.createCell(21).setCellValue("UOM Amount");
				popHeaderRow.createCell(22).setCellValue("UOM Units");
				popHeaderRow.createCell(23).setCellValue("Application Duration Length");
				popHeaderRow.createCell(24).setCellValue("Application Duration Unit");
				popHeaderRow.createCell(25).setCellValue("Down Payment");
				popHeaderRow.createCell(26).setCellValue("Partner");
				popHeaderRow.createCell(27).setCellValue("Interest Rate");
				popHeaderRow.createCell(28).setCellValue("External Id");

			}
			return popSheet;
		}

		private Sheet extractedCfs(XSSFWorkbook workbook) {
			Sheet cfsSheet = workbook.getSheet(CFS_SHEET);
			if (null == cfsSheet) {
				cfsSheet = workbook.createSheet(CFS_SHEET);
				Row cfsHeaderRow = cfsSheet.createRow(0);
				cfsHeaderRow.createCell(0).setCellValue(CFSID);
				cfsHeaderRow.createCell(1).setCellValue(CFSNAME);
				cfsHeaderRow.createCell(2).setCellValue(DESCRIPTION);
				cfsHeaderRow.createCell(3).setCellValue(VALIDITY_START_DATE);
				cfsHeaderRow.createCell(4).setCellValue(VALIDITY_END_DATE);
				cfsHeaderRow.createCell(5).setCellValue("Type");
				cfsHeaderRow.createCell(6).setCellValue("Related Resource ID");
				cfsHeaderRow.createCell(7).setCellValue("Related Resource Name");
			}
			return cfsSheet;
		}

		private Sheet extractedPsCfs(XSSFWorkbook workbook) {
			Sheet pSCFsSheet = workbook.getSheet(PS_CFS_SHEET);
			if (null == pSCFsSheet) {
				pSCFsSheet = workbook.createSheet(PS_CFS_SHEET);
				Row pSCfsHeaderRow = pSCFsSheet.createRow(0);
				pSCfsHeaderRow.createCell(0).setCellValue("PS ID");
				pSCfsHeaderRow.createCell(1).setCellValue("PS Name");
				pSCfsHeaderRow.createCell(2).setCellValue("Support entity type");
				pSCfsHeaderRow.createCell(3).setCellValue("Support entity_ID");
				pSCfsHeaderRow.createCell(4).setCellValue("Support entity_name");
			}
			return pSCFsSheet;
		}

		private Sheet extractedPsCharVal(XSSFWorkbook workbook) {
			Sheet pSCharValueSheet = workbook.getSheet(PS_CHAR_VALUE_SHEET);
			if (null == pSCharValueSheet) {
				pSCharValueSheet = workbook.createSheet(PS_CHAR_VALUE_SHEET);
				Row pSCharValHeaderRow = pSCharValueSheet.createRow(0);
				pSCharValHeaderRow.createCell(0).setCellValue(ExcelSheetConstants.CHARACTERISTIC_ID);
				pSCharValHeaderRow.createCell(1).setCellValue(ExcelSheetConstants.CHARACTERISTIC_NAME);
				pSCharValHeaderRow.createCell(2).setCellValue(VALUE);
				pSCharValHeaderRow.createCell(3).setCellValue(ISDEFAULT);
				pSCharValHeaderRow.createCell(4).setCellValue(VALUEFROM);
				pSCharValHeaderRow.createCell(5).setCellValue(VALUETO);
				pSCharValHeaderRow.createCell(6).setCellValue(UOM);
				pSCharValHeaderRow.createCell(7).setCellValue(VALIDITY_START_DATE);
				pSCharValHeaderRow.createCell(8).setCellValue(VALIDITY_END_DATE);
				pSCharValHeaderRow.createCell(9).setCellValue(ADDID);
				pSCharValHeaderRow.createCell(10).setCellValue(SUBUNIT);
				pSCharValHeaderRow.createCell(11).setCellValue(STREET);
				pSCharValHeaderRow.createCell(12).setCellValue(POST);
				pSCharValHeaderRow.createCell(13).setCellValue(CITY);
				pSCharValHeaderRow.createCell(14).setCellValue(COUNTRY);
			}
			return pSCharValueSheet;
		}

		//for ps export
		private Sheet extractedPsCharValforPS(XSSFWorkbook workbook) {
			Sheet pSCharValueSheet = workbook.getSheet(PS_CHAR_VALUE_SHEET);
			if (null == pSCharValueSheet) {
				pSCharValueSheet = workbook.createSheet(PS_CHAR_VALUE_SHEET);
				Row pSCharValHeaderRow = pSCharValueSheet.createRow(0);
				pSCharValHeaderRow.createCell(0).setCellValue(ExcelSheetConstants.CHARACTERISTIC_ID);
				pSCharValHeaderRow.createCell(1).setCellValue(ExcelSheetConstants.CHARACTERISTIC_NAME);
				pSCharValHeaderRow.createCell(2).setCellValue(VALUE);
				pSCharValHeaderRow.createCell(3).setCellValue(ISDEFAULT);
				pSCharValHeaderRow.createCell(4).setCellValue(VALUEFROM);
				pSCharValHeaderRow.createCell(5).setCellValue(VALUETO);
				pSCharValHeaderRow.createCell(6).setCellValue(UOM);
				pSCharValHeaderRow.createCell(7).setCellValue("Time Range start date");
				pSCharValHeaderRow.createCell(8).setCellValue("Time Range end date");
				pSCharValHeaderRow.createCell(9).setCellValue(VALIDITY_START_DATE);
				pSCharValHeaderRow.createCell(10).setCellValue(VALIDITY_END_DATE);
			}
			return pSCharValueSheet;
		}

		private Sheet extractedPsChar(XSSFWorkbook workbook) {
			Sheet pSCharSheet = workbook.getSheet(PS_CHAR_SHEET);
			if (null == pSCharSheet) {
				pSCharSheet = workbook.createSheet(PS_CHAR_SHEET);
				Row pSCharHeaderRow = pSCharSheet.createRow(0);
				pSCharHeaderRow.createCell(0).setCellValue(PRODUCT_SPECIFICATION_ID);
				pSCharHeaderRow.createCell(1).setCellValue(PRODUCT_SPECIFICATION_NAME);
				pSCharHeaderRow.createCell(2).setCellValue(ExcelSheetConstants.CHARACTERISTIC_NAME);
				pSCharHeaderRow.createCell(3).setCellValue(ExcelSheetConstants.CHARACTERISTIC_ID);
				pSCharHeaderRow.createCell(4).setCellValue(DESCRIPTION);
				pSCharHeaderRow.createCell(5).setCellValue(CONFIG);
				pSCharHeaderRow.createCell(6).setCellValue(EXTENSIBLE);
				pSCharHeaderRow.createCell(7).setCellValue(ISUNIQUE);
				pSCharHeaderRow.createCell(8).setCellValue(VALUETYPE);
			}
			return pSCharSheet;
		}

		private Sheet extractedPsCharforPS(XSSFWorkbook workbook) {
			Sheet pSCharSheet = workbook.getSheet(PS_CHAR_SHEET);
			if (null == pSCharSheet) {
				pSCharSheet = workbook.createSheet(PS_CHAR_SHEET);
				Row pSCharHeaderRow = pSCharSheet.createRow(0);
				pSCharHeaderRow.createCell(0).setCellValue(PRODUCT_SPECIFICATION_ID);
				pSCharHeaderRow.createCell(1).setCellValue(PRODUCT_SPECIFICATION_NAME);
				pSCharHeaderRow.createCell(2).setCellValue(ExcelSheetConstants.CHARACTERISTIC_NAME);
				pSCharHeaderRow.createCell(3).setCellValue(ExcelSheetConstants.CHARACTERISTIC_TYPE);
				pSCharHeaderRow.createCell(4).setCellValue(ExcelSheetConstants.CHARACTERISTIC_ID);
				pSCharHeaderRow.createCell(5).setCellValue(DESCRIPTION);
				pSCharHeaderRow.createCell(6).setCellValue(CONFIG);
				pSCharHeaderRow.createCell(7).setCellValue(EXTENSIBLE);
				pSCharHeaderRow.createCell(8).setCellValue(ISUNIQUE);
				pSCharHeaderRow.createCell(9).setCellValue(VALUETYPE);
			}
			return pSCharSheet;
		}


		private Sheet extractedPs(XSSFWorkbook workbook) {
			Sheet pSSheet = workbook.getSheet(PS_MASTER_SHEET);
			if (null == pSSheet) {
				pSSheet = workbook.createSheet(PS_MASTER_SHEET);
				Row pSHeaderRow = pSSheet.createRow(0);
				pSHeaderRow.createCell(0).setCellValue(PRODUCT_SPECIFICATION_ID);
				pSHeaderRow.createCell(1).setCellValue(PRODUCT_SPECIFICATION_NAME);
				pSHeaderRow.createCell(2).setCellValue(LAST_UPDATE);
				pSHeaderRow.createCell(3).setCellValue("Support Entity Type");
				pSHeaderRow.createCell(4).setCellValue(LIFECYCLE_STATUS);
				pSHeaderRow.createCell(5).setCellValue(VERSION);
				pSHeaderRow.createCell(6).setCellValue(VALIDITY_START_DATE);
				pSHeaderRow.createCell(7).setCellValue(VALIDITY_END_DATE);
				pSHeaderRow.createCell(8).setCellValue(BRAND);
				pSHeaderRow.createCell(9).setCellValue(DESCRIPTION);
				pSHeaderRow.createCell(10).setCellValue("Product Number");
				pSHeaderRow.createCell(11).setCellValue("Related Party Id");
				pSHeaderRow.createCell(12).setCellValue("Related Party Name");
				pSHeaderRow.createCell(13).setCellValue("Service specification ID");
				pSHeaderRow.createCell(14).setCellValue("Service specification Name");
				pSHeaderRow.createCell(15).setCellValue("Stock item type Id");
				pSHeaderRow.createCell(16).setCellValue("Stock item type Name");
				pSHeaderRow.createCell(17).setCellValue("Type");
			}
			return pSSheet;
		}

		//for PS export
		private Sheet extractedPsforPS(XSSFWorkbook workbook) {
			Sheet pSSheet = workbook.getSheet(PS_MASTER_SHEET);
			if (null == pSSheet) {
				pSSheet = workbook.createSheet(PS_MASTER_SHEET);
				Row pSHeaderRow = pSSheet.createRow(0);
				pSHeaderRow.createCell(0).setCellValue(PRODUCT_SPECIFICATION_ID);
				pSHeaderRow.createCell(1).setCellValue(PRODUCT_SPECIFICATION_NAME);
				pSHeaderRow.createCell(2).setCellValue("Related Product Offering ID");
				pSHeaderRow.createCell(3).setCellValue(LAST_UPDATE);
				pSHeaderRow.createCell(4).setCellValue("Support Entity Type");
				pSHeaderRow.createCell(5).setCellValue(LIFECYCLE_STATUS);
				pSHeaderRow.createCell(6).setCellValue(VERSION);
				pSHeaderRow.createCell(7).setCellValue(VALIDITY_START_DATE);
				pSHeaderRow.createCell(8).setCellValue(VALIDITY_END_DATE);
				pSHeaderRow.createCell(9).setCellValue(BRAND);
				pSHeaderRow.createCell(10).setCellValue(DESCRIPTION);
				pSHeaderRow.createCell(11).setCellValue("Product Number");
				pSHeaderRow.createCell(12).setCellValue("Related Party Id");
				pSHeaderRow.createCell(13).setCellValue("Related Party Name");
				pSHeaderRow.createCell(14).setCellValue("Service specification ID");
				pSHeaderRow.createCell(15).setCellValue("Service specification Name");
				pSHeaderRow.createCell(16).setCellValue("Stock item type Id");
				pSHeaderRow.createCell(17).setCellValue("Stock item type Name");
				pSHeaderRow.createCell(18).setCellValue("Type");
			}
			return pSSheet;
		}

		private Sheet extractedAtomicPs(XSSFWorkbook workbook) {
			Sheet atomicPsSheet = workbook.getSheet(ATOMIC_PS_SHEET);
			if (null == atomicPsSheet) {
				atomicPsSheet = workbook.createSheet(ATOMIC_PS_SHEET);
				Row bundleAtomicHeaderRow = atomicPsSheet.createRow(0);
				bundleAtomicHeaderRow.createCell(0).setCellValue("Atomic PO ID");
				bundleAtomicHeaderRow.createCell(1).setCellValue("Atomic PO Name");
				bundleAtomicHeaderRow.createCell(2).setCellValue(PRODUCT_SPECIFICATION_ID);
				bundleAtomicHeaderRow.createCell(3).setCellValue(PRODUCT_SPECIFICATION_NAME);
			}
			return atomicPsSheet;
		}

		private Sheet extractedPoMaster(XSSFWorkbook workbook) {
			Sheet productOfferingSheet = workbook.getSheet(PO_MASTER_SHEET);
			if (null == productOfferingSheet) {
				productOfferingSheet = workbook.createSheet(PO_MASTER_SHEET);
				Row atomicPOHeaderRow = productOfferingSheet.createRow(0);
				atomicPOHeaderRow.createCell(0).setCellValue("ID");
				atomicPOHeaderRow.createCell(1).setCellValue("Name");
				atomicPOHeaderRow.createCell(2).setCellValue(DESCRIPTION);
				atomicPOHeaderRow.createCell(3).setCellValue(BRAND);
				atomicPOHeaderRow.createCell(4).setCellValue("Type");
				atomicPOHeaderRow.createCell(5).setCellValue("isSellable");
				atomicPOHeaderRow.createCell(6).setCellValue("isBundle");
				atomicPOHeaderRow.createCell(7).setCellValue("isInstallable");
				atomicPOHeaderRow.createCell(8).setCellValue("Billing Type");
				atomicPOHeaderRow.createCell(9).setCellValue(LIFECYCLE_STATUS);
				atomicPOHeaderRow.createCell(10).setCellValue(VALIDITY_START_DATE);
				atomicPOHeaderRow.createCell(11).setCellValue(VALIDITY_END_DATE);
				atomicPOHeaderRow.createCell(12).setCellValue(VERSION);
				atomicPOHeaderRow.createCell(13).setCellValue("Channels");
				atomicPOHeaderRow.createCell(14).setCellValue("Commercial Operations ID");
				atomicPOHeaderRow.createCell(15).setCellValue("Commercial Operations Name");
				atomicPOHeaderRow.createCell(16).setCellValue("Commitment Term Duration");
				atomicPOHeaderRow.createCell(17).setCellValue("Commitment Term Units");
				atomicPOHeaderRow.createCell(18).setCellValue("Market Segment");
				atomicPOHeaderRow.createCell(19).setCellValue("Policy Rule Ref ID");
				atomicPOHeaderRow.createCell(20).setCellValue("Policy Rule Ref Name");
			}
			return productOfferingSheet;
		}

		private Sheet extractedBundle(XSSFWorkbook workbook) {
			Sheet bundleChildSheet = workbook.getSheet(BUNDLE_CHILD_SHEET);
			if (null == bundleChildSheet) {
				bundleChildSheet = workbook.createSheet(BUNDLE_CHILD_SHEET);
				Row bundleChildHeaderRow = bundleChildSheet.createRow(0);
				bundleChildHeaderRow.createCell(0).setCellValue("Bundle PO ID");
				bundleChildHeaderRow.createCell(1).setCellValue("Bundle PO Name");
				bundleChildHeaderRow.createCell(2).setCellValue("Min Cardinality");
				bundleChildHeaderRow.createCell(3).setCellValue("Default Cardinality");
				bundleChildHeaderRow.createCell(4).setCellValue("Max Cardinality");
				bundleChildHeaderRow.createCell(5).setCellValue("Child PO ID");
				bundleChildHeaderRow.createCell(6).setCellValue("Child PO Name");
				bundleChildHeaderRow.createCell(7).setCellValue("Child PO Type");
			}
			return bundleChildSheet;
		}

		private Sheet extractedContract(XSSFWorkbook workbook) {
			Sheet contractBundleSheet = workbook.getSheet(CONTRACT_BUNDLE_SHEET);
			if (null == contractBundleSheet) {
				// Create header row for main data
				contractBundleSheet = workbook.createSheet(CONTRACT_BUNDLE_SHEET);
				Row contractBundleHeaderRow = contractBundleSheet.createRow(0);
				contractBundleHeaderRow.createCell(0).setCellValue("Contract PO ID");
				contractBundleHeaderRow.createCell(1).setCellValue("Contract PO Name");
				contractBundleHeaderRow.createCell(2).setCellValue("Min Cardinality");
				contractBundleHeaderRow.createCell(3).setCellValue("Default Cardinality");
				contractBundleHeaderRow.createCell(4).setCellValue("Max Cardinality");
				contractBundleHeaderRow.createCell(5).setCellValue("Child PO ID");
				contractBundleHeaderRow.createCell(6).setCellValue("Child PO Name");
				contractBundleHeaderRow.createCell(7).setCellValue("Child PO Type");
			}
			return contractBundleSheet;
		}

	// Step 19: New sheets extraction methods for CFS
	private Sheet extractedCfsMaster(XSSFWorkbook workbook) {
		Sheet cfsSheet = workbook.getSheet(CFS_MASTER_SHEET);
		if (null == cfsSheet) {
			cfsSheet = workbook.createSheet(CFS_MASTER_SHEET);
			Row cfsHeaderRow = cfsSheet.createRow(0);
			cfsHeaderRow.createCell(0).setCellValue(CFSID);
			cfsHeaderRow.createCell(1).setCellValue(CFSNAME);
			cfsHeaderRow.createCell(2).setCellValue("PS ID");
			cfsHeaderRow.createCell(3).setCellValue("PS Lifecycle Status");
			cfsHeaderRow.createCell(4).setCellValue(DESCRIPTION);
			cfsHeaderRow.createCell(5).setCellValue(VALIDITY_START_DATE);
			cfsHeaderRow.createCell(6).setCellValue(VALIDITY_END_DATE);
			cfsHeaderRow.createCell(7).setCellValue("Type");
			cfsHeaderRow.createCell(8).setCellValue("Related Resource ID");
			cfsHeaderRow.createCell(9).setCellValue("Related Resource Name");
			cfsHeaderRow.createCell(10).setCellValue("Operation Specification");
		}
		return cfsSheet;
	}

	private Sheet extractedCfsChar(XSSFWorkbook workbook) {
		Sheet cfsCharSheet = workbook.getSheet(CFS_CHAR_SHEET);
		if (null == cfsCharSheet) {
			cfsCharSheet = workbook.createSheet(CFS_CHAR_SHEET);
			Row cfsCharHeaderRow = cfsCharSheet.createRow(0);
			cfsCharHeaderRow.createCell(0).setCellValue(CFSID);
			cfsCharHeaderRow.createCell(1).setCellValue(CFSNAME);
			cfsCharHeaderRow.createCell(2).setCellValue(ExcelSheetConstants.CHARACTERISTIC_NAME);
			cfsCharHeaderRow.createCell(3).setCellValue(ExcelSheetConstants.CHARACTERISTIC_TYPE);
			cfsCharHeaderRow.createCell(4).setCellValue(ExcelSheetConstants.CHARACTERISTIC_ID);
			cfsCharHeaderRow.createCell(5).setCellValue(DESCRIPTION);
			cfsCharHeaderRow.createCell(6).setCellValue(CONFIG);
			cfsCharHeaderRow.createCell(7).setCellValue(EXTENSIBLE);
			cfsCharHeaderRow.createCell(8).setCellValue(ISUNIQUE);
			cfsCharHeaderRow.createCell(9).setCellValue(VALUETYPE);
		}
		return cfsCharSheet;
	}

	private Sheet extractedCfsCharVal(XSSFWorkbook workbook) {
		Sheet cfsCharValueSheet = workbook.getSheet(CFS_CHAR_VALUE_SHEET);
		if (null == cfsCharValueSheet) {
			cfsCharValueSheet = workbook.createSheet(CFS_CHAR_VALUE_SHEET);
			Row cfsCharValHeaderRow = cfsCharValueSheet.createRow(0);
			cfsCharValHeaderRow.createCell(0).setCellValue(ExcelSheetConstants.CHARACTERISTIC_ID);
			cfsCharValHeaderRow.createCell(1).setCellValue(ExcelSheetConstants.CHARACTERISTIC_NAME);
			cfsCharValHeaderRow.createCell(2).setCellValue(VALUE);
			cfsCharValHeaderRow.createCell(3).setCellValue(ISDEFAULT);
			cfsCharValHeaderRow.createCell(4).setCellValue(VALUEFROM);
			cfsCharValHeaderRow.createCell(5).setCellValue(VALUETO);
			cfsCharValHeaderRow.createCell(6).setCellValue(UOM);
			cfsCharValHeaderRow.createCell(7).setCellValue("Time Range start date");
			cfsCharValHeaderRow.createCell(8).setCellValue("Time Range end date");
			cfsCharValHeaderRow.createCell(9).setCellValue(VALIDITY_START_DATE);
			cfsCharValHeaderRow.createCell(10).setCellValue(VALIDITY_END_DATE);
			cfsCharValHeaderRow.createCell(11).setCellValue(ADDID);
			cfsCharValHeaderRow.createCell(12).setCellValue(SUBUNIT);
			cfsCharValHeaderRow.createCell(13).setCellValue(STREET);
			cfsCharValHeaderRow.createCell(14).setCellValue(POST);
			cfsCharValHeaderRow.createCell(15).setCellValue(CITY);
		}
		return cfsCharValueSheet;
	}

		private String getRelatedPartyName(ProductSpecification ps) {
			if (ps.getRelatedParty() != null && !ps.getRelatedParty().isEmpty()) {
				return ps.getRelatedParty().stream().map(RelatedParty::getName).collect(Collectors.joining(", ", "[", "]"));
			}
			return "";
		}

		private String getRelatedPartyId(ProductSpecification ps) {
			if (ps.getRelatedParty() != null && !ps.getRelatedParty().isEmpty()) {
				return ps.getRelatedParty().stream().map(RelatedParty::getId).collect(Collectors.joining(", ", "[", "]"));
			}
			return "";
		}

		private void extracted(ExportJob export, Set<String> childIds, Set<String> fetchedChildIds, String exceptionId) {
			if (!fetchedChildIds.containsAll(childIds)) {
				Set<String> missingIds = new HashSet<>(childIds);
				missingIds.removeAll(fetchedChildIds);
				updateExportJob(export.getId(), JobStateType.FAILED, exceptionId);
				throw new DiscoManagedClientException(exceptionId, "", missingIds.toString());
			}
		}

		private Category getCategory(String catId, List<Category> categories) {
			return categories.stream().filter(cat -> cat.getId().equals(catId)).findFirst().orElse(null);
		}

		private List<Category> getCategories(Set<String> categoryIds) throws UnsupportedEncodingException {
			Map<String, Object> requestParams = extracted(categoryIds);
			return categoryService.fetchCategory(requestParams);
		}

		private List<String> getCatIds(ProductOffering po) {
			return po.getCategory().stream().map(CategoryRef::getId).toList();
		}

		private String getPORelationshipName(String poRId, Set<ProductOffering> poMaster) {
			Optional<ProductOffering> poRelationship = poMaster.stream().filter(po -> po.getId().equals(poRId)).findFirst();
			if (!poRelationship.isPresent()) {
				ProductOffering prodOff = productOfferingService.fetchProductOfferingById(poRId);
				if (prodOff == null) {
					throw new DiscoManagedClientException(DISCO_APO_NOT_FOUND, " For Relationship with Id: ", poRId);
				}
				return prodOff.getName();
			}
			return poRelationship.get().getName();
		}

		private List<StockItem> getStockitems(Set<String> stockItemTypeIds) throws UnsupportedEncodingException {
			Map<String, Object> requestParams = new HashMap<>();
			requestParams.put("stockItemType._id", String.join(",", stockItemTypeIds));
			return stockItemService.fetchStockItem(requestParams);
		}

		private ProductOfferingPrice getProductOfferingPriceData(String popId, List<ProductOfferingPrice> pops) {
			if (popId == null) {
				return null;
			}
			return pops.stream().filter(pop -> popId.equals(pop.getId())).findFirst().orElse(null);
		}

		private List<ProductOfferingPrice> getProductOfferingPrices(Set<String> popIds)
			throws UnsupportedEncodingException {
			return productOfferingPriceService.getProductOfferingPricesByIdsWithSubtypes(popIds);
		}

		private List<String> getPopIds(ProductOffering po) {
			List<String> ids = new ArrayList<>();
			//  Direct commercialOperation
			if (po.getCommercialOperation() != null) {
				ids.addAll(
						po.getCommercialOperation().stream()
								.filter(Objects::nonNull)
								.flatMap(co -> {
									List<ProductOfferingPriceRef> carries = co.getCarries();
									return carries != null ? carries.stream() : Stream.empty();
								})
								.filter(Objects::nonNull)
								.map(ProductOfferingPriceRef::getId)
								.filter(Objects::nonNull)
								.toList()
				);
			}
			// inside ProductOfferingTerm  commercialOperation
			if (po.getProductOfferingTerm() != null) {
				for (ProductOfferingTerm term : po.getProductOfferingTerm()) {
					if (term.getCommercialOperation() != null) {
						ids.addAll(
								term.getCommercialOperation().stream()
										.filter(Objects::nonNull)
										.flatMap(co -> {
											List<ProductOfferingPriceRef> carries = co.getCarries();
											return carries != null ? carries.stream() : Stream.empty();
										})
										.filter(Objects::nonNull)
										.map(ProductOfferingPriceRef::getId)
										.filter(Objects::nonNull)
										.toList()
						);
					}
				}
			}
			return ids;
		}

		private String getRelatedResourcesId(ServiceSpecification cfs) {
			if (cfs.getRelatedResource() != null && !cfs.getRelatedResource().isEmpty()) {
				return cfs.getRelatedResource().stream().filter(Objects::nonNull).map(RelatedResource::getId)
						.filter(Objects::nonNull).collect(Collectors.joining(", ", "[", "]"));
			}
			return "";
		}

		private String getRelatedResourcesName(ServiceSpecification cfs) {
			if (cfs.getRelatedResource() != null && !cfs.getRelatedResource().isEmpty()) {
				return cfs.getRelatedResource().stream().filter(Objects::nonNull).map(RelatedResource::getName)
						.filter(Objects::nonNull).collect(Collectors.joining(", ", "[", "]"));
			}
			return "";
		}

		private String getPolicyRuleRefID(ProductOffering po) {
			if (po.getPolicyRuleRef() != null && !po.getPolicyRuleRef().isEmpty()) {
				return po.getPolicyRuleRef().stream().filter(Objects::nonNull) // Filter out any null elements
						.map(PolicyRuleRef::getId).filter(Objects::nonNull) // Ensure that getId() does not return null
						.collect(Collectors.joining(", ", "[", "]"));
			}
			return "";
		}

		private String getPolicyRuleRefName(ProductOffering po) {
			if (po.getPolicyRuleRef() != null && !po.getPolicyRuleRef().isEmpty()) {
				return po.getPolicyRuleRef().stream().filter(Objects::nonNull).map(PolicyRuleRef::getName)
						.filter(Objects::nonNull).collect(Collectors.joining(", ", "[", "]"));
			}
			return "";
		}

		private List<ServiceSpecification> getCFSSpecs(Set<String> cfsIds) throws UnsupportedEncodingException {
			Map<String, Object> requestParams = extracted(cfsIds);
			return serviceSpecsService.fetchServiceSpecifications(requestParams);
		}

		private List<ProductSpecification> getProductSpecs(Set<String> psIds) throws UnsupportedEncodingException {
			Map<String, Object> requestParams = extracted(psIds);
			return productSpecService.fetchProductSpecification(requestParams, null,
					null, null);
		}

		private Map<String, Object> extracted(Set<String> psIds) {
			Map<String, Object> requestParams = new HashMap<>();
			requestParams.put("_id", String.join(",", psIds));
			return requestParams;
		}

		private String getChannelData(List<ChannelRef> channels) {
			return channels.stream().map(ChannelRef::getId).collect(Collectors.joining(", ", "[", "]"));
		}

		private String getCommercialDataIds(List<CommercialOperation> commercialOperations) {
			return commercialOperations.stream().map(CommercialOperation::getId)
					.collect(Collectors.joining(", ", "[", "]"));
		}

		private String getCommercialDataName(List<CommercialOperation> commercialOperations) {
			return commercialOperations.stream().map(CommercialOperation::getName)
					.collect(Collectors.joining(", ", "[", "]"));
		}

		private String getMarketSegment(List<MarketSegmentRef> marketSegments) {
			return marketSegments.stream().map(MarketSegmentRef::getId).collect(Collectors.joining(", ", "[", "]"));
		}

		private List<ProductOffering> getProductOfferingsData(Set<String> bpoIds) throws UnsupportedEncodingException {
			Map<String, Object> requestParams = extracted(bpoIds);
			return getProductOfferings(requestParams, null, null, null);
		}

		/**
		 * Fetch the ExportJob on the basis of the parameters given.
		 *
		 * @param requestParams parameters to search upon
		 * @return List of filtered ExportJob
		 */
		@Override
		public List<ExportJob> fetchExportJob(Map<String, Object> requestParams) throws UnsupportedEncodingException {
			Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);
			Query query = new Query();
			for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
				query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
			}
			return mongoTemplate.find(query, ExportJob.class);
		}

		/**
		 * Fetch the ExportJob on the basis of id.
		 *
		 * @param id to search
		 * @return ExportJob
		 */
		@Override
		public ExportJob fetchExportJobById(final String id) {
			return mongoTemplate.findById(id, ExportJob.class);
		}

		/**
		 * Find ExportJob instance w.r.t id parameter and only show fields of fieldList
		 * of ExportJob.
		 *
		 * @param id        the id of exportJob
		 * @param fieldList the fieldList for exportJob
		 * @return exportJob
		 */
		@Override
		public ExportJob fetchExportJobById(final String id, List<String> fieldList) {
			Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
			return mongoTemplate.findOne(query, ExportJob.class);
		}

	}
