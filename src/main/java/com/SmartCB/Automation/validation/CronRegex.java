package com.SmartCB.Automation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = {CronValidator.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface CronRegex {
    String message() default "Cron invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class CronValidator implements ConstraintValidator<CronRegex, String> {
    CronValidator() {
    }

    public void initialize(CronRegex customConstarint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        } else {
            String cronRegex = "^([0-5]?\\d)\\s([0-5]?\\d)\\s([01]?\\d|2[0-3])\\s(\\?|\\*|[1-9]|[12]\\d|3[01])\\s(\\*|1[0-2]|0?[1-9])\\s(\\?|\\*|[0-7])\\s(\\*|\\d{4})$";
            return value.matches(cronRegex);
//            return EnumUtils.isValidEnum(Cron.class, value.trim());
        }
    }
}
