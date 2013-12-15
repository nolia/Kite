package org.kite.async;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.kite.annotations.AsyncMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.Executor;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class AsyncHandler implements InvocationHandler {


    private ResultQueue resultQueue;

    private static final String TAG = "AsyncHandler";
    private final Object origin;
    private Object proxy;
    private final Executor executor;

    private HashMap<Method, Integer> asyncMethods;

    private final Handler hadler;

    public static AsyncHandler wrapAll(Object origin, Class<?> type, Executor executor) {
        AsyncHandler handler = new AsyncHandler(origin, executor, AsyncType.ALL, type);
        handler.proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, handler);
        return handler;
    }

    public static AsyncHandler wrapMethods(Object origin, Class<?> type, Executor executor) {
        AsyncHandler handler = new AsyncHandler(origin, executor, AsyncType.METHODS, type);
        handler.proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, handler);
        return handler;
    }

    public Object getProxy() {
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        if (asyncMethods.containsKey(method)) {
            // async call:
            final int code = asyncMethods.get(method);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        method.setAccessible(true);
                        Object result = method.invoke(origin, args);
                        deliverResult(code, result, method);
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "Can't access : ", e);
                    } catch (InvocationTargetException e) {
                        Log.e(TAG, "Can't invoke : ", e);
                    }
                }
            });
            return null;
        } else {
            // sync call
            return method.invoke(origin, args);
        }
    }


    private void deliverResult(final int code, final Object result, final Method method) {
        hadler.post(new Runnable() {
            @Override
            public void run() {
                MethodResult mr = new MethodResult(code, result, method);
                if (resultQueue != null){
                    resultQueue.postResult(mr);
                }
            }
        });
    }


    private AsyncHandler(Object origin, Executor executor, AsyncType asyncType, Class<?> type) {
        this.origin = origin;
        this.executor = executor;
        Looper looper = Looper.myLooper();
        // FIXME handle if looper is null
        this.hadler = new Handler(looper);
        gatherMethods(asyncType, type);
    }

    private void gatherMethods(AsyncType asyncType, Class<?> type) {
        asyncMethods = new HashMap<Method, Integer>();
        for (Method method : type.getDeclaredMethods()) {
            AsyncMethod asyncMethod = method.getAnnotation(AsyncMethod.class);
            int code = 0;
            boolean isAsync = asyncMethod != null;
            if (isAsync) {
                code = asyncMethod.value();
            }
            if (AsyncType.ALL.equals(asyncType)) {
                asyncMethods.put(method, code);
            } else if (isAsync) {
                asyncMethods.put(method, code);
            }
        }
    }

    public void setResultQueue(ResultQueue resultQueue) {
        this.resultQueue = resultQueue;
    }
}
