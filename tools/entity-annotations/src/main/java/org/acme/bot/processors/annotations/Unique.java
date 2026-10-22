package org.acme.bot.processors.annotations;

import java.lang.annotation.*;


/**
 *
 * The Unique annotation is used to specify a field of the record that must be unique in the database.
 * The generated field will have @Column(unique = true, nullable = false)
 *
 */
@Documented
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.SOURCE)
public @interface Unique {
}
