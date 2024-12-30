package com.desafio.account.payable.infrastructure.config;

import com.desafio.account.payable.interfaces.util.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EnumValueValidator implements ConstraintValidator<EnumValue, Enum<?>> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant == value) {
                return true;
            }
        }
        return false;
    }
}
