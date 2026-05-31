package com.otb.poc.simulator.activation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication(scanBasePackages = "com.otb.poc.simulator")
public class ActivationServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(ActivationServiceApplication.class, args);
  }

  @Bean
  RestClient restClient(RestClient.Builder builder) {
    return builder.build();
  }
}
