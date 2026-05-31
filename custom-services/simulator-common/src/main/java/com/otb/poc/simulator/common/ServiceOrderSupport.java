package com.otb.poc.simulator.common;

import com.otb.poc.simulator.common.model.ErrorMessage;
import com.otb.poc.simulator.common.model.Service;
import com.otb.poc.simulator.common.model.ServiceOrder;
import com.otb.poc.simulator.common.model.ServiceOrderItem;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ServiceOrderSupport {
  public static final String COMPLETED = "Completed";
  public static final String FAILED = "Failed";
  public static final String HELD = "Held";

  private ServiceOrderSupport() {
  }

  public static String firstServiceSpecificationId(ServiceOrder serviceOrder) {
    return firstItem(serviceOrder).service() == null
        || firstItem(serviceOrder).service().serviceSpecification() == null
        ? "unknown-service"
        : firstItem(serviceOrder).service().serviceSpecification().id();
  }

  public static ServiceOrderItem firstItem(ServiceOrder serviceOrder) {
    if (serviceOrder.serviceOrderItem() == null || serviceOrder.serviceOrderItem().isEmpty()) {
      throw new IllegalArgumentException("serviceOrderItem must contain at least one item");
    }
    return serviceOrder.serviceOrderItem().get(0);
  }

  public static ServiceOrder withIdsAndState(
      ServiceOrder request,
      String serviceOrderId,
      String itemId,
      String state,
      String serviceName,
      String failureReason) {
    ServiceOrderItem requestItem = firstItem(request);
    Service requestService = requestItem.service() == null ? new Service(null, null, null, null, null, null) : requestItem.service();
    String serviceId = requestService.id() == null ? "svc-" + itemId : requestService.id();
    Service service = new Service(
        serviceId,
        "/serviceInventory/v1/service/" + serviceId,
        serviceName,
        requestService.serviceType() == null ? "CFS" : requestService.serviceType(),
        requestService.serviceCharacteristic(),
        requestService.serviceSpecification());

    List<ErrorMessage> errors = failureReason == null || failureReason.isBlank()
        ? List.of()
        : List.of(new ErrorMessage("OTB_SIMULATOR_FAILURE", failureReason, failureReason));

    ServiceOrderItem item = new ServiceOrderItem(
        itemId,
        requestItem.quantity(),
        requestItem.action(),
        errors,
        service,
        state);

    Instant now = Instant.now();
    return new ServiceOrder(
        serviceOrderId,
        "/serviceOrdering/v1/serviceOrder/" + serviceOrderId,
        request.category(),
        request.description(),
        request.requestedCompletionDate(),
        request.requestedStartDate(),
        request.orderDate() == null ? now : request.orderDate(),
        COMPLETED.equals(state) ? now : null,
        state,
        List.of(item));
  }

  public static String idempotencyKey(ServiceOrder request, String serviceName) {
    ServiceOrderItem item = firstItem(request);
    List<String> parts = new ArrayList<>();
    parts.add(serviceName);
    parts.add(nullToDash(request.id()));
    parts.add(nullToDash(request.externalId()));
    parts.add(nullToDash(item.id()));
    parts.add(nullToDash(item.action()));
    parts.add(firstServiceSpecificationId(request));
    return Integer.toHexString(String.join("|", parts).hashCode());
  }

  public static String generatedServiceOrderId(String serviceName, String idempotencyKey) {
    return serviceName + "-so-" + idempotencyKey;
  }

  public static String generatedItemId(String serviceName, String idempotencyKey) {
    return serviceName + "-soi-" + idempotencyKey;
  }

  public static String correlationId(ServiceOrder request) {
    return Objects.requireNonNullElseGet(request.id(), () -> UUID.randomUUID().toString());
  }

  private static String nullToDash(String value) {
    return value == null || value.isBlank() ? "-" : value;
  }
}
