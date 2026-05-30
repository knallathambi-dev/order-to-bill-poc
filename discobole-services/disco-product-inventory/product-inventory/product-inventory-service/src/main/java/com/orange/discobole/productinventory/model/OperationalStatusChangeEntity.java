// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperationalStatusChangeEntity {
    private OffsetDateTime changeDate;
    private String changeReason;
    private ProductOperationalStatusType status;
    @JsonProperty("@type")
    @JsonAlias("atType")
    private String atType;
    @JsonProperty("@referredType")
    @JsonAlias("atReferredType")
    private String atReferredType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OperationalStatusChangeEntity that)) {
            return false;
        }
        return Objects.equals(changeDate, that.changeDate) && status == that.status;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
