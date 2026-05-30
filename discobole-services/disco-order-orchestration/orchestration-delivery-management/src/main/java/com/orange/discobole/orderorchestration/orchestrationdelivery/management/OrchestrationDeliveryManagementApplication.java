// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management;


import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableMongock
@ComponentScan(basePackages = {"com.orange.discobole.orderorchestration", "com.orange.discobole.orderorchestration.orchestrationdelivery.management"})
public class OrchestrationDeliveryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestrationDeliveryManagementApplication.class, args);
    }

}
