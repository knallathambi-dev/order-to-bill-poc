package com.otb.poc.simulator.qualification;

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
public class QualificationSimulatorService {
  private final QualificationRecordRepository repository;
  private final ServiceOrderStateChangePublisher publisher;
  private volatile String failureMode;

  public QualificationSimulatorService(
      QualificationRecordRepository repository,
      ServiceOrderStateChangePublisher publisher,
      @Value("${simulator.failure-mode:success}") String failureMode) {
    this.repository = repository;
    this.publisher = publisher;
    this.failureMode = normalizeMode(failureMode);
  }

  public ServiceOrder create(ServiceOrder request) {
    String key = ServiceOrderSupport.idempotencyKey(request, "qualification");
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
    String reason = ServiceOrderSupport.FAILED.equals(state) ? "Qualification simulator forced failure" : null;
    String serviceOrderId = ServiceOrderSupport.generatedServiceOrderId("qualification", key);
    String itemId = ServiceOrderSupport.generatedItemId("qualification", key);
    QualificationRecord record = new QualificationRecord(
        serviceOrderId,
        key,
        serviceOrderId,
        itemId,
        ServiceOrderSupport.firstServiceSpecificationId(request),
        state,
        ServiceOrderSupport.COMPLETED.equals(state) ? "serviceable" : "not-serviceable",
        reason,
        Instant.now(),
        Instant.now());
    repository.save(record);
    ServiceOrder response = responseFromRecord(request, record);
    publisher.publish(response);
    return response;
  }

  private ServiceOrder responseFromRecord(ServiceOrder request, QualificationRecord record) {
    return ServiceOrderSupport.withIdsAndState(
        request,
        record.serviceOrderId(),
        record.serviceOrderItemId(),
        record.state(),
        "Broadband qualification",
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
