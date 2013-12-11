package org.kite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
@Target({ElementType.METHOD}) // TODO add field type
@Retention(RetentionPolicy.RUNTIME)
public @interface Provided {


}
