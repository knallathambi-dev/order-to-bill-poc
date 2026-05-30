// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.status;

import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;
import com.orange.discobole.productinventory.exception.ProductInventoryException;
import com.orange.discobole.productinventory.model.OperationalStatusChangeEntity;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.StatusChangeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.*;

import static com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType.CONFIRMED;
import static com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType.CREATED;
import static com.orange.discobole.productinventory.enumerate.ErrorCodeEnum.INVALID_INPUT;
import static com.orange.discobole.productinventory.exception.model.BusinessErrors.*;

@Slf4j
public abstract class StatusChecker {

    private final Map<ProductStatusType, Set<ProductOperationalStatusType>> mapProductStatusMainToOperation;

    private final Map<ProductStatusType, Set<ProductStatusType>> mapProductStatusMain;

    private final Map<ProductOperationalStatusType, Set<ProductOperationalStatusType>> mapProductStatusOperation;

    protected StatusChecker(Map<ProductStatusType, Set<ProductOperationalStatusType>> mapProductStatusMainToOperation,
                            Map<ProductStatusType, Set<ProductStatusType>> mapProductStatusMain,
                            Map<ProductOperationalStatusType, Set<ProductOperationalStatusType>> mapProductStatusOperation) {
        this.mapProductStatusMainToOperation = new EnumMap<>(mapProductStatusMainToOperation);
        this.mapProductStatusMain = new EnumMap<>(mapProductStatusMain);
        this.mapProductStatusOperation = new EnumMap<>(mapProductStatusOperation);
    }



    public void validateProductStatus(ProductStatusType statusOld, ProductOperationalStatusType operationalStatusOld, ProductStatusType statusNew, ProductOperationalStatusType operationalStatusNew) {
        checkValidTransitionMain(statusOld, statusNew);
        checkValidTransitionOperational(operationalStatusOld, operationalStatusNew);
        checkStatusOperationalStatusMapping(statusNew, operationalStatusNew);
    }

    public void checkProductStatusBeforeCreated(ProductStatusType status, ProductOperationalStatusType operationalStatus) {
        checkProductStatusMainBeforeCreated(status);
        checkProductOperationalStatusBeforeCreated(operationalStatus);
    }

    private void checkStatusOperationalStatusMapping(ProductStatusType statusNew, ProductOperationalStatusType operationalStatusNew) {
        Set<ProductOperationalStatusType> operationalStatusSet = mapProductStatusMainToOperation.getOrDefault(statusNew, Collections.emptySet());
        boolean isValidStatusOperationalStatusMapping = operationalStatusSet.contains(operationalStatusNew);
        if (!isValidStatusOperationalStatusMapping) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(),
                    String.format(THE_MAPPING_BETWEEN_THE_STATUS_AND_THE_OPERATIONAL_STATUS_IS_INVALID, statusNew, operationalStatusNew));
        }
    }

    private void checkProductStatusMainBeforeCreated(ProductStatusType status) {
        if ((Objects.nonNull(status)) && (status != ProductStatusType.CREATED)) {
            log.error("Invalid product status: {}", status);
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), THE_STATUS_SHOULD_BE_CREATED);
        }
    }

    private void checkProductOperationalStatusBeforeCreated(ProductOperationalStatusType operationalStatus) {
        if ((Objects.nonNull(operationalStatus)) && (operationalStatus != ProductOperationalStatusType.CREATED) && (operationalStatus != ProductOperationalStatusType.CONFIRMED)) {
            log.error("Invalid product operational status: {}", operationalStatus);
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(), THE_OPERATIONAL_STATUS_SHOULD_BE_CREATED);
        }
    }

    private void checkValidTransitionOperational(ProductOperationalStatusType operationalStatusOld, ProductOperationalStatusType operationalStatusNew) {
        boolean isValidTransition = isValidTransitionOperational(operationalStatusOld, operationalStatusNew);
        if (!isValidTransition) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(),
                    String.format(THE_OPERATIONAL_STATUS_CANNOT_BE_MODIFIED, operationalStatusOld, operationalStatusNew));
        }
    }

    private void checkValidTransitionMain(ProductStatusType statusOld, ProductStatusType statusNew) {
        boolean isValidTransition = isValidTransitionMain(statusOld, statusNew);
        if (!isValidTransition) {
            throw new ProductInventoryException(HttpStatus.BAD_REQUEST, INVALID_INPUT.getCode(), INVALID_INPUT.getStatus(),
                    String.format(THE_STATUS_CANNOT_BE_MODIFIED, statusOld, statusNew));
        }
    }

    private boolean isValidTransitionOperational(ProductOperationalStatusType fromStatus, ProductOperationalStatusType toStatus) {
        Set<ProductOperationalStatusType> statusOperationalSet = mapProductStatusOperation.getOrDefault(fromStatus, Set.of(CREATED, CONFIRMED));
        return statusOperationalSet != null && statusOperationalSet.contains(toStatus);
    }

    private boolean isValidTransitionMain(ProductStatusType fromStatus, ProductStatusType toStatus) {
        Set<ProductStatusType> statusMainSet = mapProductStatusMain.getOrDefault(fromStatus, Collections.emptySet());
        return statusMainSet != null && statusMainSet.contains(toStatus);
    }

    public static void addStatusChange(ProductEntity updatedProduct) {
        ProductStatusType status =  updatedProduct.getStatus();
        List<StatusChangeEntity> statusChange = updatedProduct.getStatusChange();
        ProductStatusType oldStatus = statusChange != null ? statusChange.get(statusChange.size() - 1).getStatus() : null;

        if (statusChange == null) {
            statusChange = new ArrayList<>();
        }

        if (statusChange.isEmpty() || !status.equals(oldStatus)) {
            statusChange.add(
                    StatusChangeEntity.builder()
                            .changeDate(OffsetDateTime.now())
                            .status(status)
                            .build()
            );

            updatedProduct.setStatusChange(statusChange);
        }

        ProductOperationalStatusType operationalStatus =  updatedProduct.getOperationalStatus();
        List<OperationalStatusChangeEntity> operationalStatusChange  = updatedProduct.getOperationalStatusChange();
        ProductOperationalStatusType oldOperationalStatus = operationalStatusChange != null ? operationalStatusChange.get(operationalStatusChange.size() - 1).getStatus() : null;

       if (operationalStatusChange == null) {
           operationalStatusChange = new ArrayList<>();
       }

        if (operationalStatusChange.isEmpty() || !operationalStatus.equals(oldOperationalStatus)) {
            operationalStatusChange.add(
            OperationalStatusChangeEntity.builder()
                    .changeDate(OffsetDateTime.now())
                    .status(operationalStatus)
                    .build()
            );
            updatedProduct.setOperationalStatusChange(operationalStatusChange);
        }
    }

}
