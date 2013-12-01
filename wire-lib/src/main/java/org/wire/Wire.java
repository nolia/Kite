package org.wire;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            wireBinder = (WireBinder) service;

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
    }

    void setService(Class<? extends WiredService> service) {
        this.service = service;
    }
}
