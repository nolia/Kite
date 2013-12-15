package org.kite.wire;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import org.kite.annotations.Provided;
import org.kite.async.MethodResult;
import org.kite.async.ResultQueue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    /**
     * TODO
     *
     * @author Nikolay Soroka
     */
    public static class ConnectionPair extends Pair<Class<? extends WiredService>, Class<?>> {

        /**
         * Constructor for a Pair.
         *
         * @param first  the first object in the Pair
         * @param second the second object in the pair
         */
        public ConnectionPair(Class<? extends WiredService> first, Class<?> second) {
            super(first, second);
        }
    }

    private static final String TAG = "Wire";

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
    public Wire noInjection() {
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
     * @see #noInjection()
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
        resultQueue.setListener(null);
        context.unbindService(connection);
    }

    // private logic

    private void fillInjection() {
        Set<Class<?>> wiredClasses = clientFacade.getWiredClasses();
        for (Class<?> key : wiredClasses) {
            Object value = serviceFacade.getValue(key, serviceInstance, resultQueue);
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

    private void callbackAsync(MethodResult methodResult){
        if (clientFacade != null && methodResult.code != 0){
            Method callback = clientFacade.getAsyncCallbacks().get(methodResult.code);
            if (callback == null){
                return;
            }
            try {
                callback.setAccessible(true);
                callback.invoke(target, methodResult.result);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Can't access", e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "Can't invoke", e);
            }
        }
    }


    private Context context;
    private Object target;

    private Class<? extends WiredService> serviceClass;

    private WireCallback callback;

    private boolean toInject = true;

    private Intent serviceIntent;

    private WiredService serviceInstance;
    private ResultQueue resultQueue;

    private ServiceFacade serviceFacade;
    private ClientFacade clientFacade;

    private ResultQueue.ResultListener resultQueueListener = new ResultQueue.ResultListener() {
        @Override
        public void onResultAdded(ResultQueue resultQueue) {
            MethodResult methodResult = resultQueue.peekResult();
            if (methodResult != null){
                callbackAsync(methodResult);
            }
        }
    };
    private ConnectionPair connectionPair;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WireBinder wireBinder = (WireBinder) service;
            serviceInstance = wireBinder.getService();
            try {
                serviceClass = (Class<? extends WiredService>) Class.forName(name.getClassName());
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Service class not found", e);
            } catch (ClassCastException e){
                final String msg = "Class is not a WiredService";
                Log.e(TAG, msg, e);
                throw new IllegalArgumentException(msg);
            }
            buildFacadeIfNeeded(serviceIntent);
            connectionPair = new ConnectionPair(serviceClass, target.getClass());
            resultQueue = wireBinder.getResultQueue(connectionPair);
            resultQueue.setListener(resultQueueListener);
            if (toInject) {
                fillInjection();
            }
            deliverPendingResults();
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

    private void deliverPendingResults() {
        if (resultQueue != null){
            while (resultQueue.isNotEmpty()){
                callbackAsync(resultQueue.peekResult());
            }
        }
    }


}
