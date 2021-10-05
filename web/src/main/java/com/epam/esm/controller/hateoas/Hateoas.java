package com.epam.esm.controller.hateoas;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Mark a controller that needs HATEOAS support.
 *
 * @author Dmitry Karnyshov
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hateoas {

}
