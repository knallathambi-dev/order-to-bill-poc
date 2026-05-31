package com.otb.poc.simulator.activation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.otb.poc.simulator.common.ServiceOrderStateChangePublisher;
import com.otb.poc.simulator.common.model.Service;
import com.otb.poc.simulator.common.model.ServiceOrder;
import com.otb.poc.simulator.common.model.ServiceOrderItem;
import com.otb.poc.simulator.common.model.ServiceSpecification;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class ActivationSimulatorServiceTest {
  @Test
  void completesBroadbandActivationAndPublishesEvent() {
    ActivationRecordRepository repository = mock(ActivationRecordRepository.class);
    ServiceOrderStateChangePublisher publisher = mock(ServiceOrderStateChangePublisher.class);
    when(repository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    ActivationSimulatorService service = new ActivationSimulatorService(
        repository, publisher, mock(RestClient.class), "http://billing", "success");

    ServiceOrder response = service.create(orderFor("fiber-broadband-service"));

    assertThat(response.state()).isEqualTo("Completed");
    assertThat(response.serviceOrderItem().get(0).state()).isEqualTo("Completed");
    verify(publisher).publish(response);
  }

  @Test
  void forcedFailureReturnsFailedState() {
    ActivationRecordRepository repository = mock(ActivationRecordRepository.class);
    when(repository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    ActivationSimulatorService service = new ActivationSimulatorService(
        repository, mock(ServiceOrderStateChangePublisher.class), mock(RestClient.class), "http://billing", "failed");

    assertThat(service.create(orderFor("static-ip-service")).state()).isEqualTo("Failed");
  }

  private ServiceOrder orderFor(String specId) {
    Service service = new Service(null, null, null, "CFS", List.of(), new ServiceSpecification(specId, null, null));
    ServiceOrderItem item = new ServiceOrderItem("item-1", 1, "add", List.of(), service, null);
    return new ServiceOrder("request-1", null, null, null, null, null, null, null, null, List.of(item));
  }
}
