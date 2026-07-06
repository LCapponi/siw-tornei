package it.uniroma3.siw.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotFutureYearValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotFutureYear {

    String message() default "L'anno non può essere nel futuro";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
