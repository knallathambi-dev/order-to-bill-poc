// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.orange.discobole.productinventory.api.v1.ProductApi;
import com.orange.discobole.productinventory.constant.QueryFields;
import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.job.enumerate.FileType;
import com.orange.discobole.productinventory.service.ExportProductService;
import com.orange.discobole.productinventory.service.HrefGeneratorService;
import com.orange.discobole.productinventory.service.PatchProductService;
import com.orange.discobole.productinventory.service.ProductService;
import com.orange.discobole.productinventory.service.impl.AuthorizationService;
import com.orange.discobole.productinventory.util.ApiUtil;
import com.orange.discobole.productinventory.util.PageableHeader;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.PipedInputStream;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.orange.discobole.productinventory.constant.PreAuthorizeExpressions.*;
import static com.orange.discobole.productinventory.constant.QueryFields.PRODUCT_RELATIONSHIP_PRODUCT_ID;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_QUERY_STRING_PARAMETER;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.INVALID_FORMAT_FOR_PRODUCT_RELATIONSHIP_ID;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.NOT_BE_EMPTY;
import static com.orange.discobole.productinventory.util.ApiUtil.addListToMap;
import static com.orange.discobole.productinventory.util.ApiUtil.addValueToMap;
import static com.orange.discobole.productinventory.util.FileUtils.getProductExportFileName;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class ProductApiImpl implements ProductApi {

    private final ProductService productService;
    private final AuthorizationService authorisationService;
    private final ProductMapper productMapper;
    private final HrefGeneratorService generatorHref;
    private final PatchProductService patchProductService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final ExportProductService exportProductService;

    @SneakyThrows
    @Override
    @PreAuthorize(CAN_CREATE_PRODUCT)
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        // Validate that user can only create products for themselves (IDOR protection)
        authorisationService.validateProductCreation(product);

        Product productSaved = productService.createProduct(product);
        generatorHref.generateHrefProductRelationships(Collections.singletonList(productSaved));
        log.info("Received request to create products");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION, productSaved.getHref());
        return new ResponseEntity<>(productSaved, responseHeaders, HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize(CAN_READ_PRODUCT)
    public ResponseEntity<Product> retrieveProduct(String id, String fields) {
        log.info("Received request to get product by ID");

        if (Objects.nonNull(fields) && StringUtils.isBlank(fields)) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), "fields" + NOT_BE_EMPTY);
        }
        this.authorisationService.validateByIdRequest(id);
        ProductEntity productEntity = productService.getProductEntity(id, fields);
        Product productDto = productMapper.toDtoWithProductIdOnly(productEntity);
        generatorHref.generateHrefProductRelationships(Collections.singletonList(productDto));
        return ResponseEntity.ok(productDto);
        //API conformance with CTK 404
    }

    @Override
    @PreAuthorize(CAN_READ_PRODUCT)
    public ResponseEntity<List<Product>> listProduct(List<String> relatedPartyPartyOrPartyRoleId, String productCharacteristicValue, String productCharacteristicName, String productCharacteristicAtType, List<String> productOrderItemProductOrderId, List<String> id, List<String> productOrderItemOrderItemId, List<ProductStatusType> status, List<ProductOperationalStatusType> operationalStatus, List<OffsetDateTime> startDate, OffsetDateTime startDateGte, OffsetDateTime startDateLte, List<String> productSpecificationId, List<String> productOfferingId, List<String> productRelationshipRelationshipType, List<String> productOfferingAtType, List<String> productSpecificationAtType, List<String> relatedPartyPartyOrPartyRoleName, List<String> relatedPartyPartyOrPartyRolePartyId, List<String> relatedPartyPartyOrPartyRolePartyName, List<String> relatedPartyPartyOrPartyRoleAtReferredType, List<String> atType, List<String> relatedPartyPartyOrPartyRoleRole, List<String> productRelationshipProductId, List<String> productOfferingName, OffsetDateTime terminationDateGte, OffsetDateTime terminationDateLte, OffsetDateTime creationDateGte, OffsetDateTime creationDateLte, List<String> name, Boolean isRoot, OffsetDateTime lastUpdateDateGte, OffsetDateTime lastUpdateDateLte, OffsetDateTime orderDateGte, OffsetDateTime orderDateLte, String fields, Integer offset, Integer limit, List<SortEnum> sort) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        addListToMap(QueryFields.RELATED_PARTY + QueryFields.ID_SUFFIX, multiValueMap, relatedPartyPartyOrPartyRoleId);
        addListToMap(QueryFields.RELATED_PARTY + QueryFields.NAME_SUFFIX, multiValueMap, relatedPartyPartyOrPartyRoleName);
        addListToMap(QueryFields.RELATED_PARTY_PARTY_ID, multiValueMap, relatedPartyPartyOrPartyRolePartyId);
        addListToMap(QueryFields.RELATED_PARTY_PARTY_NAME, multiValueMap, relatedPartyPartyOrPartyRolePartyName);
        addListToMap(QueryFields.RELATED_PARTY_PARTY_AT_REFERRED_TYPE, multiValueMap, relatedPartyPartyOrPartyRoleAtReferredType);
        addListToMap(QueryFields.RELATED_PARTY_ROLE, multiValueMap, relatedPartyPartyOrPartyRoleRole);
        addListToMap(QueryFields.PRODUCT_ORDER_ITEM_PRODUCT_ORDER_ID, multiValueMap, productOrderItemProductOrderId);
        addListToMap(QueryFields.ID, multiValueMap, id);
        addListToMap(QueryFields.PRODUCT_ORDER_ITEM_ORDER_ITEM_ID, multiValueMap, productOrderItemOrderItemId);
        addListToMap(QueryFields.STATUS, multiValueMap, status);
        addListToMap(QueryFields.OPERATIONAL_STATUS, multiValueMap, operationalStatus);
        addListToMap(QueryFields.PRODUCT_SPECIFICATION + QueryFields.ID_SUFFIX, multiValueMap, productSpecificationId);
        addListToMap(QueryFields.PRODUCT_OFFERING + QueryFields.ID_SUFFIX, multiValueMap, productOfferingId);
        addListToMap(QueryFields.START_DATE, multiValueMap, startDate);
        addValueToMap(QueryFields.START_DATE + QueryFields.GTE_SUFFIX, multiValueMap, startDateGte);
        addValueToMap(QueryFields.START_DATE + QueryFields.LTE_SUFFIX, multiValueMap, startDateLte);
        addListToMap(QueryFields.PRODUCT_RELATIONSHIP_RELATIONSHIP_TYPE, multiValueMap, productRelationshipRelationshipType);
        addListToMap(QueryFields.PRODUCT_OFFERING + QueryFields.AT_TYPE_SUFFIX, multiValueMap, productOfferingAtType);
        addListToMap(QueryFields.PRODUCT_SPECIFICATION + QueryFields.AT_TYPE_SUFFIX, multiValueMap, productSpecificationAtType);

        addListToMap(QueryFields.AT_TYPE, multiValueMap, atType);
        addListToMap(QueryFields.PRODUCT_OFFERING + QueryFields.NAME_SUFFIX, multiValueMap, productOfferingName);
        addValueToMap(QueryFields.TERMINATION_DATE + QueryFields.GTE_SUFFIX, multiValueMap, terminationDateGte);
        addValueToMap(QueryFields.TERMINATION_DATE + QueryFields.LTE_SUFFIX, multiValueMap, terminationDateLte);
        addValueToMap(QueryFields.CREATION_DATE + QueryFields.GTE_SUFFIX, multiValueMap, creationDateGte);
        addValueToMap(QueryFields.CREATION_DATE + QueryFields.LTE_SUFFIX, multiValueMap, creationDateLte);
        addListToMap(QueryFields.NAME, multiValueMap, name);
        addValueToMap(QueryFields.IS_ROOT, multiValueMap, isRoot);
        addValueToMap(QueryFields.LAST_UPDATE_DATE + QueryFields.GTE_SUFFIX, multiValueMap, lastUpdateDateGte);
        addValueToMap(QueryFields.LAST_UPDATE_DATE + QueryFields.LTE_SUFFIX, multiValueMap, lastUpdateDateLte);
        addValueToMap(QueryFields.ORDER_DATE + QueryFields.GTE_SUFFIX, multiValueMap, orderDateGte);
        addValueToMap(QueryFields.ORDER_DATE + QueryFields.LTE_SUFFIX, multiValueMap, orderDateLte);
        addValueToMap(QueryFields.PRODUCT_CHARACTERISTIC + QueryFields.VALUE_SUFFIX, multiValueMap, productCharacteristicValue);
        addValueToMap(QueryFields.PRODUCT_CHARACTERISTIC + QueryFields.NAME_SUFFIX, multiValueMap, productCharacteristicName);
        addValueToMap(QueryFields.PRODUCT_CHARACTERISTIC + QueryFields.AT_TYPE_SUFFIX, multiValueMap, productCharacteristicAtType);

        if (Objects.nonNull(productRelationshipProductId)) {
            List<String> validObjectIds = productRelationshipProductId.stream().filter(ApiUtil::isValidObjectId).toList();

            if (!validObjectIds.isEmpty()) {
                addListToMap(PRODUCT_RELATIONSHIP_PRODUCT_ID, multiValueMap, validObjectIds.stream().map(ObjectId::new).toList());
            } else {
                throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_QUERY_STRING_PARAMETER.getCode(), INVALID_QUERY_STRING_PARAMETER.getStatus(), INVALID_FORMAT_FOR_PRODUCT_RELATIONSHIP_ID);
            }
        }
        authorisationService.validateFetchRequest(multiValueMap, QueryFields.RELATED_PARTY + QueryFields.ID_SUFFIX);
        PageableTMF pageable = new PageableTMF(offset, limit, productService.getTotalCount(multiValueMap), Objects.nonNull(sort) ? sort.stream().map(SortEnum::getValue).toList() : null, fields, multiValueMap);
        List<ProductEntity> productEntities = productService.getProducts(pageable);
        List<Product> productDTOs = productEntities.stream().map(productMapper::toDtoWithProductIdOnly).toList();
        pageable.setResultCount(productEntities.size());
        generatorHref.generateHrefProductRelationships(productDTOs);
        HttpHeaders responseHeaders = PageableHeader.buildPaginationHeaders(pageable, request);
        boolean dataFitInOnPage = pageable.getTotalCount() <= pageable.getLimit();
        return new ResponseEntity<>(productDTOs, responseHeaders, dataFitInOnPage ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT);
    }

    @Override
    @PreAuthorize(CAN_UPDATE_PRODUCT)
    public ResponseEntity<Product> patchProduct(String id, Object product) {
        // Validate that user can only update their own products (IDOR protection)
        authorisationService.validateProductUpdate(id);

        return new ResponseEntity<>(productService.updateProduct(id, objectMapper.convertValue(product, Product.class)), HttpStatus.OK);
    }

    @Override
    @PreAuthorize(CAN_UPDATE_PRODUCT)
    public ResponseEntity<List<Product>> patchProducts(List<ProductPatch> productPatch) {
        Map<String, ArrayNode> productIdWithArrayNodeList = patchProductService.groupProductOperations(productPatch);

        // Validate that user can only update their own products (IDOR protection)
        productIdWithArrayNodeList.keySet().forEach(authorisationService::validateProductUpdate);

        List<ProductEntity> productsToBePatched = productService.getListOfProductEntityBy(productIdWithArrayNodeList.keySet());
        List<ProductEntity> patchedProducts = patchProductService.applyPatchToProductsWithValidation(productIdWithArrayNodeList, productsToBePatched);
        List<ProductEntity> productList = productService.updateProducts(patchedProducts);
        List<Product> productDtoList = productList.stream().map(productMapper::toDtoWithProductIdOnly).toList();
        generatorHref.generateHrefProductRelationships(productDtoList);
        return new ResponseEntity<>(productDtoList, HttpStatus.OK);
    }


    @SneakyThrows
    @Override
    @PreAuthorize(CAN_READ_PRODUCT)
    public ResponseEntity<Resource> exportProduct(
            ContentTypeEnum contentType,
            String relatedPartyPartyOrPartyRoleId,
            List<ProductStatusType> status,
            OffsetDateTime startDateGte,
            OffsetDateTime startDateLte,
            OffsetDateTime creationDateGte,
            OffsetDateTime creationDateLte,
            OffsetDateTime lastUpdateDateGte,
            OffsetDateTime lastUpdateDateLte
    ) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        addValueToMap(QueryFields.RELATED_PARTY + QueryFields.ID_SUFFIX, multiValueMap, relatedPartyPartyOrPartyRoleId);
        addListToMap(QueryFields.STATUS, multiValueMap, status);
        addValueToMap(QueryFields.START_DATE + QueryFields.GTE_SUFFIX, multiValueMap, startDateGte);
        addValueToMap(QueryFields.START_DATE + QueryFields.LTE_SUFFIX, multiValueMap, startDateLte);
        addValueToMap(QueryFields.CREATION_DATE + QueryFields.GTE_SUFFIX, multiValueMap, creationDateGte);
        addValueToMap(QueryFields.CREATION_DATE + QueryFields.LTE_SUFFIX, multiValueMap, creationDateLte);
        addValueToMap(QueryFields.LAST_UPDATE_DATE + QueryFields.GTE_SUFFIX, multiValueMap, lastUpdateDateGte);
        addValueToMap(QueryFields.LAST_UPDATE_DATE + QueryFields.LTE_SUFFIX, multiValueMap, lastUpdateDateLte);
        PipedInputStream inputStream = new PipedInputStream();
        Resource resource = new InputStreamResource(inputStream);
        exportProductService.exportProducts(multiValueMap, inputStream, contentType);
        FileType fileType = FileType.fromValue(contentType);
        String fileName = getProductExportFileName(fileType);
        MediaType mediaType = switch (fileType) {
            case JSON -> MediaType.APPLICATION_JSON;
            case CSV -> MediaType.parseMediaType("text/csv");
        };

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(fileName))
                .body(resource);
    }


    @Override
    @PreAuthorize(CAN_DELETE_PRODUCT)
    public ResponseEntity<String> deleteProduct(String id) {
        log.info("Received request to delete product by ID");

        // Validate that user can only delete their own products (IDOR protection)
        authorisationService.validateByIdRequest(id);

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}