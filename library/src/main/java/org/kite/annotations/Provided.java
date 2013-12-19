package org.kite.annotations;

import org.kite.async.AsyncType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that method or field of {@link org.kite.wire.WiredService} is
 * available after connection to dependency injection.<br/>
 * <b>Note, that if you mark field with it - you have to worry about it's initialization,
 * by the moment service is connected<br/>
 * If you mark method with it - it has to be getter-like, with no parameters.
 * </b>
 * <p>Async:</p>
 * By setting {@link #async()} type of exposing this method, you set
 * the behaviour of calling interface, marked with this annotation. Examine {@link org.kite.async.AsyncType}
 * for better understanding. Default async value is {@link org.kite.async.AsyncType#NONE}
 *
 * @see org.kite.async.AsyncType
 * @see org.kite.annotations.Wired
 * @see org.kite.wire.WiredService
 * @author Nikolay Soroka
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Provided {

    /**
     * Indicates scope to use with {@link Provided}
     *
     * @author Nikolay Soroka
     */
    public static enum Scope {
        /**Means that method or field will be available,
         * only when connecting without specifying {@link android.content.Intent}
         * {@code Action}.
         *
         */
        DEFAULT,

        /**Properties with this scope type will be available
         * where or not you have specified intent and action.
         *
         */
        ALL,

        /**Properties will be available only when you specify
         * {@link android.content.Intent} and it's action equals
         * to {@link org.kite.annotations.Provided#action()}
         *
         */
        ACTION
    }

    /**Specify the scope of method or field.<br/>
     * Default is {@link org.kite.annotations.Provided.Scope#DEFAULT}
     *
     */
    Scope scope() default Scope.DEFAULT;

    /**The action to match when checking intent scope.
     * Note, it's only used if {@link #scope()} is {@link Scope#ACTION}
     *
     */
    String action() default "";

    /**Marks that asynchronous calls
     * to this interface.
     * @see org.kite.annotations.AsyncMethod
     */
    AsyncType async() default AsyncType.NONE;
}
