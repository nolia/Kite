package org.kite.wire;

import android.os.Binder;
import android.util.Log;

import org.kite.async.ResultQueue;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link Binder} implementation that holds reference to
 * enclosing {@link org.kite.wire.WiredService}.<br/>
 * It also holds result queue of async method invocation.
 *
 * @author Nikolay Soroka
 */
public class WireBinder extends Binder {

    private static final String TAG = "WireBinder";
    private final WiredService service;

    private Map<Wire.ConnectionPair, ResultQueue> resultQueueMap = new HashMap<Wire.ConnectionPair, ResultQueue>();

    /**Creates new {@code WireBinder} upon given
     * {@link org.kite.wire.WiredService} instance.
     *
     * @param wiredService
     */
    public WireBinder(WiredService wiredService) {
        service = wiredService;
        Log.d(TAG, "new WireBinder");
    }

    /**Return {@link org.kite.wire.WiredService} which
     * this binder is associated with.
     *
     * @return {@link org.kite.wire.WiredService} which
     * this binder is associated with.
     */
    public WiredService getService() {
        return service;
    }

    /**Return ResultQueue which async method results will be
     * delivered to.
     *
     * @param key connection key between {@link org.kite.wire.WiredService} and
     * target object
     * @return ResultQueue which async method results will be
     * delivered to.
     */
    public ResultQueue getResultQueue(Wire.ConnectionPair key) {
        ResultQueue resultQueue = resultQueueMap.get(key);
        if (resultQueue == null){
            Log.d(TAG, "new ResultQueue");
            resultQueue = new ResultQueue();
            resultQueueMap.put(key, resultQueue);
        }
        return resultQueue;
    }
}
