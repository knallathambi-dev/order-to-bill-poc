// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.repository;

import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.OrchestrationPlanNodeState;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.cood.enums.State;
import com.orange.discobole.orderorchestration.orchestrationdelivery.model.*;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface OrchestrationPlanRepository extends MongoRepository<OrchestrationPlan, String> {

    Optional<OrchestrationPlan> findOrchestrationPlanById(String planId);

    Optional<OrchestrationPlan> findOrchestrationPlanByOrchestrationPlanNodes_id(String orchestrationPlanNodeID);

    Stream<OrchestrationPlan> findOrchestrationPlansByStateAndRequestedDeliveryDateIsBefore(State state, Instant date);

    @Update("{ '$set' : {'state' :  ?1} }")
    @Query("{'_id' : ?0}")
    int updateStateById(String planId, State state);

    @Update("{ '$set' : {'orchestrationPlanNodes.$.relatedProduct' : ?1} }")
    @Query("{'orchestrationPlanNodes._id' : ?0}")
    int updateOrchestrationPlanNodesRelatedProductById(String nodeId, List<RelatedProduct> relatedProducts);

    @Update("{ '$set' : { 'orchestrationPlanNodes.$.state' : ?1, 'orchestrationPlanNodes.$.previousState' : ?2} }")
    @Query("{'orchestrationPlanNodes._id' : ?0}")
    int updateOrchestrationPlanNodesStateById(String nodeId, OrchestrationPlanNodeState orchestrationPlanNodeState, OrchestrationPlanNodeState previousState);


    @Update("{ '$set' : {'orchestrationPlanNodes.$.state' : ?2, 'orchestrationPlanNodes.$.errorMessage' : ?1, 'orchestrationPlanNodes.$.previousState' : ?3} }")
    @Query("{'orchestrationPlanNodes._id' : ?0}")
    int updateOrchestrationPlanNodesStateAndErrorMessageById(
            String nodeId,
            List<OrchestrationNodeErrorMessage> errorMessage,
            OrchestrationPlanNodeState orchestrationPlanNodeState,
            OrchestrationPlanNodeState previousState);

    List<OrchestrationPlan> findOrchestrationPlanByState(State state);

    List<OrchestrationPlan> findOrchestrationPlanByStateIn(List<State> states);

    @Aggregation(pipeline = {
            "{ '$match': { 'orchestrationPlanNodes.id': ?0 } }",
            "{ '$unwind': '$orchestrationPlanNodes' }",
            "{ '$match': { 'orchestrationPlanNodes.id': ?0 } }",
            "{ '$project': { 'node': '$orchestrationPlanNodes' } }",
            "{ '$replaceRoot': { 'newRoot': '$node' } }"
    })
    Optional<OrchestrationPlanNode> findOrchestrationPlanNodeById(String nodeId);

    Optional<OrchestrationPlan> findOrchestrationPlanByRelatedProductOrder_Id(String orderId);

    @Update("{ '$set' : {'previousState': ?2, 'state' :  ?3 , 'errorMessage' : ?1 } }")
    @Query("{'_id' : ?0}")
    int updateOrchestrationPlanStateAndErrorMessagesById(String planId, List<OrchestrationPlanErrorMessage> errorMessages, State previousState, State state);

}
