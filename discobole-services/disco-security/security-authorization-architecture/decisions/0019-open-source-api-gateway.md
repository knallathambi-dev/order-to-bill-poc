# Open-Source API Gateway  

## Purpose  
Which open-source API gateway to be used.  

## Decision Date  
**04 May 2023**  

## References  
- **GitLab:** [Which Open-Source API Gateway to be Used (#42)](https://gitlab.tech.orange/disco/project/architecture-kanban/-/issues/42)  

## Objective  
The objective is to identify the most suitable open-source API gateway solution that can handle high traffic and provide robust security features for our microservices architecture.  

## Considered Options  

### Spring Cloud Gateway  

**Features and Functionality:**  
- Allows users to define routing rules based on request attributes such as path, headers, and query parameters.  
- Provides built-in filters for authentication, rate limiting, and caching.  
- Supports load balancing algorithms such as Round Robin, Weighted Response Time, and Random.  
- Provides circuit-breaking capabilities to prevent cascading failures.  
- Integrates tightly with other Spring projects such as Spring Boot and Spring Cloud Config.  
- Provides out-of-the-box support for various metrics and monitoring tools such as Prometheus, Grafana, and Micrometer.  

**Pros:**  
- Flexible and configurable framework for building API gateways.  
- Integration with the Spring ecosystem for easy deployment and management.  
- High performance with asynchronous I/O capabilities.  
- Built-in support for load balancing across multiple instances of backend services.  
- Horizontally scalable to handle increasing traffic.  
- Supports reactive programming for non-blocking I/O and better resource utilization.  
- Built-in circuit-breaking capabilities to prevent cascading failures.  
- Provides out-of-the-box support for metrics and monitoring tools.  

**Cons:**  
- Learning curve for setup and configuration.  
- Limited documentation compared to other tools.  
- Reliance on the Netty framework, which may require additional learning.  
- May lack certain features or functionality compared to other API gateways.  

## Decision  
We have decided to use **Spring Cloud Gateway** for our gateway implementation in the June MVP scope. Spring Cloud Gateway offers advantages such as easy integration with Spring Cloud, the ability to handle multiple protocols, flexible routing options, and efficient handling of HTTP requests. However, we are open to exploring other options like **APIGEE** or any other concrete gateway solution as per project requirements in the future.  
