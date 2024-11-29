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
public @interface SiteRegex {
    String message() default "Site Code invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class SiteValidator implements ConstraintValidator<SiteRegex, String> {
    SiteValidator() {
    }

    public void initialize(SiteRegex customConstarint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        } else {
            String siteRegex = "^[A-Z]{3}\\d{4}$";
            return value.matches(siteRegex);
//            return EnumUtils.isValidEnum(Cron.class, value.trim());
        }
    }
}
