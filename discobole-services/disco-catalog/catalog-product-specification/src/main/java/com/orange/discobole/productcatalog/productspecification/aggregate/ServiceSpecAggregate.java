// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.aggregate;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import com.orange.discobole.productcatalog.productspecification.constant.CharacteristicTypes;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.CharacteristicSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productspecification.command.servicespec.ServiceSpecAttributeValueChangeCommand;
import com.orange.discobole.productcatalog.productspecification.command.servicespec.ServiceSpecificationStateChangeCommand;
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecAttributeUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecExpurgedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecNotificationSentEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecReplicatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.ServiceSpecStatusUpdatedEvent;
import com.orange.discobole.productcatalog.productspecification.event.servicespec.StatusVerifiedEvent;

/**
 * The ServiceSpecAggregate type consists logic to handle new and modify CFS.
 *
 * @author Diksha Srivastava
 * @author Ankur Singh
 * @since 1.0
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Aggregate
public class ServiceSpecAggregate {

	private static final Logger LOGGER = LogManager.getLogger(ServiceSpecAggregate.class);

	private static final String DISCO_PS_INVALID_SERVICE_SPEC_HREF = "DISCO_PS_INVALID_SERVICE_SPEC_HREF";

	private static final String DISCO_PS_INVALID_SERVICE_SPEC_RELATED_RESOURCE = "DISCO_PS_INVALID_SERVICE_SPEC_RELATED_RESOURCE";

	private static final String DISCO_PS_INVALID_SERVICE_SPEC_NOT_EXISTS = "DISCO_PS_INVALID_SERVICE_SPEC_NOT_EXISTS";

	private static final String DISCO_PS_SERVICE_SPECIFICATION_RECEIVED_ALREADY = "DISCO_PS_SERVICE_SPECIFICATION_RECEIVED_ALREADY";

	private static final String DISCO_PS_INVALID_SERVICE_SPECIFICATION_STATUS_RECEIVED = "DISCO_PS_INVALID_SERVICE_SPECIFICATION_STATUS_RECEIVED";

	private static final String DISCO_PS_DUPLICATE_SERVICE_SPEC = "DISCO_PS_DUPLICATE_SERVICE_SPEC";

	private static final String DISCO_INVALID_CFS_CHAR_TYPE="DISCO_INVALID_CFS_CHAR_TYPE";

	@AggregateIdentifier
	private String aggregateId;
	private String serviceSpecId;
	private ServiceSpecLifeCycleEnum status;
	String existingCfsId;
	String existingLifeCycle;
	OffsetDateTime existingCfsTimeOccurred;


	public ServiceSpecAggregate() {

	}

	/**
	 * Process cfs event.
	 *
	 * @param command the service specification state change command
	 * 
	 */
	@CommandHandler
	public ServiceSpecAggregate(ServiceSpecificationStateChangeCommand command) {
		LOGGER.info("ServiceSpecificationStateChangeCommand : {}", command);
		LOGGER.info("ServiceSpecificationStateChangeCommand 2");
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		com.orange.discobole.productcatalog.productspecification.dto.generated.Event event = command.getEvent();
		try {
			LOGGER.info("validated events for applying:");
			ServiceSpecification serviceSpecification = objectMapper.convertValue(event.getEvent(),
					ServiceSpecification.class);
			serviceSpecification.setLastUpdate(event.getTimeOcurred());
			String cfsId = serviceSpecification.getId();
			String lifecycleStatus = serviceSpecification.getLifecycleStatus();
			this.aggregateId = command.getAggregateId();
			LOGGER.info("Applying ServiceSpecExpurgedEvent event");
			AggregateLifecycle.apply(new ServiceSpecExpurgedEvent(command.getAggregateId(), serviceSpecification));

			if (serviceSpecId != null && serviceSpecId.equalsIgnoreCase(cfsId)) {
				throw new DiscoManagedClientException(DISCO_PS_DUPLICATE_SERVICE_SPEC);
			} else {
				if (verifyLifeCycleOfServiceSpec(serviceSpecification.getLifecycleStatus())) {

					throw new DiscoManagedClientException(DISCO_PS_DUPLICATE_SERVICE_SPEC);
				}
				LOGGER.info("validateCFS");
				validateCFS(serviceSpecification);
				LOGGER.info("Applying StatusVerifiedEvent");
				AggregateLifecycle.apply(new StatusVerifiedEvent(command.getAggregateId(), cfsId,ServiceSpecLifeCycleEnum.from(lifecycleStatus), status));
				LOGGER.info("Applying ServiceSpecReplicatedEvent");
				AggregateLifecycle.apply(new ServiceSpecReplicatedEvent(command.getAggregateId(), serviceSpecification));
				LOGGER.info("Applying ServiceSpecNotificationSentEvent");
				AggregateLifecycle.apply(new ServiceSpecNotificationSentEvent(command.getAggregateId(), serviceSpecification));

			}

		} catch (Exception e) {
			LOGGER.error("Error while parsing event: {}, exception raised: {}", event, e);
		}
	}



	private void validateCFS(ServiceSpecification serviceSpecification) {
		if(serviceSpecification.getServiceSpecCharacteristic()!=null)
			validateCFSChar(serviceSpecification.getServiceSpecCharacteristic());

		if (serviceSpecification.getHref() == null || serviceSpecification.getHref().isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICE_SPEC_HREF);
				
		}
		
		if(serviceSpecification.getRelatedResource() == null || serviceSpecification.getRelatedResource().isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICE_SPEC_RELATED_RESOURCE);
		}else {
			for(RelatedResource relatedResource:serviceSpecification.getRelatedResource()) {
				if(relatedResource.getId()==null || relatedResource.getRole()==null || relatedResource.getName()==null || relatedResource.getHref()==null) {
					throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICE_SPEC_RELATED_RESOURCE);
				}
				
			}
		}
		
	}
	private void validateCFSChar( List<CharacteristicSpecification> serviceSpecificationChar){
		for(CharacteristicSpecification characteristic :serviceSpecificationChar){
			if(CharacteristicTypes.fromValue(characteristic.getType())==null)
				throw new DiscoManagedClientException(DISCO_INVALID_CFS_CHAR_TYPE);

		}
	}
	@EventSourcingHandler
	public void on(ServiceSpecNotificationSentEvent event) {
		this.aggregateId = event.getAggregateId();
	}
	
	@EventSourcingHandler
	public void on(ServiceSpecExpurgedEvent event) {
		this.aggregateId = event.getAggregateId();
	}

	@EventSourcingHandler
	public void on(ServiceSpecReplicatedEvent event) {
		this.aggregateId = event.getAggregateId();
		this.serviceSpecId = event.getServiceSpecification().getId();
		this.existingCfsTimeOccurred=event.getServiceSpecification().getLastUpdate();
	}

	@EventSourcingHandler
	public void on(ServiceSpecAttributeUpdatedEvent event) {
		this.aggregateId = event.getAggregateId();
		this.existingCfsTimeOccurred=event.getServiceSpecification().getLastUpdate();
	}
	
	@EventSourcingHandler
	public void on(StatusVerifiedEvent event) {
		this.aggregateId = event.getAggregateId();
		this.status = event.getLifecycleStatus();
	}
	
	@EventSourcingHandler
	public void on(ServiceSpecStatusUpdatedEvent event) {
		this.aggregateId = event.getAggregateId();
		this.status = event.getLifecycleStatus();
		this.existingCfsTimeOccurred=event.getCfsTimeOccurred();
	}

	/**
	 * Process list.
	 *
	 * @param attributeValueChangeCommand the attribute value change command
	 * 
	 */
	@CommandHandler
	public void serviceSpecAttributeValueChange(ServiceSpecAttributeValueChangeCommand attributeValueChangeCommand) {
		LOGGER.info("ServiceSpecAttributeValueChangeCommand : {}", attributeValueChangeCommand);
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		com.orange.discobole.productcatalog.productspecification.dto.generated.Event event = attributeValueChangeCommand.getEvent();
		try {
			ServiceSpecification serviceSpecification = objectMapper.convertValue(event.getEvent(),
					ServiceSpecification.class);
			String cfsId = serviceSpecification.getId();
			String lifecycle = serviceSpecification.getLifecycleStatus();
			serviceSpecification.setLastUpdate(event.getTimeOcurred());
			String existingCfsID = this.serviceSpecId;
			String existingCFSLifeCycle = this.status.toString();
			OffsetDateTime existingCFSTimeOccurred = this.existingCfsTimeOccurred;
			String aggregateID=this.aggregateId;
			if (!existingCfsID.equalsIgnoreCase(cfsId)) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICE_SPEC_NOT_EXISTS);
			}
			AggregateLifecycle.apply(
					new ServiceSpecExpurgedEvent(aggregateID, serviceSpecification));

			if (checkEarlyTimeOccurred(serviceSpecification.getLastUpdate(), existingCFSTimeOccurred)) {
				throw new DiscoManagedClientException(DISCO_PS_SERVICE_SPECIFICATION_RECEIVED_ALREADY);
			}

			if (!verifyStatus(lifecycle, existingCFSLifeCycle)) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICE_SPECIFICATION_STATUS_RECEIVED);
			} else if (lifecycle.equals(existingCFSLifeCycle)) {
				validateCFS(serviceSpecification);
				AggregateLifecycle.apply(new ServiceSpecAttributeUpdatedEvent(
						aggregateID, serviceSpecification));
			} else {
				AggregateLifecycle.apply(new StatusVerifiedEvent(aggregateID, cfsId,
						ServiceSpecLifeCycleEnum.from(lifecycle), ServiceSpecLifeCycleEnum.from(existingCFSLifeCycle)));
				AggregateLifecycle.apply(new ServiceSpecStatusUpdatedEvent(aggregateID,
						cfsId, ServiceSpecLifeCycleEnum.from(lifecycle), serviceSpecification.getLastUpdate()));
			}

		} catch (Exception e) {
			LOGGER.error("Error while parsing event: {}, exception raised: {}", event, e);
		}
	}

	/**
	 * Returns true if newLastUpdate time is before to oldLastUpdate time, false
	 * otherwise.
	 *
	 * @param newLastUpdate the new last update
	 * @param oldLastUpdate the old last update
	 * @return boolean
	 */
	private boolean checkEarlyTimeOccurred(OffsetDateTime newLastUpdate, OffsetDateTime oldLastUpdate) {
		if (newLastUpdate == null)
			return true;
		if (oldLastUpdate == null)
			return false;
		return newLastUpdate.isEqual(oldLastUpdate) || newLastUpdate.isBefore(oldLastUpdate);
	}

	/**
	 * Returns true if lifeCycleStatus is according to the life cycle of the service
	 * spec, false otherwise.
	 *
	 * @param newLifeCycle      the new life cycle
	 * @param existingLifeCycle the existing life cycle
	 * @return boolean
	 */
	private boolean verifyStatus(String newLifeCycle, String existingLifeCycle) {
		if (newLifeCycle == null || existingLifeCycle == null || verifyLifeCycleOfServiceSpec(newLifeCycle))
			return false;
		else if (ServiceSpecLifeCycleEnum.ACTIVE.toString().equals(newLifeCycle)
				&& (ServiceSpecLifeCycleEnum.from(existingLifeCycle) == ServiceSpecLifeCycleEnum.LAUNCHED
						|| ServiceSpecLifeCycleEnum.from(existingLifeCycle) == ServiceSpecLifeCycleEnum.UNAVAILABLE))
			return false;
		else
			return !ServiceSpecLifeCycleEnum.LAUNCHED.toString().equals(newLifeCycle)
					|| ServiceSpecLifeCycleEnum.from(existingLifeCycle) != ServiceSpecLifeCycleEnum.UNAVAILABLE;
	}

	/**
	 * Returns true if and only if this string exist in serviceSpecEnum enum
	 *
	 * @param lifecycleStatus the life cycle status
	 * @return boolean
	 */
	private boolean verifyLifeCycleOfServiceSpec(String lifecycleStatus) {
		return Arrays.stream(ServiceSpecLifeCycleEnum.values()).noneMatch(c -> c.getStatus().equals(lifecycleStatus));
	}
}
