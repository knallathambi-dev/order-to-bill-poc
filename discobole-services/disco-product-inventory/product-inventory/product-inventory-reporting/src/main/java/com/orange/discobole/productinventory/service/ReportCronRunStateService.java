// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.model.ReportCronRunState;
import com.orange.discobole.productinventory.repository.ReportCronRunStateRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
@Service
public class ReportCronRunStateService {

    private final ReportCronRunStateRepository reportCronRunStateRepository;


    public boolean hasRunForDate(LocalDate date) {
        return reportCronRunStateRepository.findFirstByDate(date)
                .map(ReportCronRunState::isHasRun)
                .orElse(false);
    }

    public void markAsRunForDate(LocalDate date) {
        ReportCronRunState state = new ReportCronRunState();
        state.setDate(date);
        state.setHasRun(true);
        reportCronRunStateRepository.save(state);
    }
}
