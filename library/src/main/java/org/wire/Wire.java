package org.wire;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Main class for providing service connection
 * // TODO detailed javadoc
 *
 * @author Nikolay Soroka
 */
public class Wire {

    private Context context;
    private Object target;
    private Class<? extends Service> service;

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

    private void fillInjection() {
        for (Class<?> injectedType : injectionMap.keySet()){
            if (interfaceMap.containsKey(injectedType)){
                Method method = interfaceMap.get(injectedType);
                try {
                    method.setAccessible(true);
                    Object value = method.invoke(serviceInstance);
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
        context.bindService(getServiceIntent(), connection, Context.BIND_AUTO_CREATE);
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
        this.injectionMap = InterfaceFinder.findAllWired(target.getClass());
    }

    void setService(Class<? extends Service> service) {
        this.service = service;
        this.interfaceMap = InterfaceFinder.findAllProvided(service);
    }
}