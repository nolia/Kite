package org.kite.wire;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class ClientFacade {

    private Map<Class<?>, Field> wired;

    public static ClientFacade build(Class<? extends Object> targetClass) {
        ClientFacade clientFacade = new ClientFacade();
        clientFacade.wired = InterfaceFinder.findAllWired(targetClass);

        return clientFacade;
    }

    public Set<Class<?>> getWiredClasses(){
        return wired.keySet();
    }

    public void fillWith(Object clientInstance, Class<?> type, Object value){
        Field field = wired.get(type);
        if (field != null){
            field.setAccessible(true);
            try {
                field.set(clientInstance, value);
            } catch (IllegalAccessException e) {

            }
        }

    }

    private ClientFacade(){
    }
}
