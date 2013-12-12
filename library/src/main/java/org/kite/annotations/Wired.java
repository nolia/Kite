package org.kite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields with this annotation will be filled
 * whet service is connected.
 *
 * @see org.kite.wire.Wire
 * @see org.kite.annotations.Provided
 * @see org.kite.wire.WiredService
 * @author Nikolay Soroka
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Wired {
}
