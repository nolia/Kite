package org.wire;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
@Target({ElementType.FIELD}) // TODO add getter target
@Retention(RetentionPolicy.RUNTIME)
public @interface Wired {
}
