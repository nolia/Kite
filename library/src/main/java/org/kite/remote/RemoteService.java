package org.kite.remote;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class RemoteService extends Service {

    private static final String TAG = "RemoteService";
    public static int MSG_SAY_HELLO = 0x101;

    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handled: " + msg.toString());
            if (msg.what == Remote.CODE_EXECUTE_COMMAND){
                Command command = (Command) msg.obj;
                Log.d(TAG, "Executing command : " + command);
            }
            super.handleMessage(msg);
        }
    }

    public RemoteService(String name){
        super();
        parseSelf();
    }

    private void parseSelf() {
        // TODO parse methods
        incomingHandler = new IncomingHandler();
        messenger = new Messenger(incomingHandler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private IncomingHandler incomingHandler;
    private Messenger messenger;

}
