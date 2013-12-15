package org.kite.async;

import java.util.LinkedList;
import java.util.Queue;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class ResultQueue {

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    private boolean isEmpty() {
        return queue.isEmpty();
    }

    public static interface ResultListener {
        void onResultAdded(ResultQueue resultQueue);
    }

    public synchronized void postResult(MethodResult r){
        queue.add(r);
        notifyListener();
    }

    public synchronized MethodResult peekResult(){
        return queue.poll();
    }

    public ResultListener getListener() {
        return listener;
    }

    public void setListener(ResultListener listener) {
        this.listener = listener;
    }

    private ResultListener listener;

    private void notifyListener() {
        if (listener != null){
            listener.onResultAdded(this);
        }
    }

    private Queue<MethodResult> queue = new LinkedList<MethodResult>();
}
