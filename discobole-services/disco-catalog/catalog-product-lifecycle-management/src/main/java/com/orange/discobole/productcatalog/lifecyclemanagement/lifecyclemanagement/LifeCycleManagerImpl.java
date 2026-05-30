// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.*;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleEntitySelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.InvalidLifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.ProductSpecificationStateChangeEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.AdminQueryService;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.CommercialProductInstalledBaseQueryService;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.QueryService;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class LifeCycleManagerImpl implements LifeCycleManager {

    private static final String ENTIY_IS_IN_USE_IN_CPIB = "Entiy is in use in CPIB";
    private static final String DISCO_LS_INVALID_PS_LIFECYCLE_STATUS = "DISCO_LS_INVALID_PS_LIFECYCLE_STATUS";
    private static final String DISCO_LS_INVALID_BPO_LIFECYCLE_STATUS = "DISCO_LS_INVALID_BPO_LIFECYCLE_STATUS";

    @Resource
    private QueryService queryService;


    private CommercialProductInstalledBaseQueryService cpibQueryService;


    private AdminQueryService adminQueryService;

    @Autowired
    public LifeCycleManagerImpl(CommercialProductInstalledBaseQueryService cpibQueryService,AdminQueryService adminQueryService) {
        this.cpibQueryService = cpibQueryService;
        this.adminQueryService = adminQueryService;

    }


    @Override
    public List<Event> changeState(String aggregateId, String entityId, EntityType entityType, String nextState, String accessToken) {
        List<Event> eventList = new ArrayList<>();
        String entityTypeString = entityType.toString();
        switch (entityTypeString) {
            case "ProductSpecification":
                eventList = changeProductSpecState(aggregateId, entityId, entityType, nextState,accessToken);
                break;
            case "AtomicOffer":
                eventList = changeAtomicProductOfferingState(aggregateId, entityId, entityType, nextState,accessToken);
                break;
            case "ProductOfferingPrice":
                eventList = changeProductOfferingPriceState(aggregateId, entityId, entityType, nextState,accessToken);
                break;
            case "BundleProductOffering":
                eventList = changeBundleAndContractProductOfferingState(aggregateId, entityId, entityType, nextState,accessToken);
                break;
            case "Contract":
                eventList = changeBundleAndContractProductOfferingState(aggregateId, entityId, entityType, nextState, accessToken);
                break;
            default:
                break;
        }
        return eventList;
    }

    public List<Event> changeProductSpecState(String aggregateId, String entityId, EntityType entityType, String nextState, String accessToken) {
        List<Event> eventList = new ArrayList<>();
        ProductSpecification productSpecification = queryService.fetchProductSpecById(entityId, accessToken);
        String currentState = productSpecification.getLifecycleStatus().toString();

        updateVersion(productSpecification);


        if (nextState.equals(LifecycleState.OBSOLETE.getValue()) &&
                adminQueryService.fetchCPIBConfiguration(accessToken) &&
                cpibQueryService.fetchProductByProductSpecId(productSpecification.getId(), accessToken)) {

                    throw new DiscoManagedClientException(ENTIY_IS_IN_USE_IN_CPIB,productSpecification.getId(),productSpecification.getId());


        }
        Set<String> nextPossibleStates = getProductSpecificationNextPossibleStates(entityId,accessToken);

        if (nextPossibleStates.contains(nextState)) {
            OffsetDateTime lastUpdate = OffsetDateTime.now();
            eventList.add(new LifeCycleStateSelectedEvent(aggregateId, entityId, entityType, nextState, currentState, lastUpdate, productSpecification.getVersion()));
        } else {
            eventList.add(
                    new InvalidLifeCycleStateSelectedEvent(aggregateId, entityId, entityType, currentState, nextPossibleStates));
        }

        return List.copyOf(eventList);

    }

    private List<Event> changeAtomicProductOfferingState(String aggregateId, String entityId, EntityType entityType, String nextState, String accessToken) {
        List<Event> eventList = new ArrayList<>();
        ProductOffering productOffering = queryService.fetchProductOfferingById(entityId, accessToken);
        String currentState = productOffering.getLifecycleStatus().toString();
        Set<String> nextPossibleStates = getAtomicOfferNextPossibleStates(entityId,accessToken);

        updateVersion(productOffering);
        if (nextState.equals(LifecycleState.OBSOLETE.getValue()) &&
                adminQueryService.fetchCPIBConfiguration(accessToken) &&
                cpibQueryService.fetchProductByProductOfferingId(productOffering.getId(), accessToken) ) {

                    throw new DiscoManagedClientException(ENTIY_IS_IN_USE_IN_CPIB,productOffering.getId(),productOffering.getId());

        }
        if (nextPossibleStates.contains(nextState)) {
            OffsetDateTime lastUpdate = OffsetDateTime.now();
            eventList.add(new LifeCycleStateSelectedEvent(aggregateId, entityId, entityType, nextState, currentState, lastUpdate, productOffering.getVersion()));
            String productSpecId = productOffering.getProductSpecification().getId();
            ProductSpecification productSpecification = queryService.fetchProductSpecById(productSpecId, accessToken);
            List<ProductOffering> productOfferings = queryService.fetchProductOfferingsByProductSpecId(productSpecId,accessToken);
            // checking ps is in launched state or not
            if (nextState.equals(LifecycleState.LAUNCHED.getValue()) && !(productSpecification.getLifecycleStatus().equals(ProductSpecificationLifecycle.LAUNCHED))) {
                throw new DiscoManagedClientException(DISCO_LS_INVALID_PS_LIFECYCLE_STATUS,null,productSpecId);
            }
            List<ProductOffering> atomicProductOfferings = productOfferings.stream()
                    .filter(po -> po.getLifecycleStatus().equals(ProductOfferingLifecycle.ACTIVE)
                            || po.getLifecycleStatus().equals(ProductOfferingLifecycle.LAUNCHED)).
                    toList();
            if (atomicProductOfferings.isEmpty()
                    && productSpecification.getLifecycleStatus().equals(ProductSpecificationLifecycle.UNAVAILABLE)) {
                eventList.add(new ProductSpecificationStateChangeEvent(productSpecId,
                        productSpecification.getLifecycleStatus(), ProductSpecificationLifecycle.RETIRED));
            }

        } else {
            eventList.add(
                    new InvalidLifeCycleStateSelectedEvent(aggregateId, entityId, entityType, currentState, nextPossibleStates));
        }

        return List.copyOf(eventList);
    }


    private List<Event> changeBundleAndContractProductOfferingState(String aggregateId, String entityId, EntityType entityType, String nextState, String accessToken) {

        List<Event> eventList = new ArrayList<>();
        ProductOffering productOffering = queryService.fetchProductOfferingById(entityId,accessToken);
        String currentState = productOffering.getLifecycleStatus().getValue();
        Set<String> nextPossibleStates = getBundleAndContractProductOfferingNextPossibleStates(entityId,accessToken);
        if (nextState.equals(LifecycleState.OBSOLETE.getValue()) && adminQueryService.fetchCPIBConfiguration(accessToken) && cpibQueryService.fetchProductByProductOfferingId(productOffering.getId(), accessToken)) {
            throw new DiscoManagedClientException(ENTIY_IS_IN_USE_IN_CPIB,productOffering.getId(),productOffering.getId());
        }
        updateVersion(productOffering);
        if (nextPossibleStates.contains(nextState)) {
            OffsetDateTime lastUpdate = OffsetDateTime.now();
			// checking bundled product offerings are in launched state or not.
            if (nextState.equals(LifecycleState.LAUNCHED.getValue())) {
                validateUnderlyingPOsLifecycle(productOffering,accessToken);
            }
            eventList.add(new LifeCycleStateSelectedEvent(aggregateId, entityId, entityType, nextState, currentState, lastUpdate,productOffering.getVersion()));
        } else {
            eventList.add(
                    new InvalidLifeCycleStateSelectedEvent(aggregateId, entityId, entityType, currentState, nextPossibleStates));
        }

        return List.copyOf(eventList);
    }
    public void validateUnderlyingPOsLifecycle(ProductOffering productOffering,String accessToken){
        StringBuilder bpoIDs=new StringBuilder("[");
        for (BundledProductOffering bpos : productOffering.getBundledProductOffering()) {
            ProductOffering bpo = queryService.fetchProductOfferingById(bpos.getId(), accessToken);
            if (!(bpo.getLifecycleStatus().getValue().equalsIgnoreCase(ProductOfferingLifecycle.LAUNCHED.getValue()))) {
                bpoIDs.append(bpo.getId()).append(", ");
            }
        }
        if(bpoIDs.length()>1){
            bpoIDs.deleteCharAt(bpoIDs.length() - 2).append(" ]");
            throw new DiscoManagedClientException(DISCO_LS_INVALID_BPO_LIFECYCLE_STATUS, bpoIDs.toString(), bpoIDs.toString());
        }
    }
    public List<Event> changeProductOfferingPriceState(String aggregateId, String entityId, EntityType entityType, String nextState, String accessToken) {

        List<Event> eventList = new ArrayList<>();
        ProductOfferingPrice productOfferingPrice = queryService.getProductOfferingPrice(entityId, accessToken);
        if (null == productOfferingPrice) {
            eventList.add(
                    new InvalidLifeCycleEntitySelectedEvent(aggregateId, entityId, entityType));
            return List.copyOf(eventList);
        }
        String currentState = productOfferingPrice.getLifecycleStatus().toString();

        Set<String> nextPossibleStates = getProductOfferingPriceNextPossibleStates(entityId,accessToken);

        if (!nextPossibleStates.isEmpty() && nextPossibleStates.contains(nextState)) {
            OffsetDateTime lastUpdate = OffsetDateTime.now();
            eventList.add(new LifeCycleStateSelectedEvent(aggregateId, entityId, entityType, nextState, currentState, lastUpdate,null));
        } else {
            eventList.add(
                    new InvalidLifeCycleStateSelectedEvent(aggregateId, entityId, entityType, currentState, nextPossibleStates));
        }

        return List.copyOf(eventList);


    }

    public Set<String> getNextPossibleStates(String entityId, EntityType entityType,String accessToken) {
        String entityTypeString = entityType.toString();
        Set<String> possibleStates = null;
        switch (entityTypeString) {
            case "ProductSpecification":
                possibleStates = getProductSpecificationNextPossibleStates(entityId,accessToken);
                break;
            case "AtomicOffer":
                possibleStates = getAtomicOfferNextPossibleStates(entityId,accessToken);
                break;
            case "ProductOfferingPrice":
                possibleStates = getProductOfferingPriceNextPossibleStates(entityId,accessToken);
                break;
            case "BundleProductOffering":
                possibleStates = getBundleAndContractProductOfferingNextPossibleStates(entityId,accessToken);
                break;
            case "Contract":
                possibleStates = getBundleAndContractProductOfferingNextPossibleStates(entityId,accessToken);
                break;
            default:
                break;
        }
        return possibleStates;
    }

    private Set<String> getProductSpecificationNextPossibleStates(String entityId, String accessToken) {
        ProductSpecification productSpecification = queryService.fetchProductSpecById(entityId, accessToken);
        String currentState = productSpecification.getLifecycleStatus().toString();
        List<ProductOffering> productOfferings = queryService.fetchProductOfferingsByProductSpecId(entityId, accessToken);
        ProductSpecNextSates productSpecNextSates = new ProductSpecNextSates();
        return productSpecNextSates.getNextPossibleStates(currentState, productOfferings);

    }

    private Set<String> getAtomicOfferNextPossibleStates(String entityId, String accessToken) {
        ProductOffering productOffering = queryService.fetchProductOfferingById(entityId, accessToken);
        String currentState = productOffering.getLifecycleStatus().toString();
        List<ProductOffering> bundleProductOfferings = queryService
                .fetchBundleProductOfferingByAtomicProductOfferingId(entityId, accessToken);
        ProductOfferingNextSates productOfferingNextSates = new ProductOfferingNextSates();
        return productOfferingNextSates.getNextPossibleStates(currentState, bundleProductOfferings);
    }


    private Set<String> getBundleAndContractProductOfferingNextPossibleStates(String entityId, String accessToken) {
        ProductOffering productOffering = queryService.fetchProductOfferingById(entityId, accessToken);
        String currentState = productOffering.getLifecycleStatus().toString();
        List<ProductOffering> bundleProductOfferings = queryService
                .fetchBundleAndContractProductOfferingByBundlingProductOfferingId(entityId, accessToken);
        ProductOfferingBundlingNextStates productOfferingBundlingNextSates = new ProductOfferingBundlingNextStates();
        return productOfferingBundlingNextSates.getNextPossibleStates(currentState, bundleProductOfferings);
    }

    private Set<String> getProductOfferingPriceNextPossibleStates(String entityId, String accessToken) {

        ProductOfferingPrice productOfferingPrice = queryService.getProductOfferingPrice(entityId, accessToken);
        if (null == productOfferingPrice) {
            return new HashSet<>();
        }
        String currentState = productOfferingPrice.getLifecycleStatus().toString();
        List<ProductOffering> productOfferings = queryService.fetchProductOfferingsByProductOfferingPriceId(entityId, accessToken);
        List<ProductOfferingPrice> productOfferingsPrices = queryService.getProductOfferingPricesByProductOfferingPriceId(entityId, accessToken);

        ProductOfferingPriceNextStates productOfferingPriceNextStates = new ProductOfferingPriceNextStates();

        return productOfferingPriceNextStates.getNextPossibleStates(currentState, productOfferings, productOfferingsPrices, productOfferingPrice.getVersion());
    }

