// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.dto.v1.ExternalIdentifier;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.ProductRelationshipType;
import com.orange.discobole.productinventory.mapper.ProductMapper;
import com.orange.discobole.productinventory.model.JobReportProductRefEntity;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.ProductRefEntity;
import com.orange.discobole.productinventory.model.ProductRelationshipEntity;
import com.orange.discobole.productinventory.model.job.JobReportEntity;
import com.orange.discobole.productinventory.repository.ProductRepository;
import com.orange.discobole.productinventory.service.ProductImportService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the ProductImportService that processes and imports products
 * into the database while validating hierarchies and relationships.
 * @author Khames Guen
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@SuppressFBWarnings("EI_EXPOSE_REP2")})
public class ProductImportServiceImpl implements ProductImportService {
    public static final String HIERARCHY_DOES_NOT_HAVE_A_CLEAR_ROOT_PRODUCT = "Hierarchy does not have a clear root Product";
    public static final String HIERARCHY_DOES_NOT_HAVE_A_ROOT_PRODUCT = "Hierarchy does not have a root Product";
    public static final String PRODUCT_ALREADY_EXISTS_IN_DATABASE = "Product already exists in database";
    public static final String HIERARCHY_HAS_PRODUCT_ALREADY_EXISTS_IN_DATABASE = "Hierarchy has a product that already exists in database";
    public static final String HAS_S_RELATIONSHIP_WITH_NON_EXISTING_PRODUCT_WITH_ID_S = "Has %s relationship with non-existing product with id %s";
    public static final String MISSING_PRODUCT_IN_HIERARCHY = "Missing Product in hierarchy";
    public static final String INVALID_PRODUCT_IN_HIERARCHY = "Invalid Product in hierarchy";
    public static final String INVALID_PRODUCT_MISSING_PRODUCT_OFFERING_AND_PRODUCT_SPECIFICATION = "Invalid Product missing productOffering and ProductSpecification";

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;

    @Value("${config.importing.externalIdentifier.owner}")
    private String importOwner;

    @Value("${config.importing.externalIdentifier.type}")
    private String importType;

    /**
     * Checks if the given product has any relationship pointing to a non-existing product.
     *
     * @param productEntity the product to check
     * @param map           a map of all products keyed by product id
     * @return an Optional containing the first ProductRelationshipEntity with a missing target, or empty if all are valid
     */
    private static Optional<ProductRelationshipEntity> hasRelationshipWithNonExistingProduct(ProductEntity productEntity, Map<String, ProductEntity> map) {
        return productEntity.getProductRelationship().stream()
                .filter(productRelationshipEntity ->
                        productRelationshipEntity.getProduct() != null &&
                                productRelationshipEntity.getProduct().getId() != null &&
                                map.get(productRelationshipEntity.getProduct().getId().toString()) == null)
                .findAny();
    }

