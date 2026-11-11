package org.acme.bot.processors.annotations;

import java.lang.annotation.*;

/**
 * The DTO annotation is used to specify fields that should be included in a generated DTO class.
 * A separate DTO class is generated for each unique combination of class and value.
 */
@Documented
@Target(ElementType.RECORD_COMPONENT)
@Repeatable(DTOs.class)
@Retention(RetentionPolicy.SOURCE)
public @interface DTO {
    /**
     * The name of the DTO to which the annotated field should be added.
     *
     * @return the name of the DTO
     */
    String value();
}