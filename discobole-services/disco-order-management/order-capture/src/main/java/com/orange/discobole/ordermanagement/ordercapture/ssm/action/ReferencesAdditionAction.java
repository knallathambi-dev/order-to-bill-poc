// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.ssm.action;

import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTOList;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.ProductOrderService;
import com.orange.discobole.ordermanagement.ordercapture.util.StateMachineUtil;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.Product;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrderItem;
import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductRef;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import com.orange.discobole.productinventory.dto.v1.BillingAccountRef;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.orange.discobole.ordermanagement.ordercapture.constant.OrderCaptureConstants.BILLING_ACCOUNT_TYPE;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.BILLING_ACCOUNT_URI;
import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.PRODUCT_INVENTORY_URI;
import static java.lang.Boolean.FALSE;

@Component("addReferencesAction")
@Slf4j
public class ReferencesAdditionAction implements StateMachineStateAction<String, String> {
    private final ProductOrderService productOrderService;
    private final ProductInventoryService productInventoryService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ReferencesAdditionAction(ProductOrderService productOrderService, ProductInventoryService productInventoryService) {
        this.productOrderService = productOrderService;
        this.productInventoryService = productInventoryService;
    }

    @Override
    public Mono<Void> apply(StateContext<String, String> context) {
        if (Boolean.TRUE.equals(StateMachineUtil.isReExecutionAction(context))) {
            return Mono.empty();
        }
        try {
            log.info("Inside the addition of references action");
            ProductOrder productOrder = StateMachineUtil.getObjectValue(context, OrderCaptureConstants.CREATED_PRODUCT_ORDER, ProductOrder.class);

            Map<String, List<String>> unpaidOrderItemIds = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.ORDER_ITEM_PAYMENT_REF_MAP);
            Map<String, String> orderItemIdsRequiringBillingAccountRef = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.ORDER_ITEM_BILLING_ACCOUNT_REF_MAP);
            Map<String, String> orderItemIdsRequiringAppointmentRef = StateMachineUtil.getMapValue(context.getExtendedState().getVariables(), OrderCaptureConstants.ORDER_ITEM_APPOINTMENT_REF_MAP);
            boolean isAppointmentRequired = StateMachineUtil.getBooleanValue(context, OrderCaptureConstants.IS_APPOINTMENT_REQUIRED, FALSE);

            if (!CollectionUtils.isEmpty(unpaidOrderItemIds)) {
                productOrderService.addPaymentRef(productOrder, unpaidOrderItemIds);
            }

            if (!CollectionUtils.isEmpty(orderItemIdsRequiringBillingAccountRef)) {
                productOrderService.addBillingAccountRef(productOrder, orderItemIdsRequiringBillingAccountRef);

                Map<String, String> productIdBillingAccountRefMap = getProductBillingAccountRefMap(productOrder, orderItemIdsRequiringBillingAccountRef);

                PatchDTOList productPatch = createBillingAccountPatchRequest(productIdBillingAccountRefMap);
                productInventoryService.updateProducts(productPatch.toJsonString());
            }
            if (!CollectionUtils.isEmpty(orderItemIdsRequiringAppointmentRef) && isAppointmentRequired) {
                productOrderService.addAppointmentRef(productOrder, orderItemIdsRequiringAppointmentRef);
            }

            updateContextVariables(context, productOrder, true);
            return Mono.empty();
        } catch (DiscoException discoException) {
            log.error("Unable to add references [{}]:", discoException.getMessage(), discoException);
            StateMachineUtil.setDescriptionContext(context, discoException.getReason());
            updateContextVariables(context, null, false);
            return Mono.empty();
        } catch (Exception e) {
            log.error("Unable to add references [{}]:", e.getMessage(), e);
            updateContextVariables(context, null, false);
            return Mono.empty();
        }
    }

    private Map<String, String> getProductBillingAccountRefMap(
            ProductOrder productOrder,
            Map<String, String> orderItemIdsRequiringBillingAccountRef) {

        Map<String, String> productIdToOrderItemIdMap = productOrder.getProductOrderItem().stream()
                .collect(Collectors.toMap(
                        item -> {
                            if (item.getProduct() instanceof Product product) {
                                return product.getId();
                            } else {
                                return ((ProductRef) item.getProduct()).getId();
                            }
                        },
                        ProductOrderItem::getId
                ));

        return productIdToOrderItemIdMap.entrySet().stream()
                .filter(entry -> orderItemIdsRequiringBillingAccountRef.containsKey(entry.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> orderItemIdsRequiringBillingAccountRef.get(entry.getValue())
                ));
    }

    private void updateContextVariables(StateContext<String, String> context, ProductOrder productOrder, boolean areReferencesAdded) {
        context.getExtendedState().getVariables().put(OrderCaptureConstants.ARE_REFERENCES_ADDED, areReferencesAdded);
        if (areReferencesAdded) {
            context.getExtendedState().getVariables().put(OrderCaptureConstants.CREATED_PRODUCT_ORDER, productOrder);
        }
    }

    private PatchDTOList createBillingAccountPatchRequest(Map<String, String> productIdBillingAccountRefMap) {
        List<PatchDTO> patchDTOList = productIdBillingAccountRefMap.entrySet().stream()
                .map(entry -> buildProductPatchRequest(entry.getKey(), entry.getValue()))
                .flatMap(List::stream)
                .toList();

        return PatchDTOList.builder()
                .list(patchDTOList)
                .build();
    }

    private List<PatchDTO> buildProductPatchRequest(String productId, String billingAccountRefId) {
        String productUri = PRODUCT_INVENTORY_URI + productId;
        List<PatchDTO> patches = new ArrayList<>();

        BillingAccountRef billingAccount = BillingAccountRef.builder()
                .id(billingAccountRefId)
                .atReferredType(BILLING_ACCOUNT_TYPE)
                .build();

        PatchDTO patchBillingAccountRefDTO = createPatchRequest(productUri + BILLING_ACCOUNT_URI, billingAccount);
        patches.add(patchBillingAccountRefDTO);

        return patches;
    }

    private PatchDTO createPatchRequest(String path, BillingAccountRef billingAccount) {
        return PatchDTO.builder()
                .op(PatchOperationType.ADD)
                .path(path)
                .value(billingAccount)
                .build();
    }
}