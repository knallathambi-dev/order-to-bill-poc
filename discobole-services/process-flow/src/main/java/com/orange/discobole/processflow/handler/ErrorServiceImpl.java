// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.handler;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.dto.generated.Error;

import jakarta.annotation.Resource;

@Service
public class ErrorServiceImpl implements ErrorService {
	@Resource
	private MongoTemplate mongoTemplate;

	@Override
	public Error fetchErrorById(String id) {
		Error d = mongoTemplate.findById(id, Error.class);
		return d;
	}

	@Override
	public List<Error> saveError(List<Error> errors) {
		//return (List<ErrorRepresentationDb>) mongoTemplate.insertAll(error);
		for (Error error : errors) {
			mongoTemplate.save(error);
		}
		
		 return errors;
		//return mongoTemplate.saveAll(error, "errorRepresentationDb");		
	}

	@Override
	public List<Error> fetchError() {
		return mongoTemplate.findAll(Error.class);
	}

	

//	@Override
//	public void updateErrorRepresentation(String categoryId, ErrorRepresentation update) {
//
//		Query query = new Query();
//		query.addCriteria(Criteria.where("_id").is(""));
//		UpdateResult updateResult = mongoTemplate.updateFirst(query, (UpdateDefinition) update, ErrorRepresentation.class);
//		long modifiedCount = updateResult.getModifiedCount();
//		if (modifiedCount == 0)
//			throw new RuntimeException("Record Not Found");
//
//	}

}
