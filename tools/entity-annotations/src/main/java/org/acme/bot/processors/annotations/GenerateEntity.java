package org.acme.bot.processors.annotations;

import java.lang.annotation.*;


/**
 *
 * The Entity annotation is used to specify fiels that should be included in a generated Entity class.
 * A separate Entity class is generated for each unique combination of class and value.
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateEntity {

    /**
     *
     * The name of the Entity to which the annotated field should be added.
     * If empty, the name of the record in lower case is used (Role -> role)
     * @return the name of the Entity
     */
    String value() default "";
}
