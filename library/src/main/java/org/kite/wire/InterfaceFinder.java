package org.kite.wire;

import android.app.Service;
import android.util.SparseArray;

import org.kite.annotations.RemoteMethod;
import org.kite.annotations.Provided;
import org.kite.annotations.Wired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Helpers for finding annotated members.
 *
 * @author Nikolay Soroka
 */
class InterfaceFinder {
    public static Map<Class<?>, Method> findAllProvidedMethods(Class<? extends Service> service) {
        Map<Class<?>, Method> result = new HashMap<Class<?>, Method>();
        Method[] declaredMethods = service.getDeclaredMethods();
        for (Method method : declaredMethods) {
            Provided provided = method.getAnnotation(Provided.class);
            if (provided != null) {
                // check if method has no params
                if (method.getParameterTypes().length != 0) {
                    throw new IllegalArgumentException("Method " + method + " must have no parameters");
                }
                // method must be not static
                if (Modifier.isStatic( method.getModifiers() )){
                    throw new IllegalArgumentException("Method " + method + " must be not static");
                }

                Class<?> clazz = method.getReturnType();
                result.put(clazz, method);
            }
        }
        return result;
    }

    public static Map<Class<?>, Field> findAllProvidedFields(Class<? extends Service> service) {
        Map<Class<?>, Field> result = new HashMap<Class<?>, Field>();
        Field[] declaredFields = service.getDeclaredFields();
        for (Field field : declaredFields){
            Provided provided = field.getAnnotation(Provided.class);
            if (provided != null){
                // method must be not static
                if (Modifier.isStatic(field.getModifiers())){
                    throw new IllegalArgumentException("Provided field must be not static. ");
                }
                 Class<?> clazz = field.getType();
                result.put(clazz, field);
            }
        }
        return result;
    }

    public static Map<Class<?>, Field> findAllWired(Class<?> target) {
        Map<Class<?>, Field> result = new HashMap<Class<?>, Field>();
        Field[] declaredFields = target.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getAnnotation(Wired.class) != null) {
                field.setAccessible(true);
                result.put(field.getType(), field);
            }
        }
        return result;
    }


}
