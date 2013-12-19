package org.kite.services;

import android.app.Service;

import java.util.concurrent.Executor;

/**
 * A helpful {@link android.app.Service} extension, that can execute
 * commands in separate {@link java.util.concurrent.Executor}. In conjunction
 * with {@link org.kite.annotations.AsyncMethod} can be used to enqueue work
 * in background.
 *
 * @see org.kite.services.MainThreadExecutor
 * @see org.kite.wire.WiredService
 * @author Nikolay Soroka
 */
public abstract class CommandService extends Service implements Executor {

    private Executor workExecutor;

    /**Constructs new {@link org.kite.services.CommandService} with
     * given <code>workExecutor</code> where all invokes of {@link #execute(Runnable)}
     * will be redelivered to.
     *
     * @param executor all invokes of {@link java.util.concurrent.Executor#execute(Runnable)}
     * will be redelivered to it.
     */
    public CommandService(Executor executor) {
        workExecutor = executor;
    }

    /**Shortcat for {@link #CommandService(java.util.concurrent.Executor)} and
     * {@link org.kite.services.MainThreadExecutor} as a parameter.
     *
     */
    public CommandService() {
        this(new MainThreadExecutor());
    }

    /** {@inheritDoc} */
    @Override
    public void execute(Runnable r) {
        workExecutor.execute(r);
    }
}
