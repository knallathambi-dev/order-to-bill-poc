// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.aggregate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productspecification.command.stockitem.StockItemAttributeValueChangeCommand;
import com.orange.discobole.productcatalog.productspecification.command.stockitem.StockItemStateChangeCommand;
import com.orange.discobole.productcatalog.productspecification.constant.CharacteristicTypes;
import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItem;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItemCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItemCharacteristicValue;
import com.orange.discobole.productcatalog.productspecification.event.stockitem.*;

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

import java.time.OffsetDateTime;
import java.util.*;

/**
 * The StockItemAggregate consists logic to handle new and modify stock item.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Aggregate
public class StockItemAggregate {
    private static final Logger LOGGER = LogManager.getLogger(StockItemAggregate.class);


	private static final String DISCO_PS_DUPLICATE_STOCKITEM = "DISCO_PS_DUPLICATE_STOCKITEM";


	private static final String DISCO_PS_INVALID_STOCKITEM_STATUS_RECEIVED = "DISCO_PS_INVALID_STOCKITEM_STATUS_RECEIVED";


	private static final String DISCO_PS_STOCKITEM_NOT_EXISTS = "DISCO_PS_STOCKITEM_NOT_EXISTS";


	private static final String DISCO_PS_STOCKITEM_RECEIVED_ALREADY = "DISCO_PS_STOCKITEM_RECEIVED_ALREADY";

    private static final String DISCO_INVALID_STOCK_CHAR_TYPE="DISCO_INVALID_STOCK_CHAR_TYPE";
   
	@AggregateIdentifier
	private String aggregateId;
	private String stockItmId;
	private StockItemLifeCycleEnum status;

	private OffsetDateTime stockItemTimeOccurred;
	

	   public StockItemAggregate() {
  }

/* StockItemAggregate constructor is created in which we had 
 * written the logic of Stock creation.AggregateLifecycle.apply() is used
 *  to map Event Handlers will generate events in db.  */
	   @CommandHandler
    public StockItemAggregate(StockItemStateChangeCommand command){
		   LOGGER.info("in StockItemAggregate method of StockItemAggregrateJava");
		   ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
   
        com.orange.discobole.productcatalog.productspecification.dto.generated.Event event = command.getEvent();
        try{
           StockItem stockItem = objectMapper.convertValue(event.getEvent(),StockItem.class);
           String stockItemId = stockItem.getId();
           String lifeCycleStatus = stockItem.getState();
           stockItem.setLastUpdate(event.getTimeOcurred());
        
       	AggregateLifecycle.apply(new StockItemExpurgedEvent(command.getAggregrateId(),stockItem));

           if(stockItmId != null && stockItmId.equalsIgnoreCase(stockItemId)){
            
        	    throw new DiscoManagedClientException(DISCO_PS_DUPLICATE_STOCKITEM);
           } else{
               if(!lifeCycleStatus.equals(StockItemLifeCycleEnum.ACTIVE.toString()) && !lifeCycleStatus.equals(StockItemLifeCycleEnum.LAUNCHED.toString())){

            	   throw new DiscoManagedClientException(DISCO_PS_INVALID_STOCKITEM_STATUS_RECEIVED);
               }
               validateStockChar(stockItem);


			AggregateLifecycle.apply(new StockItemStatusVerifyEvent(command.getAggregrateId(),stockItem.getId(),status, StockItemLifeCycleEnum.from(lifeCycleStatus)));
			AggregateLifecycle.apply(new StockItemReplicatedEvent(command.getAggregrateId(),stockItem));
			AggregateLifecycle.apply(new StockItemNotificationSentEvent(command.getAggregrateId(),stockItem));
           }

        } catch (Exception e){
            LOGGER.error("Error while parsing event: {}, exception raised: {}", event, e);
     
        }
    }
	   /* @EventSourcingHandler -- all state changes are defined in the @EventSourcingHandlers
	    * on method----  we are passing variables which required for the particular events  */
	   
		@EventSourcingHandler
		public void on(StockItemExpurgedEvent event) {
			this.aggregateId = event.getAggregrateId();
		}
	   
		@EventSourcingHandler
		public void on(StockItemNotificationSentEvent event) {
			this.aggregateId = event.getAggregrateId();
		}
		@EventSourcingHandler
		public void on(StockItemStatusVerifyEvent event) {
			this.aggregateId = event.getAggregratedId();
			this.status = event.getLifecycleStatus();
					 
		}
		@EventSourcingHandler
		public void on(StockItemReplicatedEvent event) {
			this.aggregateId = event.getaggregateId();
			StockItem stockItem = event.getStockItem();
			this.stockItmId  = stockItem.getId();
			this.stockItemTimeOccurred = stockItem.getLastUpdate();
			
			
					 
		}
		

    /**
     * Process list.
     *
     * @param attributeValueChangeCommand the attribute value change command
     * @return the list
     */
		  @CommandHandler
    public void StockItemAttributeValueChangeCommand(StockItemAttributeValueChangeCommand attributeValueChangeCommand) {
			  LOGGER.info("in StockItemAttributeValueChangeCommand of StocKItemAggregrate class");
			  ObjectMapper objectMapper = new ObjectMapper();
        List<Event> eventList = new ArrayList<>();
        objectMapper.registerModule(new JavaTimeModule());
        com.orange.discobole.productcatalog.productspecification.dto.generated.Event event = attributeValueChangeCommand.getEvent();
        try {
            StockItem stockItem = objectMapper.convertValue(event.getEvent(),
                    StockItem.class);
            String stockItemId = stockItem.getId();
            String lifecycle = stockItem.getState();
            TimePeriod timePeriod = new TimePeriod();
            timePeriod.setStartDateTime(event.getTimeOcurred());
            stockItem.setValidFor(timePeriod);
            String existingStockItemId = this.stockItmId;
            String existingLifeCycle =  this.status.toString();
            OffsetDateTime existingStockItemTimeOccurred = this.stockItemTimeOccurred;
            stockItem.setLastUpdate(event.getTimeOcurred());

            if (!existingStockItemId.equalsIgnoreCase(stockItemId)) {
            	   throw new DiscoManagedClientException(DISCO_PS_STOCKITEM_NOT_EXISTS);
            }
     
        	AggregateLifecycle.apply(new StockItemExpurgedEvent(attributeValueChangeCommand.getAggregrateId(),stockItem));

            if (checkEarlyTimeOccurred(stockItem.getLastUpdate(), existingStockItemTimeOccurred)) {

            	  throw new DiscoManagedClientException(DISCO_PS_STOCKITEM_RECEIVED_ALREADY);
            }

            if (!verifyStatus(lifecycle, existingLifeCycle)) {

            	 throw new DiscoManagedClientException(DISCO_PS_INVALID_STOCKITEM_STATUS_RECEIVED);
            } else if (lifecycle.equals(existingLifeCycle)) {
             
                AggregateLifecycle.apply(new StockItemAttributeUpdatedEvent(attributeValueChangeCommand.getAggregrateId(),stockItem));
            } else {

            	AggregateLifecycle.apply(new StockItemStatusVerifyEvent(attributeValueChangeCommand.getAggregrateId(),stockItem.getId(),StockItemLifeCycleEnum.from(existingLifeCycle),StockItemLifeCycleEnum.from(lifecycle)));
            	AggregateLifecycle.apply(new StockItemStatusUpdatedEvent(attributeValueChangeCommand.getAggregrateId(),stockItemId, StockItemLifeCycleEnum.from(lifecycle),
                        stockItem.getLastUpdate()));
            }

            LOGGER.info("Events Generated: {}", eventList);
       
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
     * Returns true if lifeCycleStatus is according to the life cycle of the stock item, false otherwise.
     *
     * @param newLifeCycle      the new life cycle
     * @param existingLifeCycle the existing life cycle
     * @return boolean
     */
    private boolean verifyStatus(String newLifeCycle, String existingLifeCycle) {
        if (newLifeCycle == null || existingLifeCycle == null || verifyLifeCycleOfStockItem(newLifeCycle))
            return false;
        else if (StockItemLifeCycleEnum.ACTIVE.toString().equals(newLifeCycle)
                && (StockItemLifeCycleEnum.from(existingLifeCycle) == StockItemLifeCycleEnum.LAUNCHED
                || StockItemLifeCycleEnum.from(existingLifeCycle) == StockItemLifeCycleEnum.UNAVAILABLE))
            return false;
        else
            return !StockItemLifeCycleEnum.LAUNCHED.toString().equals(newLifeCycle)
                    || StockItemLifeCycleEnum.from(existingLifeCycle) != StockItemLifeCycleEnum.UNAVAILABLE;
    }

    /**
     * Returns true if and only if this string exist in serviceSpecEnum enum
     *
     * @param lifecycleStatus the life cycle status
     * @return boolean
     */
    private boolean verifyLifeCycleOfStockItem(String lifecycleStatus) {
        return Arrays.stream(StockItemLifeCycleEnum.values()).noneMatch(c -> c.getStatus().equals(lifecycleStatus));
    }
    private void validateStockChar(StockItem stockItem){
        //check for StockItemType Characteristic
        if(stockItem.getStockItemType()!=null && stockItem.getStockItemType().getStockItemCharacteristic()!=null)
            for(StockItemCharacteristic characteristic:stockItem.getStockItemType().getStockItemCharacteristic()){
                validateChar(characteristic);
            }

        if(stockItem.getStockItemCharacteristicValue()!=null)
            for(StockItemCharacteristicValue characteristicValue:stockItem.getStockItemCharacteristicValue()){
                validateChar(characteristicValue.getStockItemCharacteristic());
            }
    }
    private void validateChar(StockItemCharacteristic characteristic){
        if(characteristic.getType()==null){
            characteristic.setType(CharacteristicTypes.STRING.getValue());
        }
        else if (!characteristic.getType().equals(CharacteristicTypes.STRING.getValue())){
            throw new DiscoManagedClientException(DISCO_INVALID_STOCK_CHAR_TYPE);
        }
    }

}
