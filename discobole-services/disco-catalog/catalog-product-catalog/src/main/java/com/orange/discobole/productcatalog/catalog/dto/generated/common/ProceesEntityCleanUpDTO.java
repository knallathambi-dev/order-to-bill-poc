// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.dto.generated.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;


import java.time.OffsetDateTime;

public class ProceesEntityCleanUpDTO {
    @JsonProperty("processFlowSpecification")
    @NotBlank
    private String processFlowSpecification;

    @JsonProperty("lifeCycleStatus")
    private String lifeCycleStatus="ALL";
    @JsonProperty("deletionStartDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime deletionStartDate = null;


    public @NotBlank String getProcessFlowSpecification() {
        return processFlowSpecification;
    }

    public void setProcessFlowSpecification(String processFlowSpecification) {
        this.processFlowSpecification = processFlowSpecification;
    }

    public String getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    public void setLifeCycleStatus(String lifeCycleStatus) {
        this.lifeCycleStatus = lifeCycleStatus;
    }

    public OffsetDateTime getDeletionStartDate() {
        return deletionStartDate;
    }

    public void setDeletionStartDate(OffsetDateTime deletionStartDate) {
        this.deletionStartDate = deletionStartDate;
    }
}
