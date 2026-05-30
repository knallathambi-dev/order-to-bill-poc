package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records;

public record DeliveryOrderItemRecord(
        String factoryOrderId,
        String factoryOrderItemId,
        String realizingServiceId,
        String realizingServiceHref
) {
}
