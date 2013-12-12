package org.kite.wire;

import android.content.ComponentName;
import android.os.IBinder;

/**
 * This callback interface is to use with {@link Wire}.
 * It will be notified each time service is connected and disconnected
 * like {@link android.content.ServiceConnection}
 *
 * @author Nikolay Soroka
 */
public interface WireCallback {
    /**Called when service is connected.
     *
     * @param name
     * @param service
     */
    void onConnect(ComponentName name, IBinder service);

    /**Called when service is disconnected.
     *
     */
    void onDisconnect();
}
