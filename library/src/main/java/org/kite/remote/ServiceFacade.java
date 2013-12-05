package org.kite.remote;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class ServiceFacade {

    private Map<Class<?>, Field> injectionMap;
    private Map<Class<?>, Method> interfaceMap;

    public Map<Class<?>, Method> getInterfaceMap() {
        return interfaceMap;
    }

    public void setInterfaceMap(Map<Class<?>, Method> interfaceMap) {
        this.interfaceMap = interfaceMap;
    }

    public Map<Class<?>, Field> getInjectionMap() {
        return injectionMap;
    }

    public void setInjectionMap(Map<Class<?>, Field> injectionMap) {
        this.injectionMap = injectionMap;
    }
}
