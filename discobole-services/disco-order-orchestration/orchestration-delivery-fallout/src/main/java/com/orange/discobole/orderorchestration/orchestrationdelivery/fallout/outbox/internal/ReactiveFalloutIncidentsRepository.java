// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.outbox.internal;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ReactiveFalloutIncidentsRepository extends ReactiveMongoRepository<FalloutIncident, String> {
    Flux<FalloutIncident> findAllByStateInAndModificationDateIsBefore(List<State> stateList, OffsetDateTime modificationDate);
}
