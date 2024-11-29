package com.SmartCB.Automation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.apache.commons.lang3.EnumUtils;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = {GenderValidator.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface GenderRegex {
    String message() default "Gender invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class GenderValidator implements ConstraintValidator<GenderRegex, String> {
    GenderValidator() {
    }

    public void initialize(GenderRegex customConstarint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        } else {
            return true;
//            return EnumUtils.isValidEnum(Gender.class, value.trim());
        }
    }
}
