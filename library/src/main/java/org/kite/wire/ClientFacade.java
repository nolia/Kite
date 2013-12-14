package org.kite.wire;

import android.util.SparseArray;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * A helper class for injecting values.
 *
 * @author Nikolay Soroka
 */
class ClientFacade {

    private Map<Class<?>, Field> wired;
    private SparseArray<Method> asyncCallbacks;

    public static ClientFacade build(Class<?> targetClass) {
        ClientFacade clientFacade = new ClientFacade();
        clientFacade.wired = InterfaceFinder.findAllWired(targetClass);
        clientFacade.asyncCallbacks = InterfaceFinder.findAsyncCallbacks(targetClass);
        return clientFacade;
    }

    public Set<Class<?>> getWiredClasses(){
        return wired.keySet();
    }

    public SparseArray<Method> getAsyncCallbacks() {
        return asyncCallbacks;
    }

    public void fillWith(Object clientInstance, Class<?> type, Object value){
        Field field = wired.get(type);
        if (field != null){
            field.setAccessible(true);
            try {
                field.set(clientInstance, value);
            } catch (IllegalAccessException e) {
                // log this out
            }
        }

    }

    private ClientFacade(){
    }
}
