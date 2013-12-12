package org.kite.wire;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Main class for providing service connection
 * // TODO detailed javadoc
 *
 * @author Nikolay Soroka
 */
public class Wire {

    private Context context;
    private Object target;
    private Class<? extends WiredService> service;

    private WireCallback callback;
    private WireBinder wireBinder;

    private boolean toInject = true;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO check if service is WireService
            wireBinder = (WireBinder) service;
            serviceInstance = wireBinder.getService();
            if (toInject){
                fillInjection();
            }
            if (callback != null){
                callback.onConnect();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (callback != null){
                callback.onDisconnect();
            }
        }
    };

    private Intent serviceIntent;
    private Map<Class<?>, Field> injectionMap;
    private Map<Class<?>, Method> interfaceMap;
    private WiredService serviceInstance;
    private ServiceFacade serviceFacade;
    private ClientFacade clientFacade;

    private void fillInjection() {
        for (Class<?> injectedType : injectionMap.keySet()){
            if (interfaceMap.containsKey(injectedType)){
                Method method = interfaceMap.get(injectedType);
                try {
                    method.setAccessible(true);
                    Object value = method.invoke(serviceInstance);
                    // interface has async methods - wrap it
                    Field field = injectionMap.get(injectedType);
                    field.setAccessible(true);
                    field.set(target, value);
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
    }

    // builders
    // TODO consider using builder class

    public static Wire with(Context context) {
        return new Wire(context);
    }

    public Wire from(Class<? extends WiredService> service) {
        this.setService(service);
        return this;
    }

    public Wire to(Object object) {
        this.setTarget(object);
        return this;
    }

    public Wire intent(Intent serviceIntent){
        this.serviceIntent = serviceIntent;
        return this;
    }

    public Wire noInection(){
        this.toInject = false;
        return this;
    }

    // lifecycle

    public void connect(){
        Intent serviceIntent = getServiceIntent();
        buildFacadeIfNeeded(serviceIntent);
        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    private void buildFacadeIfNeeded(Intent serviceIntent) {
        if (serviceFacade != null){
            return;
        }
        String intentAction = serviceIntent.getAction();
        Scope scope;
        String action = null;
        if (intentAction != null){
            scope = Scope.ACTION;
            action = intentAction;
        } else {
            scope = Scope.DEFAULT;
        }
        this.serviceFacade = ServiceFacade.build(service, scope, action);
    }

    private Intent getServiceIntent() {
        if (serviceIntent == null){
            serviceIntent = new Intent(context, service);
        }
        return serviceIntent;
    }

    public void disconnect(){
        context.unbindService(connection);
    }

    private Wire(Context context) {
        this.context = context;
    }

    void setTarget(Object target) {
        this.target = target;
        this.clientFacade = ClientFacade.build(target.getClass());
        this.injectionMap = InterfaceFinder.findAllWired(target.getClass());
    }

    void setService(Class<? extends WiredService> service) {
        this.service = service;
    }
}
