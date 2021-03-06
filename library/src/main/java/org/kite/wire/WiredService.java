package org.kite.wire;

import android.content.Intent;
import android.os.IBinder;

import org.kite.services.CommandService;

import java.util.concurrent.Executor;

/**WiredService is a provider of dependencies via {@link org.kite.annotations.Provided}
 * annotated methods and fields. All provided dependencies must be unique by class for
 * specified scope. {@code WiredService} is just an abstract {@link android.app.Service} that
 * returns special {@link org.kite.wire.WireBinder} binder instance to use
 * it with {@link Wire} connection mechanism. <br/>
 * <b>Be careful when overriding
 * {@link #onBind(android.content.Intent)} method:
 * if you not return {@code WireBinder} instance - no warranty {@link Wire} will work.
 * </b>
 * @see org.kite.wire.Wire
 * @see org.kite.annotations.Provided
 * @see org.kite.annotations.Wired
 * @author Nikolay Soroka
 */
public abstract class WiredService extends CommandService {

    private static final String TAG = "WiredService";
    private final String serviceName;
    private WireBinder mBinder;

    /**Constructs new {@code WiredService}
     *
     * @param name service name
     */
    public WiredService(String name) {
        super();
        this.serviceName = name;
    }

    public WiredService(Executor executor, String name) {
        super(executor);
        this.serviceName = name;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return getWireBinder();
    }

    /**Return lazy initialized {@link org.kite.wire.WireBinder}
     * associated with this service
     *
     * @return lazy initialized {@link org.kite.wire.WireBinder}
     * associated with this service
     */
    protected final WireBinder getWireBinder() {
        if (mBinder == null){
            mBinder = new WireBinder(this);
        }
        return mBinder;
    }


}
