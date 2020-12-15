package app.expert.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StringListValidator.class)
@Target( {ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringListValidation {
    String message() default "List shouldn't be empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
