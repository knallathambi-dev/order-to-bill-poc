package com.orange.discobole.productcatalog.catalog.util;

import org.springframework.data.mongodb.core.query.Criteria;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class MongoCriteriaBuilder {

    private static final String ROOT_ELEMENT = "ROOT";

    private static final Set<String> ARRAY_FIELDS = Set.of(
            "productOffering", "subCategory", "bundledProductOffering",
            "category", "channel", "marketSegment", "productOfferingPrice",
            "productOfferingRelationship", "relatedParty", "allowedAction"
    );

    public static Criteria build(Map<String, Set<Object>> allParams, Set<String> orFields) {
        Map<String, List<Criteria>> groupedConditions = new HashMap<>();
        List<Criteria> orCriteriaList = new ArrayList<>();

        allParams.forEach((key, values) -> {
            if (values == null || values.isEmpty()) return;

            if (orFields.contains(key)) {
                orCriteriaList.add(createCondition(key, values));
            } else {
                String root = ROOT_ELEMENT;
                String effectiveKey = key;

                if (key.contains(".")) {
                    String potentialRoot = key.substring(0, key.indexOf("."));
                    if (ARRAY_FIELDS.contains(potentialRoot)) {
                        root = potentialRoot;
                        effectiveKey = key.substring(key.indexOf(".") + 1);
                    }
                }
                groupedConditions.computeIfAbsent(root, k -> new ArrayList<>())
                        .add(createCondition(effectiveKey, values));
            }
        });

        return assemble(groupedConditions, orCriteriaList);
    }

    private static Criteria assemble(Map<String, List<Criteria>> grouped, List<Criteria> orList) {
        List<Criteria> andList = new ArrayList<>();

        grouped.forEach((path, conditions) -> {
            if (path.equals(ROOT_ELEMENT)) {
                andList.addAll(conditions);
            } else {
                // Use elemMatch to ensure all conditions match the SAME array element
                Criteria combined = (conditions.size() > 1)
                        ? new Criteria().andOperator(conditions.toArray(new Criteria[0]))
                        : conditions.get(0);
                andList.add(Criteria.where(path).elemMatch(combined));
            }
        });

        if (!orList.isEmpty()) {
            andList.add(new Criteria().orOperator(orList.toArray(new Criteria[0])));
        }

        return andList.isEmpty() ? new Criteria() : new Criteria().andOperator(andList.toArray(new Criteria[0]));
    }

    private static Criteria createCondition(String key, Set<Object> values) {
        if (key.contains("DateTime") || key.contains("lastUpdate") || key.matches(".*(gt|lt|gte|lte|From|To)$")) {
            return buildRangeCriteria(key, values);
        }

        if (key.endsWith(".name") || key.equalsIgnoreCase("name") || key.equalsIgnoreCase("brand")) {
            List<Pattern> patterns = values.stream()
                    .map(v -> Pattern.compile(Pattern.quote(String.valueOf(v)), Pattern.CASE_INSENSITIVE))
                    .toList();
            return Criteria.where(key).in(patterns);
        }

        return Criteria.where(key).in(values);
    }

    private static Criteria buildRangeCriteria(String key, Set<Object> values) {
        Object val = values.iterator().next();
        Object processedValue = (val instanceof String s) ? tryParseDate(s) : val;

        String dbField = key.replaceAll("(gt|lt|gte|lte|From|To)$", "");
        if (key.contains("lastUpdate")) dbField = "lastUpdate";

        if (key.endsWith("gt")) return Criteria.where(dbField).gt(processedValue);
        if (key.endsWith("lt")) return Criteria.where(dbField).lt(processedValue);
        if (key.endsWith("gte") || key.contains("startDateTime") || key.endsWith("From"))
            return Criteria.where(dbField).gte(processedValue);
        if (key.endsWith("lte") || key.contains("endDateTime") || key.endsWith("To"))
            return Criteria.where(dbField).lte(processedValue);

        return Criteria.where(dbField).is(processedValue);
    }

    private static Object tryParseDate(String s) {
        try { return OffsetDateTime.parse(s); } catch (Exception e) { return s; }
    }
}