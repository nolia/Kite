package org.wire;

import android.os.Binder;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class WireBinder extends Binder {

    private WiredService service;

    public WireBinder(WiredService wiredService) {
        service = wiredService;
    }
}
