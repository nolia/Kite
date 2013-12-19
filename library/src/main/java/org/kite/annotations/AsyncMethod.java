package org.kite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods marked with this annotation, will be called asynchronously, if {@link Provided#async()}
 * instance has {@link org.kite.async.AsyncType#METHODS} or {@link org.kite.async.AsyncType#ALL} type.
 *
 * @see org.kite.annotations.Provided
 * @see org.kite.annotations.AsyncResult
 * @author Nikolay Soroka
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AsyncMethod {
    /**
     * Code of the given async method.
     * Must be unique inside declaring interface.
     * <code>AsyncResult</code> callback methods
     * will be called by this code.
     *
     */
    int value() default 0;
}
