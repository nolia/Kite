package org.kite.annotations;

import org.kite.wire.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
@Target({ElementType.METHOD, ElementType.FIELD}) // TODO add field type
@Retention(RetentionPolicy.RUNTIME)
public @interface Provided {

    Scope scope() default Scope.DEFAULT;

    String action() default "";

}
