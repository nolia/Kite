package org.kite.services;

import android.app.Service;

import java.util.concurrent.Executor;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public abstract class CommandService extends Service implements Executor {

    private Executor workExecutor;

    public CommandService(Executor executor) {
        workExecutor = executor;
    }

    public CommandService() {
        this(new MainThreadExecutor());
    }

    @Override
    public void execute(Runnable r) {
        workExecutor.execute(r);
    }
}
