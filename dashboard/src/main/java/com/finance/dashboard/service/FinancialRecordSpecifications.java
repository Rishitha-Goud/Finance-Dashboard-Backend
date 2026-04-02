package com.finance.dashboard.service;

import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.model.entity.FinancialRecord;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public final class FinancialRecordSpecifications {

    private FinancialRecordSpecifications() {
    }

    public static Specification<FinancialRecord> withFilters(
            RecordType type,
            String category,
            LocalDate from,
            LocalDate to
    ) {
        return Specification.allOf(
                hasType(type),
                hasCategory(category),
                hasDateOnOrAfter(from),
                hasDateOnOrBefore(to)
        );
    }

    private static Specification<FinancialRecord> hasType(RecordType type) {
        return (root, query, criteriaBuilder) ->
                type == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("type"), type);
    }

    private static Specification<FinancialRecord> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                category == null || category.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.trim().toLowerCase());
    }

    private static Specification<FinancialRecord> hasDateOnOrAfter(LocalDate from) {
        return (root, query, criteriaBuilder) ->
                from == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("date"), from);
    }

    private static Specification<FinancialRecord> hasDateOnOrBefore(LocalDate to) {
        return (root, query, criteriaBuilder) ->
                to == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("date"), to);
    }
}
