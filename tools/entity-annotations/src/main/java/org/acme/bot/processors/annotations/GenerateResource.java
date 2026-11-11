package org.acme.bot.processors.annotations;

import java.lang.annotation.*;


/**
 *
 * The Resource annotation is used on a record already annotated with @GenerateService.
 * A REST Resource is generated with the CRUD endpoints, on the path /api/v1/{table} :
 * GET (all), GET /{key}, POST, DELETE /{key}
 * The table is the value of @GenerateEntity and the key is the field annotated with @Unique.
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateResource {
}