package org.kite.wire;

import android.os.Binder;

/**
 * {@link Binder} implementation that holds reference to
 * enclosing {@link org.kite.wire.WiredService}.
 *
 * @author Nikolay Soroka
 */
public class WireBinder extends Binder {

    private final WiredService service;

    /**Creates new {@code WireBinder} upon given
     * {@link org.kite.wire.WiredService} instance.
     *
     * @param wiredService
     */
    public WireBinder(WiredService wiredService) {
        service = wiredService;
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


}
