package org.kite.async;

/**Type of asynchronous invocation of interfaces,
 * marked with {@link org.kite.annotations.Provided} annotation.
 * <p>Can be:</p>
 * <ul>
 *     <li>{@link #NONE} - calls will be synchronous</li>
 *     <li>{@link #ALL} - all methods will be called asynchronously</li>
 *     <li>{@link #METHODS} - only methods marked with AsyncMethod
 *     will be called asynchronously.
 *     </li>
 * </ul>
 *
 */
public enum AsyncType {
    /**No methods of this interface will be wrapped into
     * async calls, whether or not given interface
     * has methods with AsyncMethod annotation.
     *
     */
    NONE,

    /**Calls to every method of interface
     * with this type of async invocation will be
     * called asynchronously, whether or not they
     * are marked with AsyncMethod annoation
     */
    ALL,

    /**Only methods with marked with AsyncMethod
     * annotation will be called on a separate thread.
     *
     */
    METHODS
}
