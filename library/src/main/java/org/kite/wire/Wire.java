package org.kite.wire;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.kite.annotations.Provided;

import java.util.Set;

/**
 * Main class for providing connection to local services.<br/>
 * It connects {@code WiredService} exposed dependencies, annotated
 * with {@code Provided} to client's {@code Wired} properties.
 *
 * @see org.kite.annotations.Provided
 * @see org.kite.annotations.Wired
 * @see org.kite.wire.WiredService
 * @author Nikolay Soroka
 */
public class Wire {

    // builders

    /**Constructs a new {@link org.kite.wire.Wire} instance
     * upon given {@link android.content.Context}.
     *
     * @param context
     * @return a newly created {@link org.kite.wire.Wire} instance
     * upon given {@link android.content.Context}.
     */
    public static Wire with(Context context) {
        return new Wire(context);
    }

    /**Sets the service to connect to. This is same as call
     * {@link #from(android.content.Intent)} with intent preset
     * with service class.
     *
     * @param service to connect to
     * @return this {@code Wire} instance
     */
    public Wire from(Class<? extends WiredService> service) {
        serviceClass = service;
        serviceIntent = new Intent(context, service);
        return this;
    }

    /**Sets the service to connect to. Note, if nothing corresponds to
     * given intent config, nothing will happen, and no fields will be injected
     * in target.<br/>
     * If you want to use direct connection, use {@link #from(Class)}
     *
     * @param serviceIntent intent for binding to a service
     * @return this {@code Wire} instance
     */
    public Wire from(Intent serviceIntent) {
        this.serviceIntent = serviceIntent;
        return this;
    }

    /**Sets the target, that will be connected to service.
     * @param object target, must not be {@code null}.
     * @throws java.lang.NullPointerException if object is {@code null}
     * @return this {@code Wire} instance
     */
    public Wire to(Object object) {
        if (object == null){
            throw new NullPointerException("Target  must not be null");
        }
        this.setTarget(object);
        return this;
    }

    /**Sets to not perform injection, when service is connected.
     * You can specify callback, by calling {@link #setCallback(WireCallback)},
     * and it will be the same as using {@link android.content.ServiceConnection}
     *
     * @return this {@code Wire} instance
     */
    public Wire noInection() {
        this.toInject = false;
        return this;
    }

    // getters & setters

    /**Sets the callback, to notify when service is connected and
     * disconnected. Whether perform injection or not, callback will
     * be notified when service is connected and disconnected.
     *
     * @param callback
     */
    public void setCallback(WireCallback callback) {
        this.callback = callback;
    }

    /**Returns {@link ServiceFacade} associated with service, which is
     * connected. Note, that it's only valid when service has successfully been
     * connected. You can call this method if you want to avoid injection, but
     * also want to use advantage of {@link org.kite.wire.WiredService} and
     * {@link org.kite.annotations.Provided} annotations.
     *
     * @see #noInection()
     * @return {@link ServiceFacade} associated with service, which is
     * connected.
     */
    public ServiceFacade getServiceFacade() {
        return this.serviceFacade;
    }

    // lifecycle

    /**
     * Connects to a service.
     * This is same as {@link android.content.Context#bindService(android.content.Intent, android.content.ServiceConnection, int)}
     * (and it's actually is using it ).
     */
    public void connect() {
        Intent serviceIntent = getServiceIntent();
        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Disconnects from current service connection.
     */
    public void disconnect() {
        context.unbindService(connection);
    }

    // private logic

    private void fillInjection() {
        Set<Class<?>> wiredClasses = clientFacade.getWiredClasses();
        for (Class<?> key : wiredClasses) {
            Object value = serviceFacade.getValue(key, serviceInstance);
            // TODO perform wrap on async
            clientFacade.fillWith(target, key, value);
        }
    }

    private void nullify() {
        for (Class key : clientFacade.getWiredClasses()) {
            clientFacade.fillWith(target, key, null);
        }
    }


    private void buildFacadeIfNeeded(Intent serviceIntent) {
        if (serviceFacade != null) {
            return;
        }
        String intentAction = serviceIntent.getAction();
        Provided.Scope scope;
        String action = null;
        if (intentAction != null) {
            scope = Provided.Scope.ACTION;
            action = intentAction;
        } else {
            scope = Provided.Scope.DEFAULT;
        }
        this.serviceFacade = ServiceFacade.build(serviceClass, scope, action);
    }

    private Intent getServiceIntent() {
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, serviceClass);
        }
        return serviceIntent;
    }

    private void setTarget(Object target) {
        this.target = target;
        this.clientFacade = ClientFacade.build(target.getClass());
    }

    private Wire(Context context) {
        this.context = context;
    }


    private Context context;
    private Object target;

    private Class<? extends WiredService> serviceClass;

    private WireCallback callback;

    private boolean toInject = true;

    private Intent serviceIntent;
    private WiredService serviceInstance;
    private ServiceFacade serviceFacade;

    private ClientFacade clientFacade;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WireBinder wireBinder = (WireBinder) service;
            serviceInstance = wireBinder.getService();
            Wire.this.serviceClass = serviceInstance.getClass();
            buildFacadeIfNeeded(serviceIntent);
            if (toInject) {
                fillInjection();
            }
            if (callback != null) {
                callback.onConnect(name, service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (toInject) {
                nullify();
            }
            if (callback != null) {
                callback.onDisconnect();
            }
        }
    };
}
