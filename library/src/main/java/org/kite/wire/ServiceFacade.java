package org.kite.wire;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class ServiceFacade {

    public static ServiceFacade build(Class<?> service){
        return new ServiceFacade();
    }

    private ServiceFacade() {}
}
