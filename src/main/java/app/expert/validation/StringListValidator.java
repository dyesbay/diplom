package app.expert.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class StringListValidator implements ConstraintValidator<StringListValidation, List<String>> {

    @Override
    public void initialize(StringListValidation listValidation) {}

    @Override
    public boolean isValid(List<String> objects, ConstraintValidatorContext context) {
        return objects.stream().allMatch(nef -> nef != null && !nef.trim().isEmpty()) && objects.size() > 0;
    }
}
