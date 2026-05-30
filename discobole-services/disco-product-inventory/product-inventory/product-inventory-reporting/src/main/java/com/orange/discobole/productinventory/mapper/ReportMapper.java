// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.mapper;

import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.exception.UnsupportedTypeException;
import com.orange.discobole.productinventory.model.ProductOfferReportEntity;
import com.orange.discobole.productinventory.model.StatusReportEntity;
import com.orange.discobole.productinventory.model.StatusReportingFields;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ReportMapper {

    public <T extends StatusReportingFields> Report toDto(T statusReportingFieldsChild) {
        if (statusReportingFieldsChild instanceof StatusReportEntity statusReportEntity) {
            return ReportProductByStatus
                    .builder()
                    .id(statusReportEntity.getId().toString())
                    .collectDate(statusReportEntity.getDate())
                    .atType(ReportType.REPORTPRODUCTBYSTATUS.getValue())
                    .details(createReportProductByStatusDetails(statusReportEntity))
                    .build();
        }
        if (statusReportingFieldsChild instanceof ProductOfferReportEntity productOfferReportEntity) {
            return ReportProductByOffer
                    .builder()
                    .id(productOfferReportEntity.getId().toString())
                    .collectDate(productOfferReportEntity.getDate())
                    .atType(ReportType.REPORTPRODUCTBYOFFER.getValue())
                    .details(createReportProductByStatusDetails(productOfferReportEntity)
                            .stream().map(reportDetails -> {
                                reportDetails.getCharacteristic().addAll(getOfferCharacteristics(productOfferReportEntity));
                                return reportDetails;
                            })
                            .toList()
                    )
                    .build();
        }
        throw new UnsupportedTypeException("StatusReportingFields", statusReportingFieldsChild.getClass().getName());
    }

    private List<? extends @Valid ReportCharacteristic> getOfferCharacteristics(ProductOfferReportEntity productOfferReportEntity) {
        return List.of(
                getCharacteristic(ProductOfferReportEntity.Fields.productOfferId, productOfferReportEntity.getProductOfferId()),
                getCharacteristic(ProductOfferReportEntity.Fields.productOfferType, productOfferReportEntity.getProductOfferType()),
                getCharacteristic(ProductOfferReportEntity.Fields.productOfferName, productOfferReportEntity.getProductOfferName())
        );
    }

    private List<@Valid ReportDetails> createReportProductByStatusDetails(StatusReportingFields statusReportEntity) {
        return
                Arrays.stream(ProductStatusType.values())
                        .map(statusType -> buildReportDetails(statusReportEntity, statusType))
                        .toList();
    }

    private ReportDetails buildReportDetails(StatusReportingFields statusReportEntity, ProductStatusType statusType) {
        Long count = getCountByStatusType(statusReportEntity, statusType);
        if (count == null) {
            count = 0L;
        }
        return ReportDetails.builder().count(count).characteristic(new ArrayList<>(List.of(getCharacteristicForStatus(statusType)))).build();
    }


    private Long getCountByStatusType(StatusReportingFields statusReportEntity, ProductStatusType statusType) {
        return switch (statusType) {
            case ABORTED -> statusReportEntity.getAbortedCount();
            case ACTIVE -> statusReportEntity.getActiveCount();
            case CANCELLED -> statusReportEntity.getCancelledCount();
            case CREATED -> statusReportEntity.getCreatedCount();
            case TERMINATED -> statusReportEntity.getTerminatedCount();
            case SOLD -> statusReportEntity.getSoldCount();
        };
    }

    private ReportCharacteristic getCharacteristicForStatus(ProductStatusType productStatusType) {
        return getCharacteristic("status", productStatusType.getValue());
    }

    private ReportCharacteristic getCharacteristic(String name, String value) {
        return ReportCharacteristic.builder().name(name).value(value).build();
    }

}