    /**
     * Generates a map of incoming relationship counts for all products in the hierarchy.
     *
     * @param hierarchy the set of product IDs in the current hierarchy
     * @param map       the overall product map
     * @return a map where each key is a product id and the value is the number of incoming relationships
     */
    private static Map<String, Integer> generateIncommingRelationshipMap(Set<String> hierarchy, Map<String, ProductEntity> map) {
        Map<String, Integer> incommingRelationshipMap = new HashMap<>();
        // Iterate through each product in the hierarchy to count incoming relationships
        for (String productId : hierarchy) {
            ProductEntity entity = map.get(productId);
            for (ProductRelationshipEntity productRelationshipEntity : entity.getProductRelationship()) {
                // Increment count for the target product of the relationship
                incommingRelationshipMap.compute(productRelationshipEntity.getProduct().getId().toString(), (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
        return incommingRelationshipMap;
    }

    /**
     * Adds a ROOTPRODUCT relationship to all products in the hierarchy, except the root product.
     *
     * @param map       the overall product map
     * @param rootId    the product id identified as the root
     * @param hierarchy the set of product ids in the current hierarchy
     */
    private static void addRootProductRelationship(Map<String, ProductEntity> map, String rootId, Set<String> hierarchy) {
        ProductEntity root = map.get(rootId);
        // Build the new ROOTPRODUCT relationship
        ProductRelationshipEntity rootRelationship = ProductRelationshipEntity.builder()
                .relationshipType(ProductRelationshipType.ROOTPRODUCT.getValue())
                .product(ProductRefEntity.builder()
                        .id(new ObjectId(root.getId()))
                        .atType(root.getAtType())
                        .name(root.getName())
                        .build())
                .build();
        // Add the newly created ROOTPRODUCT relationship to all non-root products
        for (String productId : hierarchy) {
            if (!productId.equals(root.getId())) {
                ProductEntity productEntity = map.get(productId);
                productEntity.getProductRelationship().add(rootRelationship);
            }
        }
        log.info("Added ROOTPRODUCT relationship for hierarchy with root id: {}", rootId);
    }


    public static <K, V> Map<V, K> reverseMap(Map<K, V> originalMap) {
        Map<V, K> reversedMap = new HashMap<>();
        for (Map.Entry<K, V> entry : originalMap.entrySet()) {
            reversedMap.put(entry.getValue(), entry.getKey());
        }
        return reversedMap;
    }

    /**
     * Checks whether the hierarchy should be removed due to invalid relationships or product offerings.
     *
     * @param hierarchy        The current hierarchy being processed.
     * @param map              A map containing product entities.
     * @param jobReportEntity  The job report entity for tracking validation failures.
     * @return                 {@code true} if the hierarchy should be removed; {@code false} otherwise.
     */
    private boolean shouldRemoveDueToInvalidRelationships(Set<String> hierarchy, Map<String, ProductEntity> map, JobReportEntity jobReportEntity) {
        if (!isHierarchyRelationshipsAndOfferValid(map, hierarchy, jobReportEntity)) {
            log.warn("Hierarchy {} failed relationship/offer validation", hierarchy);
            return true;
        }
        return false;
    }

    /**
     * Determines if the hierarchy should be removed due to missing or multiple root products.
     *
     * @param hierarchy        The current hierarchy being processed.
     * @param map              A map containing product entities.
     * @param jobReportEntity  The job report entity for tracking validation failures.
     * @return                 {@code true} if the hierarchy should be removed; {@code false} otherwise.
     */
    private boolean shouldRemoveDueToInvalidRoot(Set<String> hierarchy, Map<String, ProductEntity> map, JobReportEntity jobReportEntity) {
        if (hierarchy.size() <= 1) {
            return false;
        }

        Map<String, Integer> incomingRelationshipMap = generateIncommingRelationshipMap(hierarchy, map);
        List<String> potentialRoots = getPotentialRoots(hierarchy, incomingRelationshipMap);

        if (potentialRoots.size() != 1) {
            log.warn("Hierarchy {} {}",
                    hierarchy,
                    potentialRoots.isEmpty() ? "does not have a root product" : "has multiple potential roots: " + potentialRoots);

            setUpFailedProducts(hierarchy, map, jobReportEntity,
                    potentialRoots.isEmpty() ? HIERARCHY_DOES_NOT_HAVE_A_ROOT_PRODUCT : HIERARCHY_DOES_NOT_HAVE_A_CLEAR_ROOT_PRODUCT);

            return true;
        }

        log.debug("Identified root product {} for hierarchy {}", potentialRoots.get(0), hierarchy);
        addRootProductRelationship(map, potentialRoots.get(0), hierarchy);
        return false;
    }

    /**
     * Finds the first product id in the hierarchy that already exists in the database.
     *
     * @param hierarchy a set of product ids representing a hierarchy
     * @return an Optional containing the first existing product id if found, otherwise empty
     */
    private Optional<String> findFirstExistingProductInDatabase(Set<String> hierarchy) {
        return hierarchy.stream()
                .filter(this::existsByIdOrByExternalIdentifierId)
                .findFirst();
    }

    public boolean existsByIdOrByExternalIdentifierId(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("_id").is(id),
                Criteria.where("externalIdentifier._id").is(id)
        ));

        return mongoTemplate.exists(query, ProductEntity.class);
    }

    /**
     * Processes a list of products, validates their hierarchies and relationships,
     * and saves the valid hierarchies to the database.
     *
     * @param products      the list of products to import
     * @param jobReportEntity a job report entity to log success and failure details
     */
    @Override
    public void processProducts(List<Product> products, JobReportEntity jobReportEntity) {
        log.info("Starting product import process for {} products", products.size());
        // Generate a map of product id to ProductEntity
        Map<String, ProductEntity> map = generateIdMapFromProducts(products);
        log.debug("Generated ID map with {} products", map.size());

        // Find connected components (each representing a product hierarchy)
        List<Set<String>> hierarchies = findConnectedComponents(map);
        log.info("Found {} hierarchies in the import data", hierarchies.size());

        Iterator<Set<String>> hierarchiesIterator = hierarchies.iterator();
        while (hierarchiesIterator.hasNext()) {
            Set<String> hierarchy = hierarchiesIterator.next();
            log.debug("Processing hierarchy with {} products: {}", hierarchy.size(), hierarchy);
            // Validate the relationships and product offerings for the hierarchy
            if (shouldRemoveDueToInvalidRelationships(hierarchy, map, jobReportEntity)
                    || shouldRemoveDueToInvalidRoot(hierarchy, map, jobReportEntity)
                    || shouldRemoveDueToExistingProduct(hierarchy, map, jobReportEntity)
            ) {
                hierarchiesIterator.remove();
            }
        }

        // Re-iterate through valid hierarchies and persist them
        hierarchiesIterator = hierarchies.iterator();
        while (hierarchiesIterator.hasNext()) {
            // Convert the set of product IDs into a list of ProductEntities
            List<ProductEntity> hierarchyEntities = hierarchiesIterator.next().stream().map(map::get)
                    .map(productEntity -> {
                        // Set external identifier for the product
                        productEntity.setExternalIdentifier(List.of(ExternalIdentifier.builder()
                                .externalIdentifierType(importType)
                                .owner(importOwner)
                                .id(productEntity.getId())
                                .build()));
                        return productEntity;
                    }).toList();

            // Update product IDs and their relationships to use new generated IDs
            Map<ObjectId, String> idMap = handleIds(hierarchyEntities);
            log.info("Saving hierarchy with {} products", hierarchyEntities.size());
            productRepository.saveAll(hierarchyEntities);
            setUpSucceededProducts(hierarchyEntities, jobReportEntity, idMap);

        }
        log.info("Product import process completed");
    }

    /**
     * Checks whether the hierarchy should be removed because it contains a product that already exists in the database.
     *
     * @param hierarchy        The current hierarchy being processed.
     * @param map              A map containing product entities.
     * @param jobReportEntity  The job report entity for tracking validation failures.
     * @return                 {@code true} if the hierarchy should be removed; {@code false} otherwise.
     */
    private boolean shouldRemoveDueToExistingProduct(Set<String> hierarchy, Map<String, ProductEntity> map, JobReportEntity jobReportEntity) {
        Optional<String> firstExistingProductInDatabase = findFirstExistingProductInDatabase(hierarchy);
        if (firstExistingProductInDatabase.isPresent()) {
            ProductEntity existingEntity = map.get(firstExistingProductInDatabase.get());
            log.warn("Product {} already exists in database; failing entire hierarchy", existingEntity.getId());
            jobReportEntity.getFailedProducts().add(createJobReportProductRefEntityForFailedProduct(existingEntity, PRODUCT_ALREADY_EXISTS_IN_DATABASE));
            hierarchy.remove(existingEntity.getId());
            setUpFailedProducts(hierarchy, map, jobReportEntity, HIERARCHY_HAS_PRODUCT_ALREADY_EXISTS_IN_DATABASE);
            return true;
        }
        return false;
    }

    /**
     * For every product id in the hierarchy, marks it as failed in the job report.
     *
     * @param hierarchy     a set of product ids in the hierarchy
     * @param map           the overall product map
     * @param jobReportEntity the job report to record failures
     * @param msg           the failure message to associate with each product
     */
    private void setUpFailedProducts(Set<String> hierarchy, Map<String, ProductEntity> map, JobReportEntity jobReportEntity, String msg) {
        for (String productId : hierarchy) {
            ProductEntity entity = map.get(productId);
            if (entity != null) {
                jobReportEntity.getFailedProducts().add(createJobReportProductRefEntityForFailedProduct(entity, msg));
            }
        }
    }
    /**
     * Retrieves a list of potential root product ids from the hierarchy.
     * A potential root is defined as having no incoming relationships.
     *
     * @param hierarchy              a set of product ids in the current hierarchy
     * @param incommingRelationshipMap map containing counts of incoming relationships
     * @return a list of product ids that have no incoming relationships
     */
    private List<String> getPotentialRoots(Set<String> hierarchy, Map<String, Integer> incommingRelationshipMap) {
        List<String> potentialRoots = new ArrayList<>();
        for (String productId : hierarchy) {
            Integer count = incommingRelationshipMap.get(productId);
            if (count == null || count == 0) {
                potentialRoots.add(productId);
            }
        }
        return potentialRoots;
    }

    private void setUpSucceededProducts(List<ProductEntity> hierarchy, JobReportEntity jobReportEntity, Map<ObjectId, String> idMap) {
        hierarchy.forEach(productEntity -> jobReportEntity.getSucceededProducts().add(createJobReportProductRefEntityForSucceededProduct(productEntity, idMap.get(new ObjectId(productEntity.getId())))));

    }

    /**
     * Generates a map of product id to ProductEntity from a list of Product DTOs.
     * Also performs preliminary cleanup like removing ROOTPRODUCT relationships and resetting href.
     *
     * @param products the list of Product DTOs to convert
     * @return a map keyed by product id containing the corresponding ProductEntity
     */
    private Map<String, ProductEntity> generateIdMapFromProducts(List<Product> products) {
        // Pre-cleanup: ensure relationships are not null and remove existing ROOTPRODUCT relationships
        products.forEach(product -> {
            if (product.getProductRelationship() == null) {
                product.setProductRelationship(new ArrayList<>());
            }
            product.getProductRelationship().removeIf(relationship ->
                    ProductRelationshipType.ROOTPRODUCT.getValue().equals(relationship.getRelationshipType()));
            product.setHref(null);
        });

        Map<String, ProductEntity> map = products.stream()
                .filter(product -> product.getId() != null)
                .map(productMapper::toEntity)
                .collect(Collectors.toMap(ProductEntity::getId, entity -> entity));
        products.clear();
        log.debug("Cleared original product list after mapping. Map size: {}", map.size());
        return map;
    }

    /**
     * Validates that every product in the hierarchy has valid relationships and at least one of productOffering or productSpecification.
     *
     * @param map             the overall product map
     * @param hierarchy       a set of product ids representing the hierarchy
     * @param jobReportEntity the job report to record failures
     * @return true if the hierarchy is valid; false otherwise
     */
    private boolean isHierarchyRelationshipsAndOfferValid(Map<String, ProductEntity> map, Set<String> hierarchy, JobReportEntity jobReportEntity) {
        String msg = null;
        // Iterate over each product in the hierarchy for validation
        for (String productId : new HashSet<>(hierarchy)) {
            ProductEntity entity = map.get(productId);
            if (entity == null) {
                msg = MISSING_PRODUCT_IN_HIERARCHY;
            } else {
                // Check for relationships pointing to non-existing products
                Optional<ProductRelationshipEntity> missingRelationship = hasRelationshipWithNonExistingProduct(entity, map);
                if (missingRelationship.isPresent()) {
                    ProductRelationshipEntity productRelationshipEntity = missingRelationship.get();
                    String errorMsg = String.format(
                            HAS_S_RELATIONSHIP_WITH_NON_EXISTING_PRODUCT_WITH_ID_S,
                            productRelationshipEntity.getRelationshipType(),
                            Optional.ofNullable(productRelationshipEntity.getProduct())
                                    .map(ProductRefEntity::getId)
                                    .orElse(null)
                    );
                    jobReportEntity.getFailedProducts().add(createJobReportProductRefEntityForFailedProduct(entity, errorMsg));
                    hierarchy.remove(productId);
                    msg = MISSING_PRODUCT_IN_HIERARCHY;
                    log.warn("Product {} has invalid relationship: {}", entity.getId(), errorMsg);
                } else if ((entity.getProductOffering() == null || entity.getProductOffering().getId() == null) &&
                        (entity.getProductSpecification() == null || entity.getProductSpecification().getId() == null)) {
                    // Validate that the product has either a productOffering or productSpecification
                    jobReportEntity.getFailedProducts().add(createJobReportProductRefEntityForFailedProduct(entity, INVALID_PRODUCT_MISSING_PRODUCT_OFFERING_AND_PRODUCT_SPECIFICATION));
                    hierarchy.remove(productId);
                    msg = INVALID_PRODUCT_IN_HIERARCHY;
                    log.warn("Product {} is missing productOffering and productSpecification", entity.getId());
                }
            }

        }
        if (msg != null) {
            // Mark remaining products in the hierarchy as failed with the common error message
            setUpFailedProducts(hierarchy, map, jobReportEntity, msg);
            return false;
        }
        return true;
    }

    /**
     * Handles the generation of new IDs for products in the hierarchy and updates their relationships accordingly.
     *
     * @param hierarchy the list of ProductEntities in the current hierarchy
     * @return
     */
    private Map<ObjectId, String> handleIds(List<ProductEntity> hierarchy) {
        // Map to store the new IDs for each product entity
        Map<String, ObjectId> idMap = new HashMap<>();

        // Generate new IDs and store them in the map
        for (ProductEntity productEntity : hierarchy) {
            ObjectId newId = new ObjectId();
            idMap.put(productEntity.getId(), newId);
        }

        // Update product relationships with the new IDs
        for (ProductEntity entity : hierarchy) {
            entity.getProductRelationship().forEach(productRelationshipEntity -> {
                if (productRelationshipEntity.getProduct() != null) {
                    String oldId = productRelationshipEntity.getProduct().getId().toString();
                    if (idMap.containsKey(oldId)) {
                        productRelationshipEntity.getProduct().setId(idMap.get(oldId));
                    }
                }
            });
            // Set the new ID for the product entity
            entity.setId(idMap.get(entity.getId()).toString());
        }
        log.debug("Updated IDs for hierarchy with {} products", hierarchy.size());
        return reverseMap(idMap);
    }

    /**
     * Creates a JobReportProductRefEntity representing a failed product, including the failure reason.
     *
     * @param entity the product that failed validation
     * @param msg    the failure reason message
     * @return a JobReportProductRefEntity representing the failed product
     */
    private JobReportProductRefEntity createJobReportProductRefEntityForFailedProduct(ProductEntity entity, String msg) {
        return JobReportProductRefEntity.builder()
                .id(entity.getId())
                .externalIdentifier(entity.getId())
                .name(entity.getName())
                .atType(entity.getAtType())
                .failReason(msg)
                .build();
    }

    private JobReportProductRefEntity createJobReportProductRefEntityForSucceededProduct(ProductEntity entity, String externalId) {
        return JobReportProductRefEntity.builder()
                .id(entity.getId())
                .externalIdentifier(externalId)
                .name(entity.getName())
                .atType(entity.getAtType())
                .build();
    }

    /**
     * Finds connected components (i.e. independent product hierarchies) from the product map.
     * Builds a bidirectional graph from the relationships, then uses DFS to find all connected products.
     *
     * @param products a map of product id to ProductEntity
     * @return a list of sets, each set containing product ids of a connected component (hierarchy)
     */
    public List<Set<String>> findConnectedComponents(Map<String, ProductEntity> products) {
        // Step 1: Build an adjacency list representing an undirected graph of products
        Map<String, Set<String>> graph = new HashMap<>();

        for (Map.Entry<String, ProductEntity> entry : products.entrySet()) {
            String productId = entry.getKey();
            graph.putIfAbsent(productId, new HashSet<>());
            ProductEntity product = entry.getValue();
            if (product != null && product.getProductRelationship() != null) {
                for (ProductRelationshipEntity relationship : product.getProductRelationship()) {
                    if (relationship.getProduct() != null) {
                        String relatedProductId = relationship.getProduct().getId().toString();
                        // Add both outgoing and incoming edges to make the graph undirected
                        graph.get(productId).add(relatedProductId);
                        graph.computeIfAbsent(relatedProductId, k -> new HashSet<>()).add(productId);
                    }
                }
            }
        }
        log.debug("Constructed undirected graph with {} nodes", graph.size());

        // Step 2: Run DFS to find connected components
        Set<String> visited = new HashSet<>();
        List<Set<String>> components = new ArrayList<>();

        for (String productId : products.keySet()) {
            if (!visited.contains(productId)) {
                Set<String> component = new HashSet<>();
                dfs(productId, graph, visited, component);
                components.add(component);
                log.debug("Found connected component: {}", component);
            }
        }

        return components;
    }

    /**
     * Depth-first search (DFS) that traverses the undirected graph to collect all connected nodes.
     *
     * @param productId the current product id
     * @param graph     the undirected graph of product relationships
     * @param visited   a set to track visited product ids
     * @param component the set representing the current connected component
     */
    private void dfs(String productId, Map<String, Set<String>> graph, Set<String> visited, Set<String> component) {
        if (visited.contains(productId)) {
            return;
        }

        visited.add(productId);
        component.add(productId);
        // Traverse all neighbors (both directions)
        for (String neighbor : graph.getOrDefault(productId, Collections.emptySet())) {
            dfs(neighbor, graph, visited, component);
        }
    }
}
