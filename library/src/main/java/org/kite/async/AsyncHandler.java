package org.kite.async;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.kite.annotations.AsyncMethod;
import org.kite.services.CommandService;
import org.kite.wire.WiredService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class AsyncHandler implements InvocationHandler {

    public static interface AsyncListener {

        void onAsyncResult(Method method, Object result);

        void onAsyncCodeResult(int code, Method method, Object result);

        void onAsyncError(Method method);

    }

    private static final String TAG = "AsyncHandler";
    private final Object origin;
    private Object proxy;
    private final CommandService service;

    private HashMap<Method, Integer> asyncMethods;
    private AsyncListener listener;

    private final Handler hadler;

    public static AsyncHandler wrapAll(Object origin, Class<?> type, WiredService instance) {
        AsyncHandler handler = new AsyncHandler(origin, instance, AsyncType.ALL, type);
        handler.proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, handler);
        return handler;
    }

    public static AsyncHandler wrapMethods(Object origin, Class<?> type, WiredService instance) {
        AsyncHandler handler = new AsyncHandler(origin, instance, AsyncType.METHODS, type);
        handler.proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, handler);
        return handler;
    }

    public AsyncListener getListener() {
        return listener;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setListener(AsyncListener listener) {
        this.listener = listener;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        if (asyncMethods.containsKey(method)) {
            // async call:
            final int code = asyncMethods.get(method);
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        method.setAccessible(true);
                        Object result = method.invoke(origin, args);
                        deliverResult(code, result, method);
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "Can't access : ", e);
                        notifyError(method);
                    } catch (InvocationTargetException e) {
                        Log.e(TAG, "Can't invoke : ", e);
                        notifyError(method);
                    }
                }
            });
            return null;
        } else {
            // sync call
            return method.invoke(origin, args);
        }
    }

    private void notifyError(Method method) {
        if (listener != null) {
            listener.onAsyncError(method);
        }
    }

    private void deliverResult(final int code, final Object result, final Method method) {
        // notify listener
        if (listener != null) {
            hadler.post(new Runnable() {
                @Override
                public void run() {
                    if (code != 0) {
                        listener.onAsyncCodeResult(code, method, result);
                    } else {
                        listener.onAsyncResult(method, result);
                    }
                }
            });
        }
    }

    private AsyncHandler(Object origin, CommandService instance, AsyncType asyncType, Class<?> type) {
        this.origin = origin;
        this.service = instance;
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
}
