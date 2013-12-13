package org.kite.services;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class MainThreadExecutor implements Executor {

    private final Handler handler;

    public MainThreadExecutor(){
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(Runnable command) {
        handler.post(command);
    }
}
