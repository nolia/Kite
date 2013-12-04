package org.kite.wire;

import android.app.Service;

import org.kite.annotations.Provided;
import org.kite.annotations.Wired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class InterfaceFinder {
    public static Map<Class<?>, Method> findAllProvided(Class<? extends Service> service) {
        Map<Class<?>, Method> result = new HashMap<Class<?>, Method>();
        Method[] declaredMethods = service.getDeclaredMethods();
        for (Method method : declaredMethods){
            Provided provided = method.getAnnotation(Provided.class);
            if (provided != null){
                // check if method has no params
                if (method.getParameterTypes().length != 0){
                    throw new IllegalArgumentException("Method " + method + " must have no parameters");
                }

                Class<?> clazz = method.getReturnType();
                result.put(clazz, method);
            }
        }
        return result;
    }

    public static Map<Class<?>, Field> findAllWired(Class<?> target){
        Map<Class<?>, Field> result = new HashMap<Class<?>, Field>();
        Field[] declaredFields = target.getDeclaredFields();
        for (Field field : declaredFields){
            if (field.getAnnotation(Wired.class) != null){
                field.setAccessible(true);
                result.put(field.getType(), field);
            }
        }
        return result;
    }
}
