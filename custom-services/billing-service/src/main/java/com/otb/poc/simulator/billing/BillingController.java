package com.otb.poc.simulator.billing;

import com.otb.poc.simulator.common.model.ServiceOrder;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillingController {
  private final BillingSimulatorService simulatorService;
  private final BillingRecordRepository repository;

  public BillingController(BillingSimulatorService simulatorService, BillingRecordRepository repository) {
    this.simulatorService = simulatorService;
    this.repository = repository;
  }

  @PostMapping("/serviceOrdering/v1/serviceOrder")
  public ServiceOrder createServiceOrder(@RequestBody ServiceOrder serviceOrder) {
    return simulatorService.create(serviceOrder);
  }

  @GetMapping("/simulator/orders/{id}")
  public ResponseEntity<BillingRecord> getOrder(@PathVariable String id) {
    return repository.findById(id)
        .or(() -> repository.findByIdempotencyKey(id))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/simulator/failure-mode")
  public Map<String, String> getFailureMode() {
    return simulatorService.failureMode();
  }

  @PostMapping("/simulator/failure-mode")
  public Map<String, String> setFailureMode(@RequestBody Map<String, String> body) {
    simulatorService.setFailureMode(body.get("mode"));
    return simulatorService.failureMode();
  }
}
