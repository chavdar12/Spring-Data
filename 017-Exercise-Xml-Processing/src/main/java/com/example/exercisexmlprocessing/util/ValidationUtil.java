package com.example.exercisexmlprocessing.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

/** Logs why a seed record is skipped. */
@Component
public class ValidationUtil {

    private final Validator validator;

    public ValidationUtil() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public boolean isValid(Object dto) {
        var violations = validator.validate(dto);
        violations.stream().map(ConstraintViolation::getMessage).forEach(m -> System.out.println("Skipped " + dto + ": " + m));
        return violations.isEmpty();
    }
}
