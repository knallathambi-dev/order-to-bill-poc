package com.orange.discobole.orderorchestration.orchestrationdelivery.management.mapper;

import com.orange.discobole.orderorchestration.orchestrationdelivery.enums.ShippingOrderItemStatus;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.cood.DeliveryStatusMapping;
import com.orange.discobole.orderorchestration.orchestrationdelivery.generated.som.ServiceOrderItem;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface DeliveryStatusMapper {

    default DeliveryStatusMapping mapServiceOrderItemStateType(ServiceOrderItem.State status) {
        return switch (status) {
            case COMPLETED -> DeliveryStatusMapping.builder()
                    .deliveryStatus(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED)
                    .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.COMPLETED)
                    .build();
            case FAILED -> DeliveryStatusMapping.builder()
                    .deliveryStatus(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED)
                    .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.FAILED)
                    .build();
            case HELD -> DeliveryStatusMapping.builder()
                    .deliveryStatus(DeliveryStatusMapping.DeliveryStatusEnum.HELD)
                    .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.HELD)
                    .build();
            case IN_PROGRESS -> null;
            case CANCELLED -> null;
            case ASSESSING_CANCELLATION -> null;
            case PENDING_CANCELLATION -> null;
            case PARTIAL -> null;
            case ACKNOWLEDGED -> null;
            case REJECTED -> null;
            case PENDING -> null;
        };
    }

    default DeliveryStatusMapping mapShippingItemStatus(String status) {
        return switch (ShippingOrderItemStatus.fromValue(status)) {
            case COMPLETED -> DeliveryStatusMapping.builder()
                    .deliveryStatus(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED)
                    .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.COMPLETED)
                    .build();
            case FAILED -> DeliveryStatusMapping.builder()
                    .deliveryStatus(DeliveryStatusMapping.DeliveryStatusEnum.EXECUTED)
                    .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.FAILED)
                    .build();
            case HELD -> DeliveryStatusMapping.builder()
                    .deliveryStatus(DeliveryStatusMapping.DeliveryStatusEnum.HELD)
                    .nodeStatus(DeliveryStatusMapping.NodeStatusEnum.HELD)
                    .build();
        };
    }
}
