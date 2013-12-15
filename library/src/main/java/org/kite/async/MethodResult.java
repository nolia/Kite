package org.kite.async;

import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class MethodResult {

    public final int code;
    public final Object result;
    public final Method method;

    public MethodResult(int code, Object result, Method method) {
        this.code = code;
        this.result = result;
        this.method = method;
    }

}
