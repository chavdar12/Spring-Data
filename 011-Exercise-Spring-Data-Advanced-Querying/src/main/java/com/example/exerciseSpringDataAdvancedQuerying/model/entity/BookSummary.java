package com.example.exerciseSpringDataAdvancedQuerying.model.entity;

import java.math.BigDecimal;

/** Projection for task 11 "Reduced Book". */
public interface BookSummary {
    String getTitle();

    EditionType getEditionType();

    AgeRestriction getAgeRestriction();

    BigDecimal getPrice();
}
