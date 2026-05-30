# Choose Logging Backend Solution  

## Purpose  
Choose a logging backend solution.  

## Decision Date  
**31st January 2023**  

## References   
- **GitLab:** [Issue #13](https://gitlab.tech.orange/disco/project/architecture-kanban/-/issues/13)  
 

## Objective  
Decide the logging backend solution to be used in the project.  

## Considered Options  

### ElasticSearch  

**Cons:**  
- Huge resource consumption, especially in memory.  
- No simplified version of ElasticSearch (neither in deployment model nor in features).  
- With increased log sizes, ElasticSearch becomes a bottleneck in operation and requires increased resources over time.  

**Pros:**  
- Full-text search feature capabilities.  

### Loki  

**Pros:**  
- Low resource consumption (memory and storage).  
- Has a simplified deployment model (all-in-one).  

**Cons:**  
- Needs to be tested against large-scale projects with huge log data.  
- No full-text search capabilities.  
- Search is limited to labels defined in the index.  

## Root Cause Analysis  

### Indexing  
- ElasticSearch indexes all data in every field, and each indexed field has a dedicated, optimized data structure.  
- Loki only indexes the metadata (labels) of logs, making it more cost-effective and performant but losing the rich text search capabilities that ElasticSearch provides.  

### Storage  
- Loki stores all data in a single object storage backend, reducing costs. Logs are compressed and stored as chunks in object stores like S3 and GCS.  
- ElasticSearch indexes the full contents of stored documents, requiring more storage space but making documents fully searchable.  

### Search Capabilities  
- Labels serve as the index for Loki’s log data, determining which compressed log content to retrieve.  
- High cardinality (a large number of unique label combinations) can cause performance issues in Loki, leading to high costs and slow queries.  
- Queries in Loki consist of a log stream selector and a log pipeline for filtering log data.  

### Cluster and High-Load Capabilities  
- Loki uses consistent hashing with a configurable replication factor to distribute log streams across instances.  
- Benchmarks indicate limitations in the maximum number of streams, affected by container count and dynamic label values.  

> **Issue:**  
> When using only two flog containers for log generation, increasing the container count led to max stream errors. The number of indexed attributes was limited, and adding more labels resulted in errors.  

## Decision  

Choosing between Loki and ElasticSearch depends on use cases and available resources.  
- **High cardinality affects Loki's performance.** Labels should be static or dynamic with a low-value range.  
- **Dynamic labels with a high range (e.g., IP addresses or trace IDs) will not work well with Loki.**  
- **Label selection must be decided before data ingestion**, as it impacts Loki’s performance.  
- **For high-load and large-scale solutions, a proof of concept is required to validate Loki's feasibility.**  

### Conclusion  
ElasticSearch is the preferred option over Loki for backend logging due to its ability to handle high cardinality, whereas Loki's performance is highly dependent on proper label selection.  
