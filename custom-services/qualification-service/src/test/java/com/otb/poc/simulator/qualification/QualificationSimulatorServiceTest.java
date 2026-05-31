package com.otb.poc.simulator.qualification;

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

class QualificationSimulatorServiceTest {
  @Test
  void createsServiceableQualificationAndPublishesCompletion() {
    QualificationRecordRepository repository = mock(QualificationRecordRepository.class);
    ServiceOrderStateChangePublisher publisher = mock(ServiceOrderStateChangePublisher.class);
    when(repository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    QualificationSimulatorService service = new QualificationSimulatorService(repository, publisher, "success");

    ServiceOrder response = service.create(order());

    assertThat(response.state()).isEqualTo("Completed");
    assertThat(response.serviceOrderItem().get(0).service().name()).isEqualTo("Broadband qualification");
    verify(publisher).publish(response);
  }

  private ServiceOrder order() {
    Service service = new Service(null, null, null, "CFS", List.of(), new ServiceSpecification("fiber-broadband-service", null, null));
    ServiceOrderItem item = new ServiceOrderItem("item-1", 1, "add", List.of(), service, null);
    return new ServiceOrder("request-1", null, null, null, null, null, null, null, null, List.of(item));
  }
}
