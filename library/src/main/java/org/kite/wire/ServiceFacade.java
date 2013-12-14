package org.kite.wire;

import android.app.Service;
import android.util.Log;

import org.kite.annotations.Provided;
import org.kite.async.AsyncHandler;
import org.kite.async.AsyncType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates all provided fields and methods by {@link org.kite.wire.WiredService}
 * with {@link org.kite.annotations.Provided} annotation.
 *
 * @author Nikolay Soroka
 */
public class ServiceFacade {

    private static final String TAG = "ServiceFacade";
    /**Constructs new {@code ServiceFacade} upon given service, scope and intent action.
     *
     * @param service
     * @param scope
     * @param action
     * @return new {@code ServiceFacade} built upon given service, scope and intent action.
     */
    public static ServiceFacade build(Class<? extends Service> service, Provided.Scope scope, String action) {
        ServiceFacade serviceFacade = new ServiceFacade();
        // check all methods
        Map<Class<?>, Method> allMethods = InterfaceFinder.findAllProvidedMethods(service);
        for (Class<?> key : allMethods.keySet()) {
            Method method = allMethods.get(key);
            // findAllProvidedMethods insures that method has provided annotation
            Provided provided = method.getAnnotation(Provided.class);
            if (satisfies(provided, scope, action)){
                putOrThrow(serviceFacade, key, method);
            }
        }
        // check all fields
        Map<Class<?>, Field> allFields = InterfaceFinder.findAllProvidedFields(service);
        for (Class<?> key : allFields.keySet()) {
            Field field = allFields.get(key);
            Provided provided = field.getAnnotation(Provided.class);
            if (satisfies(provided, scope, action)){
                putOrThrow(serviceFacade, key, field);
            }
        }

        return serviceFacade;
    }

    /**Returns the provided value of given {@code type} from given
     * {@code instance} to use in injection.
     *
     * @param type
     * @param instance
     * @return the value of given {@code type} from given
     * {@code instance}.
     */
    public Object getValue(Class<?> type, WiredService instance) {
        Object value = null;
        AsyncType async = null;
        try {
            if (methods.containsKey(type)) {
                Method method = methods.get(type);
                method.setAccessible(true);
                async = method.getAnnotation(Provided.class).async();
                value = method.invoke(instance);
            } else if (fields.containsKey(type)) {
                Field field = fields.get(type);
                field.setAccessible(true);
                async = field.getAnnotation(Provided.class).async();
                value = field.get(instance);
            } else { // was not found
                return null;
            }
            if ( !AsyncType.NONE.equals(async) ){
                value = wrapAsync(value, async, type, instance);
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Can't access ", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Can't invoke ", e);
        }
        return value;
    }

    public void setAsyncListener(AsyncHandler.AsyncListener asyncListener) {
        this.asyncListener = asyncListener;
    }

    public AsyncHandler.AsyncListener getAsyncListener() {
        return asyncListener;
    }

    private Object wrapAsync(Object value, AsyncType async, Class<?> type, WiredService instance) {
        if (AsyncType.NONE.equals(async)){
            return value;
        }
        Object result = value;
        AsyncHandler handler = null;
        if ( AsyncType.ALL.equals(async)){
            handler = AsyncHandler.wrapAll(value, type, instance);
        } else if (AsyncType.METHODS.equals(async)){
            handler = AsyncHandler.wrapMethods(value, type, instance);
        }
        handler.setListener(asyncListener);
        result = handler.getProxy();
        return result;
    }

    private static boolean satisfies(Provided provided, Provided.Scope neededScope, String neededAction){
        Provided.Scope declaredScope = provided.scope();
        String declaredAction = provided.action();
        boolean forAll = Provided.Scope.ALL.equals(declaredScope);
        boolean forDefault = Provided.Scope.DEFAULT.equals(neededScope)
                && Provided.Scope.DEFAULT.equals(declaredScope);
        boolean forAction = (Provided.Scope.ACTION.equals(neededScope)
                && Provided.Scope.ACTION.equals(declaredScope)
                && declaredAction.equals(neededAction));
        return forAll || forDefault || forAction;

    }

    private static void putOrThrow(ServiceFacade serviceFacade, Class<?> key, Field field) {
        if (serviceFacade.contains(key)){
            throw new IllegalArgumentException("Service must contain only one class for each scope");
        }
        serviceFacade.putValue(key, field);
    }

    private static void putOrThrow(ServiceFacade serviceFacade, Class<?> key, Method method) {
        if (serviceFacade.contains(key)) {
            throw new IllegalArgumentException("Service must contain only one class for each scope");
        }
        serviceFacade.putValue(key, method);
    }

    private void putValue(Class<?> key, Field field) {
        fields.put(key, field);
    }

    private void putValue(Class<?> key, Method method) {
        methods.put(key, method);
    }

    private boolean contains(Class<?> key) {
        return methods.containsKey(key) || fields.containsKey(key);
    }

    private Map<Class<?>, Method> methods = new HashMap<Class<?>, Method>();

    private Map<Class<?>, Field> fields = new HashMap<Class<?>, Field>();

    private AsyncHandler.AsyncListener asyncListener;

    private ServiceFacade() {
    }
}
