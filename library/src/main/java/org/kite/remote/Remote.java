package org.kite.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class Remote {

    public static final int CODE_EXECUTE_COMMAND = 0x101;

    public static interface Callback {
        void onConnected(Remote remote);

        void onDissconnected(Remote remote);
    }

    private final Context context;
    private Intent remoteService;
    private Object target;
    private Callback callback;
    private Messenger serviceMessenger;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            if (callback != null){
                callback.onConnected(Remote.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (callback != null){
                callback.onDissconnected(Remote.this);
            }
            serviceMessenger = null;
        }
    };

    public Remote(Context context){
        this.context = context;
    }

    public Remote from(Intent remoteService){
        this.remoteService = remoteService;
        return this;
    }

    public Remote to(Object target){
        this.target = target;
        return this;
    }

    public Remote callback(Callback callback){
        this.callback = callback;
        return this;
    }

    public void postCommand(Command command){
        if (command == null){
            throw new IllegalArgumentException("Command must not be null");
        }
        Message msg = Message.obtain();
        msg.what = CODE_EXECUTE_COMMAND;
        msg.obj = command;
        msg.replyTo = serviceMessenger;
        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            // FIXME handle exception
        }
    }

    public void connect(){
        context.bindService(remoteService, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void disconnect(){
        context.unbindService(serviceConnection);
    }
}
