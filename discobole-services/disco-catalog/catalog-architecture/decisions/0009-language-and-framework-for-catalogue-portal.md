# Language & framework for Catalogue Portal

## Context and Problem Statement

Decide the language & framework (Angular or React) for the Catalogue Portal.

## Considered Options

* Angular
* React

## Decision Outcome

* Choosing between Angular and React depends on your use cases and the resources available.
* Below are few points we considered to move ahead with react frame work for Catalogue UI Portal  
  * Flexibility: React is a more lightweight and flexible framework compared to Angular. It is easier to integrate with other libraries and frameworks, and it doesn't enforce a specific project structure or development approach.
  * Performance: React is known for its performance and fast rendering speed, especially when dealing with large and complex UIs. It uses a virtual DOM to optimize updates and minimize the number of actual DOM manipulations.
  * Component-based architecture: React's component-based architecture allows you to create reusable UI components that can be used across different parts of your application. This makes it easier to maintain and scale your application over time.
  * Large developer community: React has a large and active developer community, which means there are many resources and tools available to help you build and maintain your application.

## Pros and Cons of the Options

### Angular

#### Pros

* Complete framework: Angular is a complete framework that provides a wide range of features, such as routing, dependency injection, and forms handling. This can be beneficial for large-scale applications that require complex features and functionality.
* TypeScript Support: Angular is built using TypeScript, a superset of JavaScript that provides strong typing and improved tooling support. This can help catch errors at compile time and improve the maintainability of the codebase.
* Out-of-the-box support for Material Design: Angular has built-in support for Material Design, a popular design language that provides a consistent user experience across different devices and platforms.

#### Cons

* Steep learning curve: Angular has a steep learning curve, especially for developers who are not familiar with TypeScript or other concepts such as dependency injection.
* Opinionated framework: Angular has a set of strict conventions and guidelines that can be limiting for some developers. This can make it difficult to customize the framework to suit specific project requirements.
* Performance: Angular can be slower compared to other frameworks due to its large size and complex structure.

### React

#### Pros

* Lightweight framework: React is a lightweight framework that focuses on the view layer of the application. This can make it faster and easier to learn compared to Angular.
* Component-based architecture: React uses a component-based architecture, which enables modular and reusable code. This can help improve the maintainability of the codebase and make it easier to scale.
* Virtual DOM: React uses a virtual DOM, which allows for faster rendering and improved performance. This is especially beneficial for large-scale applications.

#### Cons

* Lack of built-in features: React is not a complete framework and lacks built-in features such as routing, dependency injection, and forms handling. This can require developers to use third-party libraries or build custom solutions.
* JSX Syntax: React uses JSX syntax, which can be confusing for some developers who are not familiar with it. This can make it harder to get started with the framework.
* Unopinionated framework: React is an unopinionated framework, which means that there are multiple ways to achieve the same result. This can make it harder to maintain consistency and standardization across different projects.


