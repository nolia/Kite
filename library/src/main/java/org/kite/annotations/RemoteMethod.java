package org.kite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines that method is asynchronous and all calls to it will be passed
 * through a dynamic proxy instance.
 *
 * @author Nikolay Soroka
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RemoteMethod {
    /**
     * Code of the method to be invoked.
     * Must be unique inside enclosing interface.
     */
    int code();
}
