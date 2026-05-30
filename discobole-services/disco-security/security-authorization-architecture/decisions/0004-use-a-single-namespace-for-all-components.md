# Use a Single Namespace for All ODA Components  

## Purpose  
To establish a unified namespace for all ODA components, ensuring consistency and resource optimization across environments.  

## Decision Date  
**22nd February 2023**  

## References   
- **GitLab:** [Issue #24](https://gitlab.tech.orange/disco/project/architecture-kanban/-/issues/24) – Namespace standardization discussion  

## Objective  
Define the mapping of teams and ODA components onto the environment structure.  

## Considered Options  

### Separate Namespace for Each ODA Component  
**Advantages:**  
- Provides component isolation  
- Aligns closely with TMF (TeleManagement Forum) recommendations  

**Challenges:**  
- Leads to a high number of namespaces  
- Requires extensive network policy tuning  

### Single Namespace for All ODA Components (Chosen Option)  
**Advantages:**  
- Promotes uniformity in practices  
- Prevents resource conflicts and overlapping issues  

**Challenges:**  
- Might require adjustments in the future as complexity grows  

## Decision  
We opted for the simpler solution initially—**using a single namespace for all ODA components**. This approach ensures easier management and can be modified later if necessary.  


