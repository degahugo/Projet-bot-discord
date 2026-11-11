package org.acme.bot.processors.annotations;

import java.lang.annotation.*;

/**
 * The DTOs annotation is used to make the @DTO annotation repeatable.
 * It allows multiple @DTO annotations to be applied to a single field.
 *
 * @see DTO
 */
@Documented
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.SOURCE)
public @interface DTOs {
    DTO[] value();
}
