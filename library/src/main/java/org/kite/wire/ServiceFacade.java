package org.kite.wire;

import android.app.Service;
import android.util.Log;

import org.kite.annotations.Provided;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class ServiceFacade {

    private static final String TAG = "ServiceFacade";

    public static ServiceFacade build(Class<? extends Service> service, Scope scope, String action) {
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

    private static boolean satisfies(Provided provided, Scope neededScope, String neededAction){
        Scope declaredScope = provided.scope();
        String declaredAction = provided.action();
        boolean forAll = Scope.ALL.equals(declaredScope);
        boolean forDefault = Scope.DEFAULT.equals(neededScope)
                && Scope.DEFAULT.equals(declaredScope);
        boolean forAction = (Scope.ACTION.equals(neededScope)
                && Scope.ACTION.equals(declaredScope)
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

    public Object getValue(Class<?> type, Object instance) {
        Object value = null;
        try {
            if (methods.containsKey(type)) {
                Method method = methods.get(type);

                method.setAccessible(true);
                value = method.invoke(instance);
            } else if (fields.containsKey(type)) {
                Field field = fields.get(type);
                field.setAccessible(true);
                value = field.get(instance);
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Can't access ", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Can't invoke ", e);
        }
        return value;
    }

    private Map<Class<?>, Method> methods = new HashMap<Class<?>, Method>();

    private Map<Class<?>, Field> fields = new HashMap<Class<?>, Field>();

    private ServiceFacade() {
    }
}
