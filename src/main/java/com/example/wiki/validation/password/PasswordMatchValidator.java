package com.example.wiki.validation.password;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private static final Logger logger = LoggerFactory.getLogger(PasswordMatchValidator.class);

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    private void buildFor(String fieldName, ConstraintValidatorContext context) {
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            final BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
            final Object firstValue = beanWrapper.getPropertyValue(firstFieldName);
            final Object secondValue = beanWrapper.getPropertyValue(secondFieldName);

            final boolean isValid = Objects.equals(firstValue, secondValue);

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                buildFor(firstFieldName, context);
                buildFor(secondFieldName, context);
            }

            return isValid;

        } catch (Exception e) {
            logger.warn("PasswordMatch validation error for fields [{}] and [{}]: {}",
                    firstFieldName, secondFieldName, e.getMessage());
            return true;
        }
    }
}
