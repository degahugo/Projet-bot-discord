package org.acme.bot.processors.annotations;

import java.lang.annotation.*;


/**
 *
 * The Mapper annotation is used on a record already annotated with @GenerateEntity,
 * and with @DTO("DTO") and @DTO("CreateRequest") on its fields.
 * A Mapper is generated to transform data between the layers :
 * CreateRequest -> Model -> Entity and Entity -> Model -> DTO
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateMapper {
}