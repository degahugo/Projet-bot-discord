package org.acme.bot.processors.annotations;

import java.lang.annotation.*;


/**
 *
 * The Service annotation is used on a record already annotated with
 * @GenerateEntity, @GenerateRepository and @GenerateMapper.
 * A Service is generated with the CRUD use cases : getAll, getByXxx, create, delete.
 * The record must have exactly one field annotated with @Unique : it is the business key (Role -> name).
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateService {
}