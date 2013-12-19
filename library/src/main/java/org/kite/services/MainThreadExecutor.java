package org.kite.services;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Executes commands in main thread.
 *
 * @author Nikolay Soroka
 */
public class MainThreadExecutor implements Executor {

    private final Handler handler;

    /**Constructs new <code>MainThreadExecutor</code>.
     */
    public MainThreadExecutor(){
        this.handler = new Handler(Looper.getMainLooper());
    }

    /** {@inheritDoc} */
    @Override
    public void execute(Runnable command) {
        handler.post(command);
    }
}
