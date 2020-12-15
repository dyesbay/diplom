package app.expert.validation;

import app.expert.constants.Platform;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GPlatformValidator implements ConstraintValidator<GPlatform, String> {
    @Override
    public void initialize(GPlatform constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // потому что поле необязательное
        if (value == null) return true;

       return Platform.contains(value);
    }
}
