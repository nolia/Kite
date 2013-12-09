package org.kite.wire;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public abstract class WiredService extends Service {

    private final String serviceName;
    private WireBinder mBinder;
    private ExecutorService executor;

    public WiredService(String name) {
        super();
        this.serviceName = name;
        // TODO using configurator
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return getWireBinder();
    }

    private WireBinder getWireBinder() {
        if (mBinder == null){
            mBinder = new WireBinder(this);
        }
        return mBinder;
    }

    private void init() {
        // TODO collect @Provided annotated members
        executor = Executors.newFixedThreadPool(2);
    }

}
