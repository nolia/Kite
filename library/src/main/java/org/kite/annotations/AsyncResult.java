package org.kite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Callbacks to {@link org.kite.annotations.AsyncMethod}
 * that will be called by given {@link AsyncMethod#value()} code.
 *
 * @see org.kite.annotations.Provided
 * @author Nikolay Soroka
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AsyncResult {
    /**Code for callback, corresponding
     * to <code>AsyncMethod</code> that was
     * invoked.
     *
     */
    int value();
}
