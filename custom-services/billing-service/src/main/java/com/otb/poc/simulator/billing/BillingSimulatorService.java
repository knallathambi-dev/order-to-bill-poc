package com.otb.poc.simulator.billing;

import com.otb.poc.simulator.common.ServiceOrderStateChangePublisher;
import com.otb.poc.simulator.common.ServiceOrderSupport;
import com.otb.poc.simulator.common.model.ServiceCharacteristic;
import com.otb.poc.simulator.common.model.ServiceOrder;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingSimulatorService {
  private final BillingRecordRepository repository;
  private final ServiceOrderStateChangePublisher publisher;
  private volatile String failureMode;

  public BillingSimulatorService(
      BillingRecordRepository repository,
      ServiceOrderStateChangePublisher publisher,
      @Value("${simulator.failure-mode:success}") String failureMode) {
    this.repository = repository;
    this.publisher = publisher;
    this.failureMode = normalizeMode(failureMode);
  }

  public ServiceOrder create(ServiceOrder request) {
    String key = ServiceOrderSupport.idempotencyKey(request, "billing");
    return repository.findByIdempotencyKey(key)
        .map(record -> responseFromRecord(request, record))
        .orElseGet(() -> createNew(request, key));
  }

  public void setFailureMode(String mode) {
    this.failureMode = normalizeMode(mode);
  }

  public Map<String, String> failureMode() {
    return Map.of("mode", failureMode);
  }

  private ServiceOrder createNew(ServiceOrder request, String key) {
    String state = stateFor(request);
    String reason = ServiceOrderSupport.FAILED.equals(state) ? "Billing simulator forced failure" : null;
    String serviceOrderId = ServiceOrderSupport.generatedServiceOrderId("billing", key);
    String itemId = ServiceOrderSupport.generatedItemId("billing", key);
    BillingRecord record = new BillingRecord(
        serviceOrderId,
        key,
        serviceOrderId,
        itemId,
        "acct-" + key,
        "inv-" + key,
        state,
        reason,
        Instant.now(),
        Instant.now());
    repository.save(record);
    ServiceOrder response = responseFromRecord(request, record);
    publisher.publish(response);
    return response;
  }

  private ServiceOrder responseFromRecord(ServiceOrder request, BillingRecord record) {
    return ServiceOrderSupport.withIdsAndState(
        request,
        record.serviceOrderId(),
        record.serviceOrderItemId(),
        record.state(),
        "Billing initiation",
        record.failureReason());
  }

  private String stateFor(ServiceOrder request) {
    String requestedMode = requestedSimulationMode(request);
    String mode = requestedMode == null ? failureMode : requestedMode;
    return switch (mode) {
      case "failed", "fail", "failure" -> ServiceOrderSupport.FAILED;
      case "held", "hold" -> ServiceOrderSupport.HELD;
      default -> ServiceOrderSupport.COMPLETED;
    };
  }

  private String requestedSimulationMode(ServiceOrder request) {
    return ServiceOrderSupport.firstItem(request).service() == null
        || ServiceOrderSupport.firstItem(request).service().serviceCharacteristic() == null
        ? null
        : ServiceOrderSupport.firstItem(request).service().serviceCharacteristic().stream()
            .filter(characteristic -> "simulationMode".equalsIgnoreCase(characteristic.name()))
            .map(ServiceCharacteristic::value)
            .map(String::valueOf)
            .map(this::normalizeMode)
            .findFirst()
            .orElse(null);
  }

  private String normalizeMode(String mode) {
    return mode == null || mode.isBlank() ? "success" : mode.toLowerCase(Locale.ROOT);
  }
}
