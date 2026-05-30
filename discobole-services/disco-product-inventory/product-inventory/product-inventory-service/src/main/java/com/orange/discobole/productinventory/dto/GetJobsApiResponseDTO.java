// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.dto;

import com.orange.discobole.productinventory.dto.v1.Job;
import lombok.*;

import java.util.List;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetJobsApiResponseDTO {
    private List<Job> jobs;
    private PageableTMF pageableTMF;
    private boolean dataFitInOnPage;
}
