package com.otb.poc.simulator.qualification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.otb.poc.simulator")
public class QualificationServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(QualificationServiceApplication.class, args);
  }
}
