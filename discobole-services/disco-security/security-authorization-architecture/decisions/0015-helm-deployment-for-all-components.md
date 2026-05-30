# HELM Deployment for ODA Components  

## Purpose  
HELM is used to deploy ODA Components.  

## Decision Date  
**16 Mar 2023**  

## References  
- **GitLab:** [HELM is used to deploy ODA Component (#28)](https://gitlab.tech.orange/disco/project/architecture-kanban/-/issues/28)  

## Discussion  
Implementation-level decision.  

## Considered Options  

The decision to use HELM to deploy ODA components was made for several reasons:  

- **Standardization:** HELM follows a templating approach that allows for the creation of reusable deployment manifests, ensuring a standardized and consistent deployment process across different ODA components and environments.  
- **Scalability:** HELM enables the deployment of multiple instances of ODA components, making it easier to scale up or down based on demand, especially in cloud environments.  
- **Version Control:** HELM supports version control for deployments, allowing easy rollbacks to previous versions if needed, ensuring a robust deployment process.  
- **Flexibility:** HELM can be customized to fit specific deployment requirements, making it suitable for different environments and scenarios.  

For **ODACAT**, HELM is already used for deployment, and as confirmed by **ELHOSEINY Ameen Ext O-EG/HRCS**, the OM deployment process for new services is also initiated using HELM.  

## Decision  
The decision to use HELM for deploying ODA components was made due to its advantages as mentioned above. Additionally, it was influenced by the fact that HELM was already being used to deploy the **ODACAT** component and that **OM** has also adopted HELM for deployment.  
