package org.acme.bot.processors.annotations;

import java.lang.annotation.*;

/**
 *
 *
 * The repository annotation is used on a record already annoted with @GenerateEntity
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateRepository {
}