//    // Update the version of the modified product offering
//    private void updateVersion(ProductSpecification storedProductSpecification) {
//        String version = storedProductSpecification.getVersion();
//
//
//      //  active se launched  = +1
//     //   intest  no incre
//        //launched same
//
//        if (storedProductSpecification.getLifecycleStatus().name().equals("ACTIVE")) {
//            version = (Integer.parseInt(version.substring(0, 1)) + 1) + ".0";
//            //version = version.substring(0, 2) + (Integer.parseInt(version.substring(2)) + 1);
//        }
//        //this exclude
////        else if (!ProductOfferingLifecycle.INTEST.equals(storedProductSpecification.getLifecycleStatus())) {
////          //  version = version.substring(0, 2) + (Integer.parseInt(version.substring(2)) + 1);
////            version = (Integer.parseInt(version.substring(0, 1)) + 1) + ".0";
////        }
//
//        storedProductSpecification.setVersion(version);
//    }

    private void updateVersion(Object entity) {

        String version;
        String lifecycleStatus;

        if (entity instanceof ProductSpecification ps) {
            version = ps.getVersion();
            lifecycleStatus = ps.getLifecycleStatus().name();

            if ("ACTIVE".equals(lifecycleStatus)) {
                version = (Integer.parseInt(version.substring(0, 1)) + 1) + ".0";
            }
            ps.setVersion(version);

        } else if (entity instanceof ProductOffering po) {
            version = po.getVersion();
            lifecycleStatus = po.getLifecycleStatus().name();

            if ("ACTIVE".equals(lifecycleStatus)) {
                version = (Integer.parseInt(version.substring(0, 1)) + 1) + ".0";
            }
            po.setVersion(version);
        }
    }


} 
