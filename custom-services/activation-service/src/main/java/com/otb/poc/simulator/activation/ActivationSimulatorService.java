package com.otb.poc.simulator.activation;

import com.otb.poc.simulator.common.ServiceOrderStateChangePublisher;
import com.otb.poc.simulator.common.ServiceOrderSupport;
import com.otb.poc.simulator.common.model.ServiceCharacteristic;
import com.otb.poc.simulator.common.model.ServiceOrder;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ActivationSimulatorService {
  private static final String BILLING_SPEC_ID = "billing-initiation-service";
  private static final String STATIC_IP_SPEC_ID = "static-ip-service";

  private final ActivationRecordRepository repository;
  private final ServiceOrderStateChangePublisher publisher;
  private final RestClient restClient;
  private final String billingServiceUrl;
  private volatile String failureMode;

  public ActivationSimulatorService(
      ActivationRecordRepository repository,
      ServiceOrderStateChangePublisher publisher,
      RestClient restClient,
      @Value("${simulator.billing-service-url}") String billingServiceUrl,
      @Value("${simulator.failure-mode:success}") String failureMode) {
    this.repository = repository;
    this.publisher = publisher;
    this.restClient = restClient;
    this.billingServiceUrl = billingServiceUrl;
    this.failureMode = normalizeMode(failureMode);
  }

  public ServiceOrder create(ServiceOrder request) {
    String specId = ServiceOrderSupport.firstServiceSpecificationId(request);
    if (BILLING_SPEC_ID.equalsIgnoreCase(specId)) {
      return restClient.post()
          .uri(billingServiceUrl + "/serviceOrdering/v1/serviceOrder")
          .body(request)
          .retrieve()
          .body(ServiceOrder.class);
    }

    String key = ServiceOrderSupport.idempotencyKey(request, "activation");
    return repository.findByIdempotencyKey(key)
        .map(record -> responseFromRecord(request, record))
        .orElseGet(() -> createNew(request, key, specId));
  }

  public void setFailureMode(String mode) {
    this.failureMode = normalizeMode(mode);
  }

  public Map<String, String> failureMode() {
    return Map.of("mode", failureMode);
  }

  private ServiceOrder createNew(ServiceOrder request, String key, String specId) {
    String state = stateFor(request);
    String reason = ServiceOrderSupport.FAILED.equals(state) ? "Activation simulator forced failure" : null;
    String serviceOrderId = ServiceOrderSupport.generatedServiceOrderId("activation", key);
    String itemId = ServiceOrderSupport.generatedItemId("activation", key);
    Instant now = Instant.now();
    ActivationRecord record = new ActivationRecord(
        serviceOrderId,
        key,
        serviceOrderId,
        itemId,
        specId,
        activationType(specId),
        state,
        reason,
        now,
        now);
    repository.save(record);
    ServiceOrder response = responseFromRecord(request, record);
    publisher.publish(response);
    return response;
  }

  private ServiceOrder responseFromRecord(ServiceOrder request, ActivationRecord record) {
    return ServiceOrderSupport.withIdsAndState(
        request,
        record.serviceOrderId(),
        record.serviceOrderItemId(),
        record.state(),
        record.activationType(),
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

  private String activationType(String specId) {
    if (STATIC_IP_SPEC_ID.equalsIgnoreCase(specId)) {
      return "Static IP activation";
    }
    return "Fiber broadband activation";
  }

  private String normalizeMode(String mode) {
    return mode == null || mode.isBlank() ? "success" : mode.toLowerCase(Locale.ROOT);
  }
}
